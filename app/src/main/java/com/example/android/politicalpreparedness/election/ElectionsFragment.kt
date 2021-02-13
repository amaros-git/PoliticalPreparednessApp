package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.database.ElectionDatabase
import com.example.android.politicalpreparedness.data.database.LocalDataSource
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.utils.setDisplayHomeAsUpEnabled
import com.example.android.politicalpreparedness.utils.setTitle

/*viewModel.upcomingElections.observe(viewLifecycleOwner) { result ->
            Log.d(TAG, "Observed elections: ")
            (result as Result.Success).data.forEach {
                Log.d(TAG, it.toString())
            }
        }

        viewModel.refreshUpcomingElections()*/


//TODO: Add ViewModel values and create ViewModel

//TODO: Add binding values

//TODO: Link elections to voter info

//TODO: Initiate recycler adapters

//TODO: Populate recycler adapters


class ElectionsFragment : Fragment() {

    private val TAG = ElectionsFragment::class.java.simpleName

    private lateinit var binding: FragmentElectionBinding

    private lateinit var upcomingListAdapter: ElectionListAdapter

    private lateinit var savedListAdapter: ElectionListAdapter


    private val viewModel by viewModels<ElectionsViewModel> {
        ElectionsViewModelFactory(
                requireActivity().application,
                ApplicationRepository(
                        LocalDataSource(ElectionDatabase.getInstance(requireContext())),
                        CivicsApi
                )
        )
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentElectionBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        setTitle(getString(R.string.elections_title))
        setDisplayHomeAsUpEnabled(true)

        viewModel.openVoterInfoEvent.observe(viewLifecycleOwner) { election ->
            findNavController().navigate(
                    ElectionsFragmentDirections
                            .actionElectionsFragmentToVoterInfoFragment(election)
            )
        }

        setupListAdapter()

        viewModel.upcomingElections.observe(viewLifecycleOwner) { elections ->
            upcomingListAdapter.submitMyList(elections)
        }

        viewModel.savedElections.observe(viewLifecycleOwner) { elections ->
            savedListAdapter.submitMyList(elections)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshUpcomingElections()
    }

    private fun setupListAdapter() {
        upcomingListAdapter = ElectionListAdapter(viewModel)
        binding.upcomingElections.adapter = upcomingListAdapter

        savedListAdapter = ElectionListAdapter(viewModel)
        binding.savedElections.adapter = savedListAdapter
    }

}