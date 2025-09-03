package com.task.busdriver.view.ui.activities.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.Priority
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import com.google.android.gms.location.LocationServices
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.task.busdriver.R
import com.task.busdriver.databinding.ActivityMapBinding
import com.task.busdriver.domain.entities.TripData
import com.task.busdriver.domain.entities.TripPointEntity
import com.task.busdriver.view.viewModels.LoginVM
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.File
import com.google.android.gms.location.LocationResult
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.task.busdriver.domain.entities.TripEntity
import org.osmdroid.tileprovider.modules.OfflineTileProvider
import org.osmdroid.tileprovider.tilesource.FileBasedTileSource
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.util.BoundingBox
import java.util.Locale


@Suppress("DEPRECATION")
class MapActivity : ComponentActivity() {

    private lateinit var binding: ActivityMapBinding
    private lateinit var polyline: Polyline
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var tripStarted = false
    private var tripStartTime: Long = 0

    private var lat: Double? = null
    private var lon: Double? = null
    private var endLat: Double? = null
    private var endLon: Double? = null

    private val loginVM: LoginVM by viewModels()
    private val tripPoints = ArrayList<TripPointEntity>()
    private lateinit var tripId: String

    @SuppressLint("VisibleForTests", "UseCompatLoadingForDrawables")
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)



        tripId = "trip_" + System.currentTimeMillis()

        // 1⃣ Setup osmdroid config for offline cache base path
        val basePath = File(Environment.getExternalStorageDirectory(), "osmdroid")
        Configuration.getInstance().osmdroidBasePath = basePath
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        // 2⃣ Request permissions
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 1
        )

        // Setup Toolbar and navigation
        setActionBar(binding.myToolbar)
        actionBar?.apply {
            title = "Bus Driver Map"
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
        val upArrow = ContextCompat.getDrawable(this, R.drawable.back)
        upArrow?.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP)
        actionBar?.setHomeAsUpIndicator(upArrow)
        binding.myToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setActionBar(binding.myToolbar)
        actionBar!!.title = ""
        actionBar!!.setHomeButtonEnabled(true);
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.myToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Get coordinates from Intent extras
        lat = intent.getDoubleExtra("startLat", 0.0)
        lon = intent.getDoubleExtra("startLon", 0.0)
        endLat = intent.getDoubleExtra("endLat", 0.0)
        endLon = intent.getDoubleExtra("endLon", 0.0)

        // 3⃣ Setup map tile source based on network and offline ZIP archive
        val isOnline = isNetworkAvailable(this)
        val zipFile = File(basePath, "tiles.zip")

        if (zipFile.exists()) {
            // Use offline ZIP archive for tiles
            val archiveProvider = arrayOf<File>(zipFile)
            val tileProvider = OfflineTileProvider(SimpleRegisterReceiver(this), archiveProvider)
            binding.map.setTileProvider(tileProvider)

            // Detect internal tile source name (usually "Mapnik" if you built the ZIP that way)
            val archives = tileProvider.archives
            val tileSourceName = archives.firstOrNull()?.tileSources?.firstOrNull()
            if (tileSourceName != null) {
                binding.map.setTileSource(FileBasedTileSource.getSource(tileSourceName))
                binding.map.setUseDataConnection(false) // disable online fetch
            } else {
                // fallback if no source found inside archive
                binding.map.setTileSource(TileSourceFactory.MAPNIK)
                binding.map.setUseDataConnection(isOnline)
            }
        } else {
            // No offline ZIP available, use online tiles if network available
            binding.map.setTileSource(TileSourceFactory.MAPNIK)
            binding.map.setUseDataConnection(isOnline)
            if (!isOnline) {
                Toast.makeText(this, "No offline tiles found and offline mode active", Toast.LENGTH_LONG).show()
            }
        }

        binding.map.setMultiTouchControls(true)
        binding.map.controller.setZoom(10.5)

        // Setup polyline for route
        polyline = Polyline().apply {
            color = android.graphics.Color.BLUE
            width = 8f
            isEnabled = true
            isVisible = true
        }

        val routePoints = arrayListOf(
            GeoPoint(lat ?: 0.0, lon ?: 0.0),
            GeoPoint(endLat ?: 0.0, endLon ?: 0.0)
        )
        polyline.setPoints(routePoints)
        binding.map.overlays.add(polyline)

        // Setup destination marker
        val destinationMarker = Marker(binding.map).apply {
            position = GeoPoint(endLat ?: 0.0, endLon ?: 0.0)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Final Destination"
            snippet = "You've arrived!"
            icon = resources.getDrawable(R.drawable.destination, theme)
        }
        binding.map.overlays.add(destinationMarker)

        // Center the map on polyline center
        val centerPoint = polyline.bounds.centerWithDateLine
        binding.map.controller.setCenter(centerPoint)

        // Setup location overlay to show current location
        val gpsProvider = GpsMyLocationProvider(applicationContext)
        val locationOverlay = MyLocationNewOverlay(gpsProvider, binding.map)
        locationOverlay.apply {
            enableMyLocation()
            enableFollowLocation()
            isDrawAccuracyEnabled = true
        }
        binding.map.overlays.add(locationOverlay)

        // Initialize location client and request
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = 1000L
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
                    val currentLocation = GeoPoint(location.latitude, location.longitude)

                    binding.map.controller.animateTo(currentLocation)

                    val point = TripPointEntity(
                        0, tripId, location.latitude, location.longitude,
                        timestamp = System.currentTimeMillis()
                    )
                    tripPoints.add(point)

                    // You can save the tripPoints to database here or periodically
                }
            }
        }

        // FAB listeners for start/stop trip
        binding.fabMain.setOnClickListener { startTrip() }
        binding.fabMain2.setOnClickListener { stopTrip() }

        requestPermissions()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startTrip() {
        tripStarted = true
        tripPoints.clear()
        tripStartTime = System.currentTimeMillis()
        Toast.makeText(this, "Trip started", Toast.LENGTH_SHORT).show()
        startLocationUpdates()
    }

    private fun stopTrip() {
        tripStarted = false
        stopLocationUpdates()

        if (tripPoints.isEmpty()) {
            Toast.makeText(this, "No trip data to save", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate unique trip ID for Firebase save
        val firebaseTripId = FirebaseDatabase.getInstance().getReference("trips").push().key ?: return

        val tripData = TripData(
            driverId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown",
            tripId = firebaseTripId,
            locations = tripPoints,
            startedAt = tripStartTime,
            endedAt = System.currentTimeMillis()
        )

        FirebaseDatabase.getInstance().getReference("trips").child(firebaseTripId)
            .setValue(tripData)
            .addOnSuccessListener {
                Toast.makeText(this, "✅ Trip saved to Firebase!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "❌ Failed to save trip.", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        // Optionally stop location updates on pause
        // stopLocationUpdates()
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnected == true
    }
}