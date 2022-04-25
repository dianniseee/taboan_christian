package com.example.taboan_capstone

import com.example.taboan_capstone.models.CurrentUserModel
import com.example.taboan_capstone.models.CustomerCartModel
import com.example.taboan_capstone.models.DriverModel

object Globals {

    var firebaseLink = "https://taboan-express-default-rtdb.asia-southeast1.firebasedatabase.app/"

    lateinit var currentUser: CurrentUserModel
    lateinit var  userCart: CustomerCartModel
    lateinit var  currentDriver: DriverModel

    lateinit var isAvailable: String

    var selectedIndex: Int? = null
}