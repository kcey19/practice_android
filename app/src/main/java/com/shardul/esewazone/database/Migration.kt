package com.shardul.esewazone.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val Migration_1_2 = object:Migration(1,2){
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                ALTER TABLE my_cart
                ADD COLUMN userId TEXT NOT NULL DEFAULT ''
            """.trimIndent()
        )
        db.execSQL(
            """
                ALTER TABLE my_cart
                ADD COLUMN productId Integer NOT NULL DEFAULT 0
            """.trimIndent()
        )
    }
}



