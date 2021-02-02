package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.data.ApplicationRepository
import com.example.android.politicalpreparedness.data.database.ElectionDatabase
import com.example.android.politicalpreparedness.data.database.LocalDataSource
import com.example.android.politicalpreparedness.data.network.CivicsApi
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.data.network.models.Address
import com.example.android.politicalpreparedness.election.VoterInfoViewModel
import com.example.android.politicalpreparedness.election.VoterInfoViewModelFactory
import java.util.Locale

class RepresentativeFragment : BaseFragment() {

    private val TAG = RepresentativeFragment::class.java.simpleName

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 0
    }

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
            ActivityResultContracts.RequestPermission()
    ) { result ->
        Log.d(TAG, "permission result = $result") //TODO if false show snack bar with settings
    }


    //TODO: Declare ViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this

        _viewModel.getRepresentative()

        return binding.root

        //TODO: Establish bindings

        //TODO: Define and assign Representative adapter

        //TODO: Populate Representative adapter

        //TODO: Establish button listeners for field and location search

    }

    override fun onStart() {
        super.onStart()
        checkLocationPermission()
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

}