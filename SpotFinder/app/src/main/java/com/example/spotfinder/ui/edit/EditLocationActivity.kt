package com.example.spotfinder.ui.edit

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.spotfinder.R
import com.example.spotfinder.data.db.DBHelper
import com.example.spotfinder.data.db.LocationModel
import com.example.spotfinder.databinding.ActivityEditLocationBinding
import com.example.spotfinder.net.GeocodingClient
import com.example.spotfinder.ui.map.MAP_BASE_URL
import com.example.spotfinder.ui.map.MAP_PAGE_HTML
import com.example.spotfinder.ui.settings.readPositionstackKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

// screen to add or edit one location
class EditLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditLocationBinding
    private lateinit var dbHelper: DBHelper

    private var locationId: Long = 0
    private var webViewReady = false

    // setup form, map preview, and listeners
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper.getInstance(this)

        setupToolbar()
        setupWebView()
        bindFromIntent()
        setupListeners()
    }

    // show toolbar back arrow
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    // load leaflet preview map
    private fun setupWebView() {
        binding.previewMap.settings.javaScriptEnabled = true
        binding.previewMap.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webViewReady = true
                updatePreviewMap()
            }
        }
        binding.previewMap.webChromeClient = WebChromeClient()
        binding.previewMap.loadDataWithBaseURL(
            MAP_BASE_URL,
            MAP_PAGE_HTML,
            "text/html",
            "UTF-8",
            null
        )
    }

    // fill form when editing existing row
    private fun bindFromIntent() {
        locationId = intent.getLongExtra(EXTRA_ID, 0)
        val address = intent.getStringExtra(EXTRA_ADDRESS).orEmpty()
        val lat = intent.getDoubleExtra(EXTRA_LAT, Double.NaN)
        val lon = intent.getDoubleExtra(EXTRA_LON, Double.NaN)
        if (locationId != 0L) {
            binding.inputAddress.setText(address)
            if (!lat.isNaN()) binding.inputLatitude.setText(lat.toString())
            if (!lon.isNaN()) binding.inputLongitude.setText(lon.toString())
        }
    }

    // wire up buttons for geocode, save, cancel
    private fun setupListeners() {
        binding.buttonGeocode.setOnClickListener { geocodeAddress() }
        binding.buttonSave.setOnClickListener { saveLocation() }
        binding.buttonCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    // call api and fill lat lon fields
    private fun geocodeAddress() {
        val address = binding.inputAddress.text?.toString().orEmpty()
        if (!isAddressValid(address)) {
            Toast.makeText(this, R.string.msg_invalid_input, Toast.LENGTH_SHORT).show()
            return
        }
        val key = readPositionstackKey()
        if (key.isBlank()) {
            Toast.makeText(this, R.string.msg_key_missing, Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            binding.buttonGeocode.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            val result = withContext(Dispatchers.IO) {
                GeocodingClient.geocodeAddress(key, address)
            }
            binding.buttonGeocode.isEnabled = true
            binding.progressBar.visibility = View.GONE
            if (result == null) {
                Toast.makeText(this@EditLocationActivity, R.string.msg_geocode_failed, Toast.LENGTH_SHORT).show()
            } else {
                binding.inputLatitude.setText(result.first.toString())
                binding.inputLongitude.setText(result.second.toString())
                updatePreviewMap()
            }
        }
    }

    // validate form and save to database
    private fun saveLocation() {
        val address = binding.inputAddress.text?.toString().orEmpty()
        val lat = binding.inputLatitude.text?.toString()?.toDoubleOrNull()
        val lon = binding.inputLongitude.text?.toString()?.toDoubleOrNull()
        if (!isAddressValid(address) || lat == null || lon == null ||
            !isLatitudeValid(lat) || !isLongitudeValid(lon)
        ) {
            Toast.makeText(this, R.string.msg_invalid_input, Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (locationId == 0L) {
                    dbHelper.insertLocation(LocationModel(0, address, lat, lon))
                } else {
                    dbHelper.updateLocation(LocationModel(locationId, address, lat, lon))
                }
            }
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    // update map marker when data ready
    private fun updatePreviewMap() {
        if (!webViewReady) return
        val lat = binding.inputLatitude.text?.toString()?.toDoubleOrNull() ?: return
        val lon = binding.inputLongitude.text?.toString()?.toDoubleOrNull() ?: return
        val address = binding.inputAddress.text?.toString().orEmpty()
        val safeTitle = JSONObject.quote(address)
        binding.previewMap.evaluateJavascript(
            "showLocation($lat, $lon, $safeTitle)",
            null
        )
    }

    // check that address has enough text
    private fun isAddressValid(address: String): Boolean = address.trim().length >= 5

    // make sure latitude stays in range
    private fun isLatitudeValid(value: Double): Boolean = value in -90.0..90.0

    // make sure longitude stays in range
    private fun isLongitudeValid(value: Double): Boolean = value in -180.0..180.0

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_ADDRESS = "extra_address"
        const val EXTRA_LAT = "extra_lat"
        const val EXTRA_LON = "extra_lon"
    }
}
