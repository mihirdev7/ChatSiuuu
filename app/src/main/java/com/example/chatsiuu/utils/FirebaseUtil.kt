package com.example.chatsiuu.utils

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat

class FirebaseUtil {
    companion object{
         fun currentUserID(): String {
            return FirebaseAuth.getInstance().currentUser!!.uid
        }

        fun isLoggedIn():Boolean {
            return currentUserID() != null
        }


        fun currentUserDetails():DocumentReference{
            return FirebaseFirestore.getInstance().collection("Users").document(currentUserID())
        }

        fun allUserCollectionReference():CollectionReference{
            return FirebaseFirestore.getInstance().collection("Users")
        }
        fun getChatRoomReference(chatRoomId:String):DocumentReference{
            return FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId)
        }

        fun getChatRoomMessageReference(chatroomId:String):CollectionReference{
        return getChatRoomReference(chatroomId).collection("Chats")

        }
        fun getChatRoomId(userId1:String, userId2:String):String{
            if(userId1.hashCode() <userId2.hashCode()){
                return userId1+"_"+userId2
            }else
                return userId2+"_"+userId1
        }
        fun allChatRoomCollectionReference(): CollectionReference{
            return FirebaseFirestore.getInstance().collection("chatrooms")
        }
            fun getOtherUserFromChatRoom(userIds:List<String>):DocumentReference{
                if(userIds.get(0).equals(currentUserID())){
                    return allUserCollectionReference().document(userIds.get(1))
                }else{
                    return allUserCollectionReference().document(userIds.get(0))
                }
            }
        fun timestampToString(timestamp : Timestamp):String{
            return SimpleDateFormat("HH:MM").format(timestamp.toDate())
        }
        fun logout(){
            FirebaseAuth.getInstance().signOut()
        }
        fun getCurrentProfilePicStorageRef():StorageReference{
            return FirebaseStorage.getInstance().reference.child("Profile_Images")
                .child(currentUserID())
        }
        fun getOtherProfilePicStorageRef(otherUserId:String):StorageReference{
            return FirebaseStorage.getInstance().reference.child("Profile_Images")
                .child(otherUserId)
        }
    }
}