package com.example.taboan_capstone.models
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize



@Entity(tableName = "current_user")
data class CurrentUserModel(
    @PrimaryKey(autoGenerate = true) @NonNull val user_id: Int,
    val uid: String,
    val first_name: String,
    val last_name: String,
    val email : String,
    val password : String,
    val mobile_phone: String,
    val user_type: String
    )

