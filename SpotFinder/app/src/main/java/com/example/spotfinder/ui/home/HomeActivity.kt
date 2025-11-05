package com.example.spotfinder.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotfinder.R
import com.example.spotfinder.data.db.DBHelper
import com.example.spotfinder.data.db.LocationModel
import com.example.spotfinder.databinding.ActivityHomeBinding
import com.example.spotfinder.databinding.ItemLocationBinding
import com.example.spotfinder.ui.edit.EditLocationActivity
import com.example.spotfinder.ui.locations.LocationsActivity
import com.example.spotfinder.ui.map.MapActivity
import com.example.spotfinder.ui.settings.SettingsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// home screen with search and shortcuts
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var dbHelper: DBHelper
    private val recentAdapter = RecentLocationAdapter()

    private val editLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            loadRecentLocations()
        }

    // prepare ui and wire actions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper.getInstance(this)

        setupToolbar()
        setupRecycler()
        setupListeners()
    }

    // refresh recents when coming back
    override fun onResume() {
        super.onResume()
        loadRecentLocations()
    }

    // set toolbar with settings action
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        val settingsItem = binding.toolbar.menu.add(0, MENU_SETTINGS, 0, R.string.title_settings)
        settingsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == MENU_SETTINGS) {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            } else {
                false
            }
        }
    }

    // hook recycler to adapter
    private fun setupRecycler() {
        binding.recyclerRecent.layoutManager = LinearLayoutManager(this)
        binding.recyclerRecent.adapter = recentAdapter
    }

    // listen to button taps and search
    private fun setupListeners() {
        binding.buttonAdd.setOnClickListener {
            val intent = Intent(this, EditLocationActivity::class.java)
            editLauncher.launch(intent)
        }
        binding.buttonViewAll.setOnClickListener {
            startActivity(Intent(this, LocationsActivity::class.java))
        }
        binding.buttonMyLocation.setOnClickListener {
            Toast.makeText(this, R.string.msg_my_location_stub, Toast.LENGTH_SHORT).show()
        }
        binding.buttonSearch.setOnClickListener { performSearch() }
        binding.inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    // search for address and open map if found
    private fun performSearch() {
        val query = binding.inputSearch.text?.toString().orEmpty().trim()
        if (query.isBlank()) return
        lifecycleScope.launch {
            val match = withContext(Dispatchers.IO) { dbHelper.getByAddressLike(query) }
            if (match == null) {
                Toast.makeText(this@HomeActivity, R.string.msg_not_found, Toast.LENGTH_SHORT).show()
            } else {
                MapActivity.start(this@HomeActivity, match.latitude, match.longitude, match.address)
            }
        }
    }

    // load recent items for list
    private fun loadRecentLocations() {
        lifecycleScope.launch {
            val recent = withContext(Dispatchers.IO) { dbHelper.getRecentLocations() }
            if (recent.isEmpty()) {
                recentAdapter.submitPlaceholder()
            } else {
                recentAdapter.submitList(recent)
            }
        }
    }
}

// adapter to show small recent list with placeholder state
private class RecentLocationAdapter : RecyclerView.Adapter<RecentLocationAdapter.ViewHolder>() {

    private val items = mutableListOf<LocationModel>()
    private var isPlaceholder = false

    // show actual data
    fun submitList(data: List<LocationModel>) {
        isPlaceholder = false
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    // show empty placeholder row
    fun submitPlaceholder() {
        isPlaceholder = true
        items.clear()
        notifyDataSetChanged()
    }

    // inflate row view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLocationBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    // total rows depending on placeholder
    override fun getItemCount(): Int = if (isPlaceholder) 1 else items.size

    // bind either placeholder or row data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (isPlaceholder) {
            holder.bindPlaceholder()
        } else {
            holder.bind(items[position])
        }
    }

    class ViewHolder(private val binding: ItemLocationBinding) : RecyclerView.ViewHolder(binding.root) {
        // show real location text
        fun bind(model: LocationModel) {
            binding.textAddress.text = model.address
            binding.textCoords.text = "${model.latitude}, ${model.longitude}"
            binding.buttonEdit.isEnabled = false
            binding.buttonDelete.isEnabled = false
            binding.buttonEdit.alpha = 0.3f
            binding.buttonDelete.alpha = 0.3f
            binding.buttonEdit.setOnClickListener(null)
            binding.buttonDelete.setOnClickListener(null)
        }

        // show placeholder message
        fun bindPlaceholder() {
            val context = binding.root.context
            binding.textAddress.text = context.getString(R.string.msg_recent_empty_title)
            binding.textCoords.text = context.getString(R.string.msg_recent_empty_hint)
            binding.buttonEdit.isEnabled = false
            binding.buttonDelete.isEnabled = false
            binding.buttonEdit.alpha = 0.0f
            binding.buttonDelete.alpha = 0.0f
            binding.buttonEdit.setOnClickListener(null)
            binding.buttonDelete.setOnClickListener(null)
        }
    }
}

private const val MENU_SETTINGS = 1
