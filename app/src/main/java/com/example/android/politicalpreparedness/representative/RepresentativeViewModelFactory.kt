package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.representative.model.Representative

@Suppress("UNCHECKED_CAST")
class RepresentativeViewModelFactory(
        private val app: Application,
        private val repository: ApplicationRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
            (RepresentativeViewModel(app, repository) as T)
}
