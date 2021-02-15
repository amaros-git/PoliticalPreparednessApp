package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.models.Election
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import com.example.android.politicalpreparedness.utils.convertExceptionToToastString
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

    //filter out elections with isFollowed == true else null
    val savedElections: LiveData<List<Election>?> = repository.observeElections().map { result ->
        savedElectionsMapper(result)
    }

    val openVoterInfoEvent = SingleLiveEvent<Election>()


    private fun savedElectionsMapper(result: Result<List<Election>>) =
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

    fun refreshUpcomingElections() {
        showLoading.value = true
        viewModelScope.launch() {
            try {
                repository.refreshElections()
                showLoading.value = false
            } catch (e: Exception) {
                showLoading.value = false
                showErrorMessage.value =
                        convertExceptionToToastString(app.applicationContext, e)
            }
        }
    }

    fun openVoterInfo(election: Election) {
        openVoterInfoEvent.value = election
    }

}