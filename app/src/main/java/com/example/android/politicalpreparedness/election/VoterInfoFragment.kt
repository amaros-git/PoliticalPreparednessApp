package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.database.ElectionDatabase
import com.example.android.politicalpreparedness.data.database.LocalDataSource
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.utils.setTitle

class VoterInfoFragment : BaseFragment() {

    private val TAG = VoterInfoFragment::class.java.simpleName

    override val _viewModel by viewModels<VoterInfoViewModel> {
        VoterInfoViewModelFactory(ApplicationRepository(
                LocalDataSource(ElectionDatabase.getInstance(requireContext())),
                CivicsApi)
        )
    }

    private lateinit var election: Election

    private val args: VoterInfoFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = FragmentVoterInfoBinding.inflate(inflater)

        binding.lifecycleOwner = viewLifecycleOwner

        election = args.argElection

        _viewModel.voterInfo.observe(viewLifecycleOwner) { response ->
            response?.let { it ->
                it.state?.forEach { state ->
                    Log.d(TAG, "Voting URL ${state.electionAdministrationBody.votingLocationFinderUrl}")
                    Log.d(TAG, "Ballot URL ${state.electionAdministrationBody.ballotInfoUrl}")
                }
            }
        }

        setTitle(election.name)

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        _viewModel.getVoterInfo(election.id, election.division)
    }

    //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */


        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks

    //TODO: Create method to load URL intents

}