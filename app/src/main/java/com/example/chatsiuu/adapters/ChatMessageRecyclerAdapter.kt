package com.example.chatsiuu.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsiuu.R
import com.example.chatsiuu.model.ChatMessageModel
import com.example.chatsiuu.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ChatMessageRecyclerAdapter(options: FirestoreRecyclerOptions<ChatMessageModel>, private val context:Context) :
    FirestoreRecyclerAdapter<ChatMessageModel, ChatMessageRecyclerAdapter.ChatModelViewHolder>(options) {

    inner class ChatModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val leftChatLayout=itemView.findViewById<LinearLayout>(R.id.left_chat_layout)
            val rightChatLayout=itemView.findViewById<LinearLayout>(R.id.right_chat_layout)
            val leftChatText=itemView.findViewById<TextView>(R.id.left_text_chatId)
            val rightChatText=itemView.findViewById<TextView>(R.id.right_text_chatId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatModelViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.chat_message_container,parent,false)
        return ChatModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatModelViewHolder, position: Int, model: ChatMessageModel) {
        if (model.SenderId.equals(FirebaseUtil.currentUserID())){
            holder.leftChatLayout.visibility=View.GONE
            holder.rightChatLayout.visibility=View.VISIBLE
            holder.rightChatText.text=model.message
        }else{
            holder.rightChatLayout.visibility=View.GONE
            holder.leftChatLayout.visibility=View.VISIBLE
            holder.leftChatText.text=model.message
        }
    }
}