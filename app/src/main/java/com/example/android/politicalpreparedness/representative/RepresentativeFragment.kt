package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.database.ElectionDatabase
import com.example.android.politicalpreparedness.data.database.LocalDataSource
import com.example.android.politicalpreparedness.data.database.representativescache.RepresentativeDatabase
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.data.models.Address
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.utils.setDisplayHomeAsUpEnabled
import com.example.android.politicalpreparedness.utils.setTitle
import com.google.android.material.appbar.AppBarLayout

class RepresentativeFragment : BaseFragment() { //TODO move location listener

    companion object {
        private const val Extra_address_line1 = "Extra_address_line1"
        private const val Extra_address_line2 = "Extra_address_line2"
        private const val Extra_address_city = "Extra_address_city"
        private const val Extra_address_state = "Extra_address_state"
        private const val Extra_address_zip = "Extra_address_zip"

    }

    private val TAG = RepresentativeFragment::class.java.simpleName

    private lateinit var binding: FragmentRepresentativeBinding

    private lateinit var stateSpinnerAdapter: ArrayAdapter<CharSequence>

    private lateinit var listAdapter: RepresentativeListAdapter

    override val _viewModel by viewModels<RepresentativeViewModel> {
        RepresentativeViewModelFactory(
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

    private val startForPermissionResult = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { result ->
        Log.d(TAG, "permission result = $result") //TODO if false show snack bar with settings
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        setTitle(getString(R.string.representative_title))
        setDisplayHomeAsUpEnabled(true)

        stateSpinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.states,
                R.layout.spinner_item
        )

        restoreFieldsIfNeeded(savedInstanceState)

        _viewModel.representatives.observe(viewLifecycleOwner) { list ->
            Log.d(TAG, "Representatives:")
            list.forEach {
                Log.d(TAG, it.toString())
            }
            listAdapter.submitMyList(list, getString(R.string.my_representatives))
        }

        _viewModel.cachedRepresentatives.observe(viewLifecycleOwner) { list ->
            Log.d(TAG, "cached representatives:")
            list?.forEach {
                Log.d(TAG, it.toString())
            }
        }

        _viewModel.locationAddress.observe(viewLifecycleOwner) {
            it?.let { setAddressToFields(it) }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.buttonLocation.setOnClickListener {
            getMyLocation()
        }

        binding.buttonSearch.setOnClickListener {
            val address = getAddressFromFields()
            findRepresentatives(address)
        }

        setupViews()
    }

    private fun setupViews() {
        initStateSpinner()
        configureAnimation()
        setupListAdapter()
    }

    private fun getMyLocation() {
        hideKeyboard()

        if(!isLocationPermissionGranted()) {
            requestLocationPermission()
        } else {
            _viewModel.startAddressLocation()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val address = getAddressFromFields()
        outState.apply {
            putString(Extra_address_line1, address.line1)
            putString(Extra_address_line2, address.line2)
            putString(Extra_address_city, address.city)
            putString(Extra_address_state, address.state)
            putString(Extra_address_zip, address.zip)

        }
    }

    private fun restoreFieldsIfNeeded(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            val line1 = (it.getString(Extra_address_line1)) ?: ""
            val line2 = (it.getString(Extra_address_line2)) ?: ""
            val city = (it.getString(Extra_address_city)) ?: ""
            val state = (it.getString(Extra_address_state)) ?: ""
            val zip = (it.getString(Extra_address_zip)) ?: ""

            setAddressToFields(Address(line1, line2, city, state, zip))
        }
    }

    private fun configureAnimation() {
        val appBarLayout: AppBarLayout = binding.appbarLayout
        val motionLayout: MotionLayout = binding.motionLayout
        val listener = AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val seekPosition = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
            motionLayout.progress = seekPosition
        }

        appBarLayout.addOnOffsetChangedListener(listener)
    }

    private fun getAddressFromFields() =
            Address(
                    binding.addressLine1.text.toString(),
                    binding.addressLine2.text.toString(),
                    binding.city.text.toString(),
                    binding.state.selectedItem.toString(),
                    binding.zip.text.toString()
            )

    private fun setAddressToFields(address: Address) {
        binding.addressLine1.setText(address.line1)
        binding.addressLine2.setText(address.line2)
        binding.city.setText(address.city)
        setStateSpinnerValue(address.state)
        binding.zip.setText(address.zip)
    }

    private fun isAddressValid(address: Address): Boolean {
            return (address.city.isNotEmpty()) || (address.state.isNotEmpty())
    }

    private fun findRepresentatives(address: Address) {
        hideKeyboard()
        if (isAddressValid(address)) {
            _viewModel.getRepresentative(address)
        } else {
            _viewModel.showErrorMessage.value = getString(R.string.address_check_error)
        }
    }

    private fun initStateSpinner() {
        stateSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item)
        binding.state.adapter = stateSpinnerAdapter
    }

    private fun setStateSpinnerValue(state: String) {
        binding.state.setSelection(stateSpinnerAdapter.getPosition(state))
    }

    private fun setupListAdapter() {
        listAdapter = RepresentativeListAdapter(_viewModel)
        binding.representatives.adapter = listAdapter
    }

    private fun isLocationPermissionGranted(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestLocationPermission() {
        startForPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }


    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}