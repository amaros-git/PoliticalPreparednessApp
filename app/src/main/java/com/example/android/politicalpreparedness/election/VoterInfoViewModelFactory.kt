package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.ApplicationRepository

@Suppress("UNCHECKED_CAST")
class VoterInfoViewModelFactory(
        private val repository: ApplicationRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
            (VoterInfoViewModel(repository) as T)

}
