package com.example.inventory.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.inventory.data.InventoryDB
import com.example.inventory.data.InventoryRepository
import com.example.inventory.model.Inventory

class HomeInventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InventoryRepository

    val inventoryItems: LiveData<List<Inventory>>

    init {
        val inventoryDao = InventoryDB.getDatabase(application).inventoryDao()
        repository = InventoryRepository(inventoryDao)
        inventoryItems = repository.getInventoryItems().asLiveData()
    }
}
