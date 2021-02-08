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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.network.models.Address
import com.example.android.politicalpreparedness.data.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import java.util.*

class RepresentativeViewModel(
        private val app: Application,
        private val repository: ApplicationRepository) : BaseViewModel(app), LocationListener {

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private var locationManager: LocationManager? = null

    private val _locationAddress = MutableLiveData<Address?>()
    val locationAddress: LiveData<Address?>
        get() = _locationAddress


    fun getRepresentative(address: Address) {
        showLoading.value = true
        viewModelScope.launch {
            val result = repository.getRepresentatives(address.toFormattedString())
            showLoading.value = false
            if (result is Result.Success) {
                _representatives.value = result.data
            } else {
                showErrorMessage.value = (result as Result.Error).message
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startAddressLocation() {
        locationManager =
                app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1f, this)
    }

    override fun onLocationChanged(location: Location) {
        val address = geoCodeLocation(location)

        _locationAddress.value = address

        //we need location only once
        locationManager?.removeUpdates(this)
    }


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
/*
    fun setAddress(address: Address) {
        _currentAddress.value = address
    }*/
    //TODO: Establish live data for representatives and address

    //TODO: Create function to fetch representatives from API from a provided address

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields

}
