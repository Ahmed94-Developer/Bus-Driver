package com.task.busdriver.view.ui.activities.destination

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.task.busdriver.R
import com.task.busdriver.databinding.ActivityDestinationBinding
import com.task.busdriver.databinding.ActivityFinalDestinationBinding
import com.task.busdriver.view.ui.activities.map.MapActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale
import kotlin.jvm.java

class FinalDestinationActivity : ComponentActivity() {
    private lateinit var binding: ActivityFinalDestinationBinding
    private lateinit var map: MapView
    private var selectedMarker: Marker? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var polyline: Polyline
    private  var lat: Double? = null
    private var lon : Double? = null

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         lat = intent.getDoubleExtra("startLat",0.0)
         lon = intent.getDoubleExtra("startLon",0.0)

        showPictureDialog()

        binding = ActivityFinalDestinationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 1
        )




        setActionBar(binding.myToolbar2)
        actionBar!!.title = ""
        actionBar!!.setHomeButtonEnabled(true);
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        val upArrow = ContextCompat.getDrawable(this, R.drawable.back)
        upArrow?.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP)
        actionBar?.setHomeAsUpIndicator(upArrow)

        binding.myToolbar2.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.map2.setTileSource(TileSourceFactory.MAPNIK)
        binding.map2.setMultiTouchControls(true)

        val mapController = binding.map2.controller


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = 1000 // 1 second
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }



        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
                    val point = GeoPoint(location.latitude, location.longitude)
                    mapController.setCenter(point)
                    binding.map2.invalidate()
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        // Enable tap listener
        binding.map2.overlays.add(object : Overlay() {
            override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
                val projection = mapView.projection
                val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint

                selectLocation(geoPoint)
                return true
            }
        })

        binding.map2.controller.setZoom(15.0)
        val gpsProvider = GpsMyLocationProvider(applicationContext)

        val locationOverlay = MyLocationNewOverlay(gpsProvider, binding.map2)

        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        locationOverlay.isDrawAccuracyEnabled = true
        binding.map2.overlays.add(locationOverlay)
        binding.map2.invalidate()
    }

    private fun selectLocation(geoPoint: GeoPoint) {
        // Remove previous marker if any
        selectedMarker?.let { binding.map2.overlays.remove(it) }

        // Add marker at tapped location
        val marker = Marker(binding.map2)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Selected Location:\n${geoPoint.latitude}, ${geoPoint.longitude}"

        binding.map2.overlays.add(marker)
        selectedMarker = marker



        binding.map2.invalidate() // Refresh map

        // Optionally: do something with geoPoint
        Log.d("MapSelection", "Selected location: ${geoPoint.latitude}, ${geoPoint.longitude}")


        lateinit var cityName  : String
        try {
            val  geocoder :  Geocoder = Geocoder(this, Locale.getDefault());
            val addresses  : List<Address>? = geocoder.getFromLocation(geoPoint.latitude,geoPoint.longitude, 1)
            if (addresses != null && !addresses.isEmpty()) {

                cityName = addresses.get(0).getLocality()
                Toast.makeText(applicationContext, "Your Selected location is: $cityName", Toast.LENGTH_LONG,).show()
                val intent : Intent = Intent(this@FinalDestinationActivity, MapActivity::class.java)

                intent.putExtra("startLat",lat)
                intent.putExtra("startLon",lon)
                intent.putExtra("endLat",geoPoint.latitude)
                intent.putExtra("endLon",geoPoint.longitude)
                startActivity(intent)

            } else {
                Toast.makeText(applicationContext, "Selected city not found please try again..", Toast.LENGTH_LONG,
                ).show()
            }
        }catch (io : Exception){
            Toast.makeText(applicationContext, "Selected city not found please try again..", Toast.LENGTH_LONG,
            ).show()
        }






    }

    override fun onResume() {
        super.onResume()
        binding.map2.onResume() // needed for compass, my location overlays, v6.0.0+
    }

    override fun onPause() {
        super.onPause()
        binding.map2.onPause()
    }
    @SuppressLint("UseKtx")
    private fun showPictureDialog() {
        val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)

        dialog.setContentView(R.layout.selectable_dialog2)
        dialog.setCancelable(true)

        dialog.setCanceledOnTouchOutside(true)

        val window = dialog.window
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

// Set black shadow (dim background)
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window?.setDimAmount(0.45f) //
        window?.apply {
            setGravity(Gravity.TOP)
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        dialog.show()
    }
}
