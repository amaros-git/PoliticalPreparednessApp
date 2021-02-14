package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.models.VoterInfoResponse
import kotlinx.coroutines.launch
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.models.Division
import com.example.android.politicalpreparedness.utils.convertExceptionToToastString
import kotlinx.coroutines.Dispatchers

class VoterInfoViewModel(
        private val electionId: Int,
        private val app: Application,
        private val repository: ApplicationRepository) : BaseViewModel(app) {

    private val TAG = VoterInfoViewModel::class.java.simpleName

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _isFollowed = MutableLiveData<Boolean>()
    val isFollowed: LiveData<Boolean>
        get() = _isFollowed

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getElection(electionId)
            if (result is Result.Success) {
                _isFollowed.postValue(result.data.isFollowed)
            } else {
                _isFollowed.postValue(false)
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                tryToGetVoterInfo(electionId, division)
                showLoading.postValue(false)
            } catch (e: Exception) {
                showLoading.postValue(false)
                showErrorMessage.postValue(
                        convertExceptionToToastString(app.applicationContext, e)
                )
            }
        }
    }

    private suspend fun tryToGetVoterInfo(electionId: Int, division: Division) {
        val result = repository.getVoterInfo(
                electionId,
                division.state + " " + division.country
        )

        if (result is Result.Success) {
            _voterInfo.postValue(result.data)
        } else {
            Log.e(TAG, (result as Result.Error).message)
            showErrorMessage.postValue(
                    app.applicationContext.getString(R.string.wrong_response)
            )
        }
    }

}