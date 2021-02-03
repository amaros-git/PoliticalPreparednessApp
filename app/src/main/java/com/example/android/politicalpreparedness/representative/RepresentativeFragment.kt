package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.database.ElectionDatabase
import com.example.android.politicalpreparedness.data.database.LocalDataSource
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.data.network.models.Address
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import java.util.Locale

class RepresentativeFragment : BaseFragment(), LocationListener { //TODO move location listener

    private val TAG = RepresentativeFragment::class.java.simpleName

    private lateinit var locationManager: LocationManager

    private lateinit var binding: FragmentRepresentativeBinding

    private lateinit var stateSpinnerAdapter: ArrayAdapter<CharSequence>

    private lateinit var listAdapter: RepresentativeListAdapter

    override val _viewModel by viewModels<RepresentativeViewModel> {
        RepresentativeViewModelFactory(
                requireActivity().application,
                ApplicationRepository(
                        LocalDataSource(ElectionDatabase.getInstance(requireContext())),
                        CivicsApi
                )
        )
    }

    private val startForPermissionResult = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { result ->
        Log.d(TAG, "permission result = $result") //TODO if false show snack bar with settings
    }


    //TODO: Declare ViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        binding.buttonLocation.setOnClickListener {
            registerLocationListener()
        }

        binding.buttonSearch.setOnClickListener {
            val address = getAddressFromFields()
            if (isAddressValid(address)) {
                findRepresentatives(address)
            } else {
                //TODO show toast
            }
        }

        stateSpinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.states,
                R.layout.spinner_item
        )


        initStateSpinner()

        setupListAdapter()


        return binding.root

        //TODO: Establish bindings

        //TODO: Define and assign Representative adapter

        //TODO: Populate Representative adapter

        //TODO: Establish button listeners for field and location search

    }

    override fun onStart() {
        super.onStart()

        checkLocationPermission()

        locationManager =
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onDestroy() {
        super.onDestroy()
        //remove location listener
        locationManager.removeUpdates(this)
    }


    private fun getAddressFromFields() =
            Address(
                    binding.addressLine1.text.toString(),
                    binding.addressLine2.text.toString(),
                    binding.city.text.toString(),
                    binding.state.selectedItem.toString(),
                    binding.zip.text.toString()
            )

    private fun isAddressValid(address: Address): Boolean {
        Log.d(TAG, "address = $address")
        return true
    }

    private fun findRepresentatives(address: Address) {
        _viewModel.getRepresentative(address)
    }

    private fun initStateSpinner() {
        stateSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item)
        binding.state.adapter = stateSpinnerAdapter
    }

    private fun setStateSpinnerValue(state: String) {
        binding.state.setSelection(stateSpinnerAdapter.getPosition(state))
    }

    private fun setupListAdapter() {
        listAdapter = RepresentativeListAdapter(viewModel)
        binding.upcomingElections.adapter = listAdapter
    }


    @SuppressLint("MissingPermission")
    private fun registerLocationListener() {
        if (isLocationPermissionGranted()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1f, this)
        } else {
            requestLocationPermission()
        }
    }

    /* override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         //TODO: Handle location permission result to get location on permission granted
     }*/

    private fun checkLocationPermission() {
        if (!isLocationPermissionGranted()) {
            requestLocationPermission()
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestLocationPermission() {
        startForPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }


    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "onLocationChanged called")

        Log.d(TAG, "Current location: ${location.latitude}, ${location.longitude}")

        val address = geoCodeLocation(location)
        Log.d(TAG, "address = $address")

        _viewModel.setAddress(address)
        setStateSpinnerValue(address.state)

        //we need location only once
        locationManager.removeUpdates(this)
    }

}