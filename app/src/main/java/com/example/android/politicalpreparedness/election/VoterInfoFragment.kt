package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.database.ElectionDatabase
import com.example.android.politicalpreparedness.data.database.LocalDataSource
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : BaseFragment() {

    private val TAG = VoterInfoFragment::class.java.simpleName

    override val _viewModel by viewModels<VoterInfoViewModel> {
        VoterInfoViewModelFactory(ApplicationRepository(
                LocalDataSource(ElectionDatabase.getInstance(requireContext())),
                CivicsApi)
        )
    }

    private val args: VoterInfoFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        Log.d(TAG, "ID = ${args.argElectionId}, Division = ${args.argDivision}")

        _viewModel.voterInfo.observe(viewLifecycleOwner) {
            it?.let {
                Log.d(TAG, "Voter info = $it")
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        _viewModel.getVoterInfo(args.argElectionId, args.argDivision)
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