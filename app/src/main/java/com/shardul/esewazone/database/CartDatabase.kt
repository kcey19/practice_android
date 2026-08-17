package com.shardul.esewazone.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CartEntity::class],
    version = 2,
    exportSchema = true
)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    companion object {
        @Volatile
        private var INSTANCE: CartDatabase? = null
        fun getDatabase(context: Context): CartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                CartDatabase::class.java,
                                "cart_database"
                            ).addMigrations(Migration_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
