package com.example.spotfinder.ui.locations

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotfinder.R
import com.example.spotfinder.data.db.DBHelper
import com.example.spotfinder.data.db.LocationModel
import com.example.spotfinder.databinding.ActivityLocationsBinding
import com.example.spotfinder.databinding.ItemLocationBinding
import com.example.spotfinder.ui.edit.EditLocationActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// screen listing every saved place
class LocationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationsBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: LocationsAdapter

    private val editLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            loadLocations()
        }

    // setup list and load data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper.getInstance(this)

        setupToolbar()
        setupRecycler()
        setupListeners()
        loadLocations()
    }

    // configure toolbar back button
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    // attach adapter to recycler
    private fun setupRecycler() {
        adapter = LocationsAdapter(onEdit = { openEditor(it) }, onDelete = { confirmDelete(it) })
        binding.recyclerLocations.layoutManager = LinearLayoutManager(this)
        binding.recyclerLocations.adapter = adapter
    }

    // listen for add button
    private fun setupListeners() {
        binding.buttonAdd.setOnClickListener { openEditor(null) }
    }

    // open add or edit screen
    private fun openEditor(model: LocationModel?) {
        val intent = Intent(this, EditLocationActivity::class.java)
        if (model != null) {
            intent.putExtra(EditLocationActivity.EXTRA_ID, model.id)
            intent.putExtra(EditLocationActivity.EXTRA_ADDRESS, model.address)
            intent.putExtra(EditLocationActivity.EXTRA_LAT, model.latitude)
            intent.putExtra(EditLocationActivity.EXTRA_LON, model.longitude)
        }
        editLauncher.launch(intent)
    }

    // ask before deleting row
    private fun confirmDelete(model: LocationModel) {
        AlertDialog.Builder(this)
            .setMessage(R.string.msg_delete_confirm)
            .setPositiveButton(R.string.msg_delete) { _: DialogInterface, _: Int ->
                deleteLocation(model.id)
            }
            .setNegativeButton(R.string.msg_cancel, null)
            .show()
    }

    // remove row and refresh list
    private fun deleteLocation(id: Long) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) { dbHelper.deleteLocationById(id) }
            loadLocations()
        }
    }

    // load full list from database
    private fun loadLocations() {
        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) { dbHelper.getAllLocations() }
            adapter.submitList(items)
        }
    }
}

// adapter for full list with edit and delete buttons
private class LocationsAdapter(
    private val onEdit: (LocationModel) -> Unit,
    private val onDelete: (LocationModel) -> Unit
) : RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {

    private val items = mutableListOf<LocationModel>()

    // replace items shown in list
    fun submitList(data: List<LocationModel>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    // create row view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // return item count
    override fun getItemCount(): Int = items.size

    // bind row data and callbacks
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onEdit, onDelete)
    }

    class ViewHolder(private val binding: ItemLocationBinding) : RecyclerView.ViewHolder(binding.root) {
        // fill view with model and assign actions
        fun bind(model: LocationModel, onEdit: (LocationModel) -> Unit, onDelete: (LocationModel) -> Unit) {
            binding.textAddress.text = model.address
            binding.textCoords.text = "${model.latitude}, ${model.longitude}"
            binding.buttonEdit.isEnabled = true
            binding.buttonDelete.isEnabled = true
            binding.buttonEdit.alpha = 1f
            binding.buttonDelete.alpha = 1f
            binding.buttonEdit.setOnClickListener { onEdit(model) }
            binding.buttonDelete.setOnClickListener { onDelete(model) }
        }
    }
}
