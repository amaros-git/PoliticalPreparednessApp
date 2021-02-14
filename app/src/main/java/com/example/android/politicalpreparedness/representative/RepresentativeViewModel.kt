package com.example.android.politicalpreparedness.representative

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeCache
import com.example.android.politicalpreparedness.data.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.utils.convertExceptionToToastString
import kotlinx.coroutines.launch
import java.util.*

class RepresentativeViewModel(
        private val app: Application,
        private val repository: ApplicationRepository) : BaseViewModel(app), LocationListener {

    private val TAG = RepresentativeViewModel::class.java.simpleName

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    val cachedRepresentatives: LiveData<List<RepresentativeCache>?> = repository.observerRepresentatives().map {
        if (it is Result.Success) {
            it.data
        } else {
            null
        }
    }

    private var locationManager: LocationManager? = null

    private val _locationAddress = MutableLiveData<Address?>()
    val locationAddress: LiveData<Address?>
        get() = _locationAddress


    fun getRepresentative(address: Address) {
        showLoading.value = true
        viewModelScope.launch {
            try {
                tryToGetRepresentative(address)
                showLoading.value = false
            } catch(e: Exception) {
                showLoading.value = false
                showErrorMessage.postValue(
                        convertExceptionToToastString(app.applicationContext, e)
                )
            }
        }
    }

    private suspend fun tryToGetRepresentative(address: Address) {
        val result = repository.getRepresentatives(address.toFormattedString())
        if (result is Result.Success) {
            _representatives.value = result.data
        } else {
            showErrorMessage.value = (result as Result.Error).message
        }
    }

    @SuppressLint("MissingPermission")
    fun startAddressLocation() {
        locationManager =
                app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1f, this)
    }

    override fun onLocationChanged(location: Location) {
        try {
            val address = geoCodeLocation(location)
            _locationAddress.value = address
            //we need location only once
            locationManager?.removeUpdates(this)
        } catch (e: Exception) {
            Log.d(TAG, "Exception on GPS occurred")
            e.printStackTrace()
            showErrorMessage.value = "Please enable Location services"
        }
    }

    /**
     * @throws IOException if gps disabled. ALso if internet is disabled too
     */
    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(app.applicationContext, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    override fun onCleared() {
        super.onCleared()
        locationManager?.removeUpdates(this)
    }

}
