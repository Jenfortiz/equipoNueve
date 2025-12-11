package com.example.inventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.inventory.model.Inventory
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(inventory: Inventory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(inventories: List<Inventory>)

    @Update
    suspend fun update(inventory: Inventory)

    @Delete
    suspend fun delete(inventory: Inventory)

    @Query("SELECT * FROM inventory_items WHERE id = :id")
    fun getItem(id: String): Flow<Inventory>

    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllItems(): Flow<List<Inventory>>

    @Query("DELETE FROM inventory_items")
    suspend fun deleteAll()


    @Transaction
    suspend fun syncItems(inventories: List<Inventory>) {
        deleteAll()
        insertAll(inventories)
    }
}
