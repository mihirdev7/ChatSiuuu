package com.example.chatsiuu.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsiuu.ChatActivity
import com.example.chatsiuu.R
import com.example.chatsiuu.model.ChatMessageModel
import com.example.chatsiuu.model.ChatRoomModel
import com.example.chatsiuu.model.UserModel
import com.example.chatsiuu.utils.AndroidUtils
import com.example.chatsiuu.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth

class RecentChatMessageListAdapter(options: FirestoreRecyclerOptions<ChatRoomModel>, private val context:Context) :
    FirestoreRecyclerAdapter<ChatRoomModel, RecentChatMessageListAdapter.ChatroomModelViewHolder>(options) {

    inner class ChatroomModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image:ImageView=itemView.findViewById(R.id.chatListImageID)
        val name:TextView=itemView.findViewById(R.id.usertNameID)
        val lastmsg:TextView=itemView.findViewById(R.id.lastMsgID)
        val lastmsgtime:TextView=itemView.findViewById(R.id.lastMsgTimeID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomModelViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.chat_list_container,parent,false)
        return ChatroomModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatroomModelViewHolder, position: Int, model: ChatRoomModel) {
            FirebaseUtil.getOtherUserFromChatRoom(model.userIds!!).get().addOnCompleteListener { task->
                if(task.isSuccessful){
                    val lastMessageSentByMe : Boolean=model.lastMessageSenderId.equals(FirebaseUtil.currentUserID())
                    val otherUserModel:UserModel?=task.result?.toObject(UserModel::class.java)

                    if (otherUserModel != null) {
                        FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.userId).downloadUrl.addOnCompleteListener { t->
                            if(t.isSuccessful){
                                val uri: Uri = t.result
                                AndroidUtils.setProfilePic(context,uri,holder.image)
                            }
                        }
                    }

                    if (otherUserModel != null) {
                        holder.name.text=otherUserModel.username
                    }
                        if(lastMessageSentByMe && !model.lastMessage.isEmpty()){
                            holder.lastmsg.text="You : "+model.lastMessage
                        }else{
                            holder.lastmsg.text=model.lastMessage
                        }
                        holder.lastmsgtime.text=FirebaseUtil.timestampToString(model.lastMessageTimeStamp!!)

                    holder.itemView.setOnClickListener {
                        val intentc= Intent(context, ChatActivity::class.java)
                        intentc.putExtra("userid",otherUserModel?.userId)
                        intentc.putExtra("username",otherUserModel?.username)
                        intentc.putExtra("fcmtoken",otherUserModel?.fcmtoken)
                        intentc.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intentc)
                }
            }
       }
    }
}