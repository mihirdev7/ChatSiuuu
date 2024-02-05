package com.example.chatsiuu.model

import com.google.firebase.Timestamp

data class ChatMessageModel(
    var message:String="",
    var SenderId:String="",
    var timestamp: Timestamp? =null)
