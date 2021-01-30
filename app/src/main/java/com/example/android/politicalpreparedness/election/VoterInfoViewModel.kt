package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.database.ElectionDao
import com.example.android.politicalpreparedness.data.network.models.VoterInfoResponse
import kotlinx.coroutines.launch
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.network.models.Division

class VoterInfoViewModel(private val repository: ApplicationRepository) : BaseViewModel() {

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
    get() = _voterInfo

    fun getVoterInfo(electionId: Int, division: Division) {
        viewModelScope.launch {
            val result = repository.getVoterInfo(
                    electionId,
                    division.state + " " + division.country
            )
            if (result is Result.Success) {
                _voterInfo.value = result.data
            } else {
                Log.e("VoterInfoViewModel", "${(result as Result.Error).message}")
                showErrorMessage.value = (result as Result.Error).message
            }
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