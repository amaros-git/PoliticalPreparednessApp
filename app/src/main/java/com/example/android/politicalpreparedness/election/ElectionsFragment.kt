package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.database.elections.ElectionDatabase
import com.example.android.politicalpreparedness.data.database.LocalDataSource
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeDatabase
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.utils.setDisplayHomeAsUpEnabled
import com.example.android.politicalpreparedness.utils.setTitle


class ElectionsFragment : BaseFragment() {

    private lateinit var binding: FragmentElectionBinding

    private lateinit var upcomingListAdapter: ElectionListAdapter

    private lateinit var savedListAdapter: ElectionListAdapter


    override val _viewModel by viewModels<ElectionsViewModel> {
        ElectionsViewModelFactory(
                requireActivity().application,
                ApplicationRepository(
                        LocalDataSource(
                                ElectionDatabase.getInstance(requireContext()),
                                RepresentativeDatabase.getInstance(requireContext())
                        ),
                        CivicsApi
                )
        )
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentElectionBinding.inflate(inflater)
        binding.viewModel = _viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        setTitle(getString(R.string.elections_title))
        setDisplayHomeAsUpEnabled(true)

        _viewModel.openVoterInfoEvent.observe(viewLifecycleOwner) { election ->
            findNavController().navigate(
                    ElectionsFragmentDirections
                            .actionElectionsFragmentToVoterInfoFragment(election)
            )
        }

        setupListAdapter()

        _viewModel.upcomingElections.observe(viewLifecycleOwner) { elections ->
            upcomingListAdapter.submitMyList(elections)
        }

        _viewModel.savedElections.observe(viewLifecycleOwner) { elections ->
            savedListAdapter.submitMyList(elections)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _viewModel.refreshUpcomingElections()
    }

    private fun setupListAdapter() {
        upcomingListAdapter = ElectionListAdapter(_viewModel)
        binding.upcomingElections.adapter = upcomingListAdapter

        savedListAdapter = ElectionListAdapter(_viewModel)
        binding.savedElections.adapter = savedListAdapter
    }

}