package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.ApplicationRepository
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(private val repository: ApplicationRepository): ViewModel() {

    //TODO: Create live data val for upcoming elections
    val upcomingElections = repository.observeTasks()

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

    fun refreshUpcomingElections() {
        viewModelScope.launch {
            repository.refreshElections()
        }
    }

}