package com.example.chatsiuu.model

import com.google.firebase.Timestamp

data class ChatRoomModel(var chatRoomId:String="",var userIds:List<String>?=null ,var lastMessageTimeStamp:Timestamp?=null,var lastMessageSenderId:String="",var lastMessage:String="" )
