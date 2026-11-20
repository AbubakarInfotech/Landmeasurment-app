package com.example.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.gps.GpsCalculator
import com.example.gps.GpsPoint
import com.example.localization.AppStrings
import com.example.model.CompassDirection
import com.example.model.Language
import com.example.ui.theme.AlertRed
import com.example.ui.theme.FertileGreenPrimary
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.WarningAmber
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpsWalkingScreen(
    lang: Language,
    corners: List<GpsPoint>,
    currentLocation: GpsPoint?,
    isSatellite: Boolean,
    onLocationUpdate: (lat: Double, lon: Double, accuracy: Float) -> Unit,
    onMarkCorner: () -> Unit,
    onUndoCorner: () -> Unit,
    onResetCorners: () -> Unit,
    onToggleSatellite: () -> Unit,
    onClosePlot: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasPermission = granted
    }

    // Android LocationManager listener
    DisposableEffect(hasPermission) {
        if (!hasPermission) return@DisposableEffect onDispose {}

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val listener = object : LocationListener {
            override fun onLocationChanged(loc: Location) {
                onLocationUpdate(loc.latitude, loc.longitude, loc.accuracy)
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        try {
            // Check last known location
            val lastGps = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val lastNet = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val bestLast = lastGps ?: lastNet
            if (bestLast != null) {
                onLocationUpdate(bestLast.latitude, bestLast.longitude, bestLast.accuracy)
            }

            if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0.5f, listener)
            }
            if (locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 0.5f, listener)
            }
        } catch (_: SecurityException) {}

        onDispose {
            try {
                locationManager?.removeUpdates(listener)
            } catch (_: Exception) {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.measureByWalking(lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("gps_back_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleSatellite,
                        modifier = Modifier.testTag("toggle_satellite_btn")
                    ) {
                        Icon(
                            imageVector = if (isSatellite) Icons.Default.Map else Icons.Default.Layers,
                            contentDescription = if (isSatellite) AppStrings.streetMap(lang) else AppStrings.satellite(lang),
                            tint = FertileGreenPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = FertileGreenPrimary
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (!hasPermission) {
            // Permission request prompt
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFE8F5E9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        tint = FertileGreenPrimary,
                        modifier = Modifier.size(46.dp)
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = AppStrings.locationPermissionNeeded(lang),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = AppStrings.locationPermissionDesc(lang),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("grant_gps_permission_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FertileGreenPrimary)
                ) {
                    Text(AppStrings.grantPermission(lang), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(AppStrings.continueManual(lang))
                }
            }
        } else {
            // Live GPS Walk Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Top Info & Status Bar
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    // Accuracy & Layer Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val accuracy = currentLocation?.accuracyMeters ?: 0f
                        val isWeak = accuracy > 10f || accuracy == 0f

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isWeak) Color(0xFFFFF3CD) else Color(0xFFE8F5E9),
                            border = BorderStroke(1.dp, if (isWeak) WarningAmber else FertileGreenPrimary),
                            modifier = Modifier.testTag("gps_accuracy_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isWeak) Icons.Default.Warning else Icons.Default.GpsFixed,
                                    contentDescription = null,
                                    tint = if (isWeak) WarningAmber else FertileGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (accuracy > 0f) AppStrings.gpsAccuracy(DecimalFormat("#0").format(accuracy), lang) else "Acquiring GPS...",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWeak) Color(0xFF856404) else Color(0xFF1B5E20)
                                )
                            }
                        }

                        // Corners Count
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F4F1),
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            Text(
                                text = AppStrings.cornersMarked(corners.size, lang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E4E35),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // Live Distance So Far Banner
                    if (corners.isNotEmpty() && currentLocation != null) {
                        val lastCorner = corners.last()
                        val liveDistance = GpsCalculator.haversineDistanceFeet(
                            lastCorner.latitude, lastCorner.longitude,
                            currentLocation.latitude, currentLocation.longitude
                        )
                        val bearing = GpsCalculator.calculateBearing(
                            lastCorner.latitude, lastCorner.longitude,
                            currentLocation.latitude, currentLocation.longitude
                        )
                        val dir = GpsCalculator.bearingToCompassDirection(bearing)
                        val dirName = dir.getName(lang)

                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("live_walking_distance_banner"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            border = BorderStroke(1.dp, FertileGreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = FertileGreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = AppStrings.liveDistanceSoFar(DecimalFormat("#,##0.0").format(liveDistance), dirName, lang),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F4722)
                                )
                            }
                        }
                    }

                    // Disclaimer Note
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = AppStrings.gpsEstimateDisputeNotice(lang),
                        fontSize = 11.sp,
                        color = Color(0xFF666666),
                        lineHeight = 14.sp
                    )
                }

                // Leaflet / OpenStreetMap Live Map WebView
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    LeafletMapView(
                        currentLocation = currentLocation,
                        corners = corners,
                        isSatellite = isSatellite,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Control Action Buttons
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Mark Corner Button
                            Button(
                                onClick = onMarkCorner,
                                enabled = currentLocation != null,
                                modifier = Modifier.weight(1.5f).height(54.dp).testTag("mark_corner_btn"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FertileGreenPrimary)
                            ) {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.markCorner(lang),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Close Plot Button (Finish & Transfer to Manual)
                            Button(
                                onClick = onClosePlot,
                                enabled = corners.size >= 3,
                                modifier = Modifier.weight(1.3f).height(54.dp).testTag("close_plot_btn"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HarvestGold)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.closeThePlot(lang),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Secondary actions: Undo & Start Again
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onUndoCorner,
                                enabled = corners.isNotEmpty(),
                                modifier = Modifier.weight(1f).height(44.dp).testTag("undo_corner_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(AppStrings.undoLastCorner(lang), fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = onResetCorners,
                                enabled = corners.isNotEmpty(),
                                modifier = Modifier.weight(1f).height(44.dp).testTag("reset_gps_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(AppStrings.startAgain(lang), fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Leaflet.js with OpenStreetMap and Esri World Imagery Satellite tiles (Open-source, No API Key needed)
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun LeafletMapView(
    currentLocation: GpsPoint?,
    corners: List<GpsPoint>,
    isSatellite: Boolean,
    modifier: Modifier = Modifier
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }

    val defaultLat = currentLocation?.latitude ?: 30.3753 // Pakistan central approx
    val defaultLon = currentLocation?.longitude ?: 69.3451

    val html = remember(isSatellite) {
        buildLeafletHtml(defaultLat, defaultLon, isSatellite)
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        isMapLoaded = true
                    }
                }
                loadDataWithBaseURL("https://openstreetmap.org", html, "text/html", "UTF-8", null)
                webViewRef = this
            }
        },
        update = { wv ->
            webViewRef = wv
        },
        modifier = modifier
    )

    // Sync marked corners and current position into Leaflet via JavaScript
    LaunchedEffect(currentLocation, corners, isMapLoaded) {
        if (!isMapLoaded) return@LaunchedEffect
        val wv = webViewRef ?: return@LaunchedEffect

        val cornersJsArray = buildString {
            append("[")
            corners.forEachIndexed { i, pt ->
                append("{lat: ${pt.latitude}, lon: ${pt.longitude}, id: ${i + 1}}")
                if (i < corners.size - 1) append(",")
            }
            append("]")
        }

        val currJs = if (currentLocation != null) {
            "{lat: ${currentLocation.latitude}, lon: ${currentLocation.longitude}, acc: ${currentLocation.accuracyMeters}}"
        } else "null"

        val script = "if (window.updateGpsPlot) { window.updateGpsPlot($currJs, $cornersJsArray); }"
        wv.evaluateJavascript(script, null)
    }
}

