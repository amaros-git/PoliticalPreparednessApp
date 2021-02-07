package com.example.android.politicalpreparedness.election

import android.app.Application
import android.content.*
import android.util.Log
import androidx.core.content.edit
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
import kotlinx.coroutines.Dispatchers

class VoterInfoViewModel(
        private val electionId: Int,
        private val app: Application,
        private val repository: ApplicationRepository) : BaseViewModel(app) {

    private val TAG = VoterInfoViewModel::class.java.simpleName

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    val openUrlEvent = SingleLiveEvent<String>()

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


   /* fun checkFollowStatus(electionId: Int) {
        if
        viewModelScope.launch {

        }
        _isFollowed.value = if (followed.isNullOrEmpty()) {
            false
        } else {
            followed.contains(electionId.toString())
        }
    }*/

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

    /*fun followElection(electionId: Int) {
        //get current set
        val currentFollowed = sharedPref.getStringSet(FOLLOWED_ELECTIONS_PREFERENCES, emptySet())
        currentFollowed?.let {
            //add new id to the list with current followed
            val list = it.toMutableList()
            list.add(electionId.toString())
            //save new followed set
            val newFollowed = list.toSet()
            sharedPref.edit {
                remove(FOLLOWED_ELECTIONS_PREFERENCES)
                putStringSet(FOLLOWED_ELECTIONS_PREFERENCES, newFollowed)
            }
        }
        _isFollowed.value = true
    }

    fun unfollowElection(electionId: Int) {
        //get current set
        val currentFollowed = sharedPref.getStringSet(FOLLOWED_ELECTIONS_PREFERENCES, emptySet())
        currentFollowed?.let {
            //remove election id from the list with current followed
            val list = it.toMutableList()
            list.remove(electionId.toString())
            //save new followed set
            val newFollowed = list.toSet()
            sharedPref.edit {
                remove(FOLLOWED_ELECTIONS_PREFERENCES)
                putStringSet(FOLLOWED_ELECTIONS_PREFERENCES, newFollowed)
            }
        }
        _isFollowed.value = false
    }*/

    fun getVoterInfo(electionId: Int, division: Division) {
        viewModelScope.launch {
            val result = repository.getVoterInfo(
                    electionId,
                    division.state + " " + division.country
            )
            if (result is Result.Success) {
                _voterInfo.postValue(result.data)
            } else {
                Log.e("VoterInfoViewModel", (result as Result.Error).message)
                showErrorMessage.postValue(result.message)
            }
        }
    }


    fun openUrl(url: String?) {
        url?.let {
            openUrlEvent.value = it
        }
    }

    //TODO: Add live data to hold voter info


    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}