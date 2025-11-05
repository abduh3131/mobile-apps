package com.example.spotfinder.ui.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.spotfinder.databinding.ActivityMapBinding
import org.json.JSONObject

const val MAP_BASE_URL = "https://unpkg.com/"

// provide leaflet html string for the map webview
val MAP_PAGE_HTML: String = """
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>SpotFinder Map</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
<style>
html, body, #map { height: 100%; margin: 0; background: #101317; }
.leaflet-popup-content-wrapper { background: #171A1F; color: #E6E9EE; }
.leaflet-control-attribution { font-size: 11px; }
</style>
</head>
<body>
<div id="map"></div>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script>
var map;
var marker;

function initMap(lat, lon, zoom) {
    map = L.map('map').setView([lat, lon], zoom);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors'
    }).addTo(map);
}

function showLocation(lat, lon, title) {
    if (!map) {
        initMap(43.6532, -79.3832, 10);
    }
    if (marker) {
        marker.remove();
    }
    map.setView([lat, lon], 14);
    marker = L.marker([lat, lon]).addTo(map);
    marker.bindPopup(title).openPopup();
}

document.addEventListener('DOMContentLoaded', function () {
    initMap(43.6532, -79.3832, 10);
});
</script>
</body>
</html>
""".trimIndent()

// screen showing full map in webview
class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding
    private var pendingLat: Double = DEFAULT_LAT
    private var pendingLon: Double = DEFAULT_LON
    private var pendingTitle: String = DEFAULT_TITLE

    // read intent data and load map
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        pendingLat = intent.getDoubleExtra(EXTRA_LAT, DEFAULT_LAT)
        pendingLon = intent.getDoubleExtra(EXTRA_LON, DEFAULT_LON)
        pendingTitle = intent.getStringExtra(EXTRA_TITLE) ?: DEFAULT_TITLE

        setupWebView()
    }

    // enable javascript and show marker once ready
    private fun setupWebView() {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val safeTitle = JSONObject.quote(pendingTitle)
                binding.webView.evaluateJavascript(
                    "showLocation($pendingLat, $pendingLon, $safeTitle)",
                    null
                )
            }
        }
        binding.webView.loadDataWithBaseURL(
            MAP_BASE_URL,
            MAP_PAGE_HTML,
            "text/html",
            "UTF-8",
            null
        )
    }

    companion object {
        private const val EXTRA_LAT = "extra_lat"
        private const val EXTRA_LON = "extra_lon"
        private const val EXTRA_TITLE = "extra_title"
        private const val DEFAULT_LAT = 43.6532
        private const val DEFAULT_LON = -79.3832
        private const val DEFAULT_TITLE = "Toronto"

        // helper to open map screen with data
        fun start(context: Context, lat: Double, lon: Double, title: String) {
            val intent = Intent(context, MapActivity::class.java).apply {
                putExtra(EXTRA_LAT, lat)
                putExtra(EXTRA_LON, lon)
                putExtra(EXTRA_TITLE, title)
            }
            context.startActivity(intent)
        }
    }
}