private fun buildLeafletHtml(lat: Double, lon: Double, isSatellite: Boolean): String {
    val tileUrl = if (isSatellite) {
        "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"
    } else {
        "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
    }

    val maxZoom = if (isSatellite) 19 else 19

    return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
          <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
          <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
          <style>
            body, html, #map { margin: 0; padding: 0; width: 100%; height: 100%; background: #e0ece0; }
            .corner-pin {
              background: #1E6B38; color: white; border: 2px solid white;
              border-radius: 50%; font-size: 13px; font-weight: bold;
              width: 26px; height: 26px; text-align: center; line-height: 23px;
              box-shadow: 0 2px 6px rgba(0,0,0,0.4);
            }
            .curr-pin {
              background: #E65100; border: 3px solid white; border-radius: 50%;
              width: 18px; height: 18px; box-shadow: 0 0 10px rgba(230,81,0,0.8);
            }
          </style>
        </head>
        <body>
          <div id="map"></div>
          <script>
            var map = L.map('map', { zoomControl: false }).setView([$lat, $lon], 17);
            L.tileLayer('$tileUrl', {
              maxZoom: $maxZoom,
              attribution: 'OpenStreetMap'
            }).addTo(map);

            var currMarker = null;
            var currAccuracyCircle = null;
            var cornerMarkers = [];
            var plotPolygon = null;
            var liveWalkLine = null;

            window.updateGpsPlot = function(curr, corners) {
              // Clear previous
              cornerMarkers.forEach(function(m) { map.removeLayer(m); });
              cornerMarkers = [];
              if (plotPolygon) map.removeLayer(plotPolygon);
              if (liveWalkLine) map.removeLayer(liveWalkLine);

              var latlngs = [];
              corners.forEach(function(c) {
                var pos = [c.lat, c.lon];
                latlngs.push(pos);
                var icon = L.divIcon({
                  className: 'corner-pin',
                  html: c.id,
                  iconSize: [26, 26],
                  iconAnchor: [13, 13]
                });
                var m = L.marker(pos, { icon: icon }).addTo(map);
                cornerMarkers.push(m);
              });

              if (latlngs.length >= 2) {
                plotPolygon = L.polygon(latlngs, {
                  color: '#1E6B38',
                  weight: 4,
                  fillColor: '#2E8B4C',
                  fillOpacity: 0.25
                }).addTo(map);
              }

              // Update Current Marker
              if (curr) {
                var curPos = [curr.lat, curr.lon];
                if (!currMarker) {
                  var curIcon = L.divIcon({ className: 'curr-pin', iconSize: [18, 18], iconAnchor: [9, 9] });
                  currMarker = L.marker(curPos, { icon: curIcon }).addTo(map);
                } else {
                  currMarker.setLatLng(curPos);
                }

                if (curr.acc) {
                  if (!currAccuracyCircle) {
                    currAccuracyCircle = L.circle(curPos, { radius: curr.acc, color: '#E65100', weight: 1, fillOpacity: 0.15 }).addTo(map);
                  } else {
                    currAccuracyCircle.setLatLng(curPos);
                    currAccuracyCircle.setRadius(curr.acc);
                  }
                }

                // Draw dashed line from last corner to current location
                if (latlngs.length > 0) {
                  var lastPos = latlngs[latlngs.length - 1];
                  liveWalkLine = L.polyline([lastPos, curPos], {
                    color: '#E65100',
                    weight: 3,
                    dashArray: '8, 8'
                  }).addTo(map);
                }

                if (latlngs.length === 0) {
                  map.panTo(curPos);
                }
              }

              if (latlngs.length > 1) {
                map.fitBounds(L.latLngBounds(latlngs), { padding: [40, 40] });
              }
            };
          </script>
        </body>
        </html>
    """.trimIndent()
}
