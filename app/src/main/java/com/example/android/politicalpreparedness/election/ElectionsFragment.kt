package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.database.ElectionDatabase
import com.example.android.politicalpreparedness.data.database.LocalDataSource
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ElectionsFragment: Fragment() {

    private val TAG = ElectionsFragment::class.java.simpleName

    //TODO: Declare ViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = FragmentElectionBinding.inflate(inflater)

        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters

        //TODO: Populate recycler adapters

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val repository = ApplicationRepository(LocalDataSource(ElectionDatabase.getInstance(requireContext())), CivicsApi)
        GlobalScope.launch {
            try {
                repository.refreshElections()
            }catch (e:Exception) {

            }
        }




    }

    //TODO: Refresh adapters when fragment loads

}