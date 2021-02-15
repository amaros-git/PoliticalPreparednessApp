package com.example.android.politicalpreparedness.representative

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeCacheDataItem
import com.example.android.politicalpreparedness.data.models.*
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

    private var locationManager: LocationManager? = null

    private val _locationAddress = MutableLiveData<Address?>()
    val locationAddress: LiveData<Address?>
        get() = _locationAddress

    //is used to refresh recycler view from fragment
    var lastAddress: Address? = null


    fun getRepresentative(address: Address, shouldRefresh: Boolean = false) {
        lastAddress = address

        viewModelScope.launch {
            val result = repository.getRepresentatives(address.city + address.state)
            if (result is Result.Success) {
                val representatives = mutableListOf<Representative>()
                result.data.representatives.forEach {
                    representatives.add(convertCacheItemToRepresentative(it))
                }

                _representatives.value = representatives
            }
        }

        if (shouldRefresh) {
            refreshRepresentativesFromNetwork(address)
        }
    }

    private fun convertCacheItemToRepresentative(
            representativeCacheItem: RepresentativeCacheDataItem
    ) =
            Representative(
                    createOfficial(representativeCacheItem),
                    createOffice(representativeCacheItem)
            )

    private fun createListOfChannels(cacheItem: RepresentativeCacheDataItem): List<Channel>? {
        val channels = mutableListOf<Channel>()
        if (null != cacheItem.facebookId) {
            channels.add(Channel("Facebook", cacheItem.facebookId))
        }
        if (null != cacheItem.twitterId) {
            channels.add(Channel("Twitter", cacheItem.twitterId))
        }

        return if (channels.isEmpty()) null else channels
    }

    private fun createOfficial(
            representativeCacheItem: RepresentativeCacheDataItem
    ): Official {
        val urls = if (representativeCacheItem.wwwUrl.isNullOrBlank()) {
            null
        } else {
            listOf(representativeCacheItem.wwwUrl)
        }
        val channels = createListOfChannels(representativeCacheItem)

        return Official(
                representativeCacheItem.name,
                null,
                representativeCacheItem.partyName,
                null,
                urls,
                representativeCacheItem.photoUrl,
                channels)
    }

    private fun createOffice(representativeCacheItem: RepresentativeCacheDataItem) =
            Office(
                    representativeCacheItem.name,
                    Division(
                            representativeCacheItem.divisionId,
                            representativeCacheItem.cityState,
                            representativeCacheItem.cityState
                    ),
                    emptyList()
            )


    private fun refreshRepresentativesFromNetwork(address: Address) {
        showLoading.value = true
        viewModelScope.launch {
            try {
                val result = tryToGetRepresentative(address)
                if (result is Result.Success) {
                    refreshRepresentativesCache(result.data, address)
                }
                showLoading.value = false
            } catch (e: Exception) {
                showLoading.value = false
                showErrorMessage.postValue(
                        convertExceptionToToastString(app.applicationContext, e)
                )
            }
        }
    }

    private fun refreshRepresentativesCache(
            representatives: List<Representative>,
            address: Address
    ) {
        viewModelScope.launch {
            repository.clearRepresentativeCache(address)
            repository.refreshRepresentativesCache(representatives, address)
        }
    }

    private suspend fun tryToGetRepresentative(address: Address): Result<List<Representative>> {
        val result = repository.refreshRepresentativesFromNetwork(address.toFormattedString())
        if (result is Result.Success) {
            _representatives.value = result.data
        } else {
            showErrorMessage.value = (result as Result.Error).message
        }

        return result
    }

    @SuppressLint("MissingPermission")
    fun startAddressLocation() {
        locationManager =
                app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1f, this)
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

    override fun onLocationChanged(location: Location) {
        try {
            val address = geoCodeLocation(location)
            _locationAddress.value = address
            //we need location only once
            locationManager?.removeUpdates(this)
        } catch (e: Exception) {
            val message = when (e) {
                is NullPointerException -> {
                    app.applicationContext.getString(R.string.wrong_location)
                }
                else -> {
                    app.applicationContext.getString(R.string.enable_location_services)
                }
            }
            Log.d(TAG, "Exception on GPS occurred")
            e.printStackTrace()
            showErrorMessage.value = message
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.d(TAG, "onStatusChanged")
    }

}
