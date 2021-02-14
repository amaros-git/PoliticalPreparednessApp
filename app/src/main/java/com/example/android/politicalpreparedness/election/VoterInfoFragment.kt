package com.example.android.politicalpreparedness.election

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.database.ElectionDatabase
import com.example.android.politicalpreparedness.data.database.LocalDataSource
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.data.models.Election
import com.example.android.politicalpreparedness.data.models.VoterInfoResponse
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.utils.setDisplayHomeAsUpEnabled
import com.example.android.politicalpreparedness.utils.setTitle


class VoterInfoFragment : BaseFragment() {

    private val TAG = VoterInfoFragment::class.java.simpleName

    private lateinit var binding: FragmentVoterInfoBinding

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

        binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = _viewModel

        election = args.argElection
        binding.election = election

        setTitle(election.name)
        setDisplayHomeAsUpEnabled(true)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        _viewModel.getVoterInfo(election.id, election.division)

        _viewModel.voterInfo.observe(viewLifecycleOwner) { response ->
            updateVoterInfo(response)
        }

        binding.followButton.setOnClickListener {
            changeFollowingStatus()
        }
    }

    private fun changeFollowingStatus() {
        _viewModel.isFollowed.value?.let { isFollowed ->
            if (isFollowed) { //unfollow
                _viewModel.unfollow(args.argElection.id)
            } else { //follow
                _viewModel.follow(args.argElection.id)
            }
        }
    }

    private fun enableLink(view: TextView, url: String?) {
        url?.let {
            view.visibility = View.VISIBLE
            view.paintFlags = view.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            view.setOnClickListener { setIntent(view, url) }
        }
    }

    private fun setIntent(view: TextView, url: String) {
        val uri = Uri.parse(url)
        try {
            val intent = Intent("android.intent.action.MAIN")
            intent.component =
                    ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main")
            intent.addCategory("android.intent.category.LAUNCHER")
            intent.data = uri
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Chrome is not installed
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    private fun updateVoterInfo(response: VoterInfoResponse?) { //TODO rename method
        response?.let { voterInfo ->
            binding.address =
                    voterInfo.state?.get(0)?.electionAdministrationBody?.correspondenceAddress

            // not sure how it should be handled the case when more than one state on the list,
            // I will use the first element
            if (!voterInfo.state.isNullOrEmpty()) {
                val votingUrl =
                        voterInfo.state[0].electionAdministrationBody.votingLocationFinderUrl
                val ballotUrl = voterInfo.state[0].electionAdministrationBody.ballotInfoUrl

                enableLink(binding.votingLocations, votingUrl)
                enableLink(binding.ballotInformation, ballotUrl)
            }
        }
    }
}