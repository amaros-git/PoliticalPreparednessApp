package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(private val repository: ApplicationRepository): BaseViewModel() {

    //TODO: Create live data val for upcoming elections
    val upcomingElections: LiveData<List<Election>?> = repository.observeElections().map {
        if (it is Result.Success) {
            it.data
        } else {
            null
        }
    }

    val openElectionDetailsEvent = SingleLiveEvent<Election>()


    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

    fun refreshUpcomingElections() {
        viewModelScope.launch {
            repository.refreshElections()
        }
    }

    fun openElectionDetails(election: Election) {
        openElectionDetailsEvent.value = election
    }

}