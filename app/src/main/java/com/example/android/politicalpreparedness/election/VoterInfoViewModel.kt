package com.example.android.politicalpreparedness.election

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.network.models.VoterInfoResponse
import kotlinx.coroutines.launch
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.network.models.Division
import com.example.android.politicalpreparedness.utils.SingleLiveEvent

class VoterInfoViewModel(
        private val electionId: Int,
        private val app: Application,
        private val repository: ApplicationRepository) : BaseViewModel(app) {

    private val TAG = VoterInfoViewModel::class.java.simpleName

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _isFollowed = MutableLiveData<Boolean>() //TODO rework to map and filter by id
    val isFollowed: LiveData<Boolean>
        get() = _isFollowed

    init {
        viewModelScope.launch {
            val result = repository.getElection(electionId)
            if (result is Result.Success) {
                _isFollowed.value = result.data.isFollowed
            } else {
                _isFollowed.value = false
            }
        }

    }

    fun follow(electionId: Int) {
        changeFollowingStatus(electionId, true)

        _isFollowed.value = true
    }

    fun unfollow(electionId: Int) {
        changeFollowingStatus(electionId, false)

        _isFollowed.value = false
    }

    private fun changeFollowingStatus(electionId: Int, shouldFollow: Boolean) {
        viewModelScope.launch {
            repository.changeFollowingStatus(electionId, shouldFollow)
        }
    }

    fun getVoterInfo(electionId: Int, division: Division) {
        showLoading.value = true
        viewModelScope.launch {
            val result = repository.getVoterInfo(
                    electionId,
                    division.state + " " + division.country
            )
            showLoading.value = false

            if (result is Result.Success) {
                _voterInfo.postValue(result.data)
            } else {
                Log.e("VoterInfoViewModel", (result as Result.Error).message)
                showErrorMessage.postValue(result.message)
            }
        }
    }

}