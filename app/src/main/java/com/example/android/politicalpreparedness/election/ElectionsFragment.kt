package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.ApplicationRepository
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.data.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.data.network.models.Division
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

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
        val repository = ApplicationRepository(CivicsApi)
        GlobalScope.launch {
            val result = repository.getElections()
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "Received elections: ${result.data}")

                   /* val data = """{"elections": [
                        {
                            "name": "VIP Test Election",
                            "electionDay": "2021-06-06",
                            "ocdDivisionId": "ocd-division/country:us"
                        }
                    ]}"""*/

                    val data = """
                        {
                            "id": "2000",
                            "name": "VIP Test Election",
                            "electionDay": "2021-06-06",
                            "ocdDivisionId": "ocd-division/country:us"
                        }"""

                    val moshi = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            //.add(Division::class, ElectionAdapter())
                            //.add(Date::class.java, DateJsonAdapter)
                            .build()

                    val adapter: JsonAdapter<Election> = moshi.adapter(Election::class.java)
                    adapter.fromJson(data)
                    Log.d(TAG, "election = ${adapter.fromJson(data)}")

                /*result.data.forEach {
                        Log.d(TAG, it.toString())
                    }*/
                }
                is Result.Error -> {
                    Log.d(TAG, "Error: ${result.message}")
                }
            }
        }



    }

    //TODO: Refresh adapters when fragment loads

}