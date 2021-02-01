package com.example.android.politicalpreparedness.election

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.utils.setTitle


class VoterInfoFragment : BaseFragment() {

    private val TAG = VoterInfoFragment::class.java.simpleName

    override val _viewModel by viewModels<VoterInfoViewModel> {
        VoterInfoViewModelFactory(
                args.argElection.id,
                requireActivity().application,
                ApplicationRepository(
                        LocalDataSource(ElectionDatabase.getInstance(requireContext())),
                        CivicsApi
                )
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

        setTitle(election.name)

        binding.viewModel = _viewModel
        binding.election = election

        _viewModel.voterInfo.observe(viewLifecycleOwner) { response ->
            response?.let { voterInfo ->
                binding.address =
                        voterInfo.state?.get(0)?.electionAdministrationBody?.correspondenceAddress

                // not sure how it should be handled the case when more than one state on the list,
                // I will use the first element
                if (!voterInfo.state.isNullOrEmpty()) {
                    val votingUrl = voterInfo.state[0].electionAdministrationBody.votingLocationFinderUrl
                    val ballotUrl = voterInfo.state[0].electionAdministrationBody.ballotInfoUrl

                    Log.d(TAG, "Voting URL $votingUrl")
                    Log.d(TAG, "Ballot URL $ballotUrl")

                    binding.voterUrl = votingUrl
                    binding.ballotUrl = ballotUrl
                }
            }
        }

        _viewModel.openUrlEvent.observe(viewLifecycleOwner) { url ->
            try {
                val intent = Intent("android.intent.action.MAIN")
                intent.component = ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main")
                intent.addCategory("android.intent.category.LAUNCHER")
                intent.data = Uri.parse(url)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Chrome is not installed, try other
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }

        //setFollowButtonText(args.argElection.id)

        binding.followButton.setOnClickListener {
            _viewModel.isFollowed.value?.let { isFollowed ->
                if (isFollowed) { //unfollow
                    _viewModel.unfollow(args.argElection.id)
                } else { //follow
                    _viewModel.follow(args.argElection.id)
                }
            }
        }

        return binding.root
    }

   /* private fun setFollowButtonText(electionId: Int) {
        _viewModel.checkFollowStatus(electionId)
    }*/

   /* private fun isElectionFollowed(electionId: Int): Boolean {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val followed = sharedPref.getStringSet(FOLLOWED_ELECTIONS_PREFERENCES, emptySet())
        return if (followed.isNullOrEmpty()) {
            false
        } else {
            followed.contains(id.toString())
        }
    }*/

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