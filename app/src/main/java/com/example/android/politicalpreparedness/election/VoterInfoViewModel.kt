package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.database.ElectionDao
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val repository: ApplicationRepository) : ViewModel() {



    fun getVoterInfo(electionId: Long, address: String, officialOnly: Boolean = false) {
        viewModelScope.launch {
            repository.getVoterInfo(electionId, address, officialOnly)
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