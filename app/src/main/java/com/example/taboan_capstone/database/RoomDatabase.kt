package com.example.taboan_capstone.database

import androidx.room.Database
import androidx.room.TypeConverter
import androidx.room.RoomDatabase
import com.example.taboan_capstone.dao.DBDao
import com.example.taboan_capstone.models.CurrentUserModel
import com.example.taboan_capstone.models.CustomerCartModel


@Database(entities = [CurrentUserModel::class,CustomerCartModel::class], version = 1)
abstract class RoomDatabase: RoomDatabase() {
    abstract fun dbDao(): DBDao
}