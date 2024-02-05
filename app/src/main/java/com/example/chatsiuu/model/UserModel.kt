package com.example.chatsiuu.model

import com.google.firebase.Timestamp

data class UserModel(
    var phone: String = "",
    var username: String = "",
    var userId: String = "",
    var createTimestamp: Timestamp? = null,
    var fcmtoken: String = ""
)
//var fcmtoken: String = ""