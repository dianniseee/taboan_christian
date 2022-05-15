package com.example.taboan_capstone.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.taboan_capstone.models.CurrentUserModel
import com.example.taboan_capstone.models.CustomerCartModel

@Dao
interface DBDao {

    @Query("SELECT * FROM current_user")
    fun getCurrentUser(): CurrentUserModel

    @Query("SELECT * FROM customer_cart")
    fun getCustomerCart(): CustomerCartModel

    @Query("SELECT * FROM customer_cart")
    fun getAllCart(): List<CustomerCartModel>

    @Query("SELECT DISTINCT productSellerID FROM customer_cart")
    fun getDistinctSeller(): List<String>

    @Query("SELECT DISTINCT market_name FROM customer_cart")
    fun getDistinctMarketCart(): List<String>

    @Query("SELECT * FROM customer_cart WHERE productSellerID IN (:id)")
    fun getCartSellerData(id: String): List<CustomerCartModel>

    @Query("SELECT count(*) FROM current_user")
    fun checkIfCurrentUserExist(): Int

    @Query("SELECT count(*) FROM customer_cart")
    fun checkIfCustomerCartExist(): Int

    @Query("UPDATE customer_cart SET quantity = quantity + :quantity,subtotal = subtotal + :subtotal WHERE productID = :prodID")
    fun addCustomerCartExist(prodID: String, quantity: Double, subtotal: Double)

    @Query("UPDATE customer_cart SET quantity = 0 + :quantity,subtotal = 0 + :subtotal WHERE productID = :prodID")
    fun updateCustomerCartExist(prodID: String, quantity: Double, subtotal: Double)

    @Query("UPDATE customer_cart SET quantity = quantity - :quantity,subtotal = subtotal - :subtotal WHERE productID = :prodID ")
    fun subtractCustomerCartExist(prodID: String, quantity: Double, subtotal: Double)

    @Query("SELECT EXISTS (SELECT * FROM customer_cart WHERE productID = :prodID)")
    fun checkExistingCart(prodID: String): Boolean


    @Insert
    fun insertCurrentUser(vararg currentUser: CurrentUserModel)

    @Insert
    fun insertCustomerCart(vararg  customerCart: CustomerCartModel)

    @Delete
    fun deleteCurrentUser(deleteCurrentUser: CurrentUserModel)

    @Delete
    fun deleteCustomerCart(deleteCartUser: CustomerCartModel)

    @Query("DELETE FROM customer_cart")
    fun clearCustomerCart()

    @Query("DELETE FROM customer_cart WHERE id = :id")
    fun deleteCartById(id: Int)



}