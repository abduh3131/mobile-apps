package com.example.spotfinder.ui.settings

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.spotfinder.R
import com.example.spotfinder.databinding.ActivitySettingsBinding
import com.example.spotfinder.net.GeocodingClient
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// screen where user saves geocoding key
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    // setup toolbar, load key, and listeners
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadCurrentKey()
        setupListeners()
    }

    // show toolbar back arrow
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    // preload stored key text
    private fun loadCurrentKey() {
        val current = readPositionstackKey()
        binding.inputKey.setText(current)
    }

    // save and test button listeners
    private fun setupListeners() {
        binding.buttonSave.setOnClickListener {
            val key = binding.inputKey.text?.toString().orEmpty()
            writePositionstackKey(key)
            Toast.makeText(this, R.string.msg_key_saved, Toast.LENGTH_SHORT).show()
        }
        binding.buttonTest.setOnClickListener { testKey() }
    }

    // do quick api call to check key
    private fun testKey() {
        val key = binding.inputKey.text?.toString().orEmpty()
        if (key.isBlank()) {
            Toast.makeText(this, R.string.msg_key_missing, Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            binding.buttonTest.isEnabled = false
            val result = withContext(Dispatchers.IO) {
                GeocodingClient.geocodeAddress(key, "Toronto")
            }
            binding.buttonTest.isEnabled = true
            val message = if (result != null) R.string.msg_key_test_success else R.string.msg_key_test_failed
            Toast.makeText(this@SettingsActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}

private const val PREFS_NAME = "spotfinder_prefs"
private const val KEY_POSITIONSTACK = "positionstack_key"

// read saved key or empty string
fun Context.readPositionstackKey(): String {
    val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString(KEY_POSITIONSTACK, "") ?: ""
}

// save the key value
fun Context.writePositionstackKey(key: String) {
    val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit { putString(KEY_POSITIONSTACK, key) }
}
