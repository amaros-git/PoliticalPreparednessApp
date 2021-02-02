package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class ElectionsViewModel(
        private val app: Application,
        private val repository: ApplicationRepository) : BaseViewModel(app) {

    val upcomingElections: LiveData<List<Election>?> = repository.observeElections().map {
        if (it is Result.Success) {
            it.data
        } else {
            null
        }
    }

    //filter out elections with isFollowed == true
    val savedElections: LiveData<List<Election>?> = repository.observeElections().map { result ->
        if (result is Result.Success) {
            val followed = mutableListOf<Election>()
            result.data.forEach {
                if (it.isFollowed) {
                    followed.add(it)
                }
            }
            followed
        } else {
            null
        }
    }

    val openVoterInfoEvent = SingleLiveEvent<Election>()


    fun refreshUpcomingElections() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.refreshElections()
            } catch (e: Exception) {
                //TODO
            }
        }
    }

    fun openVoterInfo(election: Election) {
        openVoterInfoEvent.value = election
    }

}