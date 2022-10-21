package com.plgpl.jarvis.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.plgpl.jarvis.R
import com.plgpl.jarvis.helpers.LocationHelper
import com.plgpl.jarvis.services.PreferencesService


class SplashActivity : AppCompatActivity() {


    private var permisos = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.INTERNET
    )

    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        if (comprovaPermisos(this)) {
            goToMainActivity()
        } else {
            ActivityCompat.requestPermissions(this, permisos, 1)
        }
    }

    private fun comprovaPermisos(context: Context): Boolean {
        for (permis in permisos) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permis
                ) == PackageManager.PERMISSION_DENIED
            ) return false
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (comprovaGrantResults(grantResults)) {
            goToMainActivity()
        } else {


            Toast.makeText(this, "You must accept the permissions to continue", Toast.LENGTH_SHORT)
                .show()


            val handler = Handler()
            handler.postDelayed({
                ActivityCompat.requestPermissions(this, permissions, 1)
            }, 3000)
        }
    }

    private fun comprovaGrantResults(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) return false
        }
        return true
    }

    private fun goToMainActivity() {

        inicialitza()

        val handler = Handler()

        handler.postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }, 3000)
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun inicialitza() {

        if (isLocationEnabled()) {

            mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                val location: Location? = task.result
                if (location == null) {
                    requestNewLocationData()
                } else {
                    iniciaCamps(location.latitude, location.longitude)
                }
            }
        } else {
            Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private fun getContext(): Context {
        return this
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            iniciaCamps(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    private fun iniciaCamps(latitude: Double, longitude: Double) {

        val localitzacio = LocationHelper(getContext()).getCityByLatLong(latitude, longitude)
        val city = PreferencesService(this).getPreference("city")
        val unitats = PreferencesService(this).getPreference("unitats")


        PreferencesService(getContext()).savePreference("localitzacio", localitzacio.ciutat)


        if (city == null) {
            PreferencesService(this).savePreference("city", localitzacio.ciutat)
        }


        if (unitats == null) {
            PreferencesService(this).savePreference("unitats", "metric")
        }
    }


}
