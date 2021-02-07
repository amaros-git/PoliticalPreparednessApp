package com.example.android.politicalpreparedness.representative

import android.app.Application
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

class RepresentativeViewModel(
        private val app: Application,
        private val repository: ApplicationRepository): BaseViewModel(app) {

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _currentAddress = MutableLiveData<Address?>()
    val currentAddress: LiveData<Address?>
        get() = _currentAddress


    fun getRepresentative(address: Address) {
        showLoading.value = true
        viewModelScope.launch {
            val result = repository.getRepresentatives(address.toFormattedString())
            showLoading.value = false
            if (result is Result.Success) {
                result.data.forEach {
                    Log.d("TEST", it.toString())
                }
                _representatives.value = result.data
            }
            else {
                showErrorMessage.value = (result as Result.Error).message
            }
        }
    }

    fun setAddress(address: Address) {
        _currentAddress.value = address
    }
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
