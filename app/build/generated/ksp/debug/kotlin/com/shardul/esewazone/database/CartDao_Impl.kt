package com.shardul.esewazone.database

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class CartDao_Impl(
  __db: RoomDatabase,
) : CartDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCartEntity: EntityInsertAdapter<CartEntity>

  private val __updateAdapterOfCartEntity: EntityDeleteOrUpdateAdapter<CartEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCartEntity = object : EntityInsertAdapter<CartEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `my_cart` (`id`,`userId`,`productId`,`quantity`,`price`,`image`,`title`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CartEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.userId)
        statement.bindLong(3, entity.productId.toLong())
        statement.bindLong(4, entity.quantity.toLong())
        statement.bindDouble(5, entity.price)
        statement.bindText(6, entity.image)
        statement.bindText(7, entity.title)
      }
    }
    this.__updateAdapterOfCartEntity = object : EntityDeleteOrUpdateAdapter<CartEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `my_cart` SET `id` = ?,`userId` = ?,`productId` = ?,`quantity` = ?,`price` = ?,`image` = ?,`title` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: CartEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.userId)
        statement.bindLong(3, entity.productId.toLong())
        statement.bindLong(4, entity.quantity.toLong())
        statement.bindDouble(5, entity.price)
        statement.bindText(6, entity.image)
        statement.bindText(7, entity.title)
        statement.bindLong(8, entity.id.toLong())
      }
    }
  }

  public override suspend fun insertItem(item: CartEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCartEntity.insert(_connection, item)
  }

  public override suspend fun updateItem(item: CartEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfCartEntity.handle(_connection, item)
  }

  public override fun getCartItemByUser(userId: String): Flow<List<CartEntity>> {
    val _sql: String = "SELECT * FROM my_cart WHERE userId = ?"
    return createFlow(__db, false, arrayOf("my_cart")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfImage: Int = getColumnIndexOrThrow(_stmt, "image")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _result: MutableList<CartEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CartEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpProductId: Int
          _tmpProductId = _stmt.getLong(_columnIndexOfProductId).toInt()
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpImage: String
          _tmpImage = _stmt.getText(_columnIndexOfImage)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          _item = CartEntity(_tmpId,_tmpUserId,_tmpProductId,_tmpQuantity,_tmpPrice,_tmpImage,_tmpTitle)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getItemById(productId: Int, userId: String): CartEntity? {
    val _sql: String = "SELECT * FROM my_cart WHERE productId=? AND userId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, productId.toLong())
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfPrice: Int = getColumnIndexOrThrow(_stmt, "price")
        val _columnIndexOfImage: Int = getColumnIndexOrThrow(_stmt, "image")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _result: CartEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpProductId: Int
          _tmpProductId = _stmt.getLong(_columnIndexOfProductId).toInt()
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpPrice: Double
          _tmpPrice = _stmt.getDouble(_columnIndexOfPrice)
          val _tmpImage: String
          _tmpImage = _stmt.getText(_columnIndexOfImage)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          _result = CartEntity(_tmpId,_tmpUserId,_tmpProductId,_tmpQuantity,_tmpPrice,_tmpImage,_tmpTitle)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteCartItem(id: Int, userId: String) {
    val _sql: String = "DELETE FROM my_cart WHERE id = ? AND userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id.toLong())
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearCart(userId: String) {
    val _sql: String = "DELETE FROM my_cart WHERE userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
