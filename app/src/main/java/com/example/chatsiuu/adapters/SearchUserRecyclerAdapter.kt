package com.example.chatsiuu.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsiuu.ChatActivity
import com.example.chatsiuu.R
import com.example.chatsiuu.model.ChatMessageModel
import com.example.chatsiuu.model.UserModel
import com.example.chatsiuu.utils.AndroidUtils
import com.example.chatsiuu.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth

class SearchUserRecyclerAdapter(options: FirestoreRecyclerOptions<UserModel>, private val context:Context) :
    FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder>(options) {

    inner class UserModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image:ImageView=itemView.findViewById(R.id.searchImageID)
        val name:TextView=itemView.findViewById(R.id.searchNameID)
        val number:TextView=itemView.findViewById(R.id.searchNumberID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserModelViewHolder {
       val view=LayoutInflater.from(context).inflate(R.layout.search_container,parent,false)
        return UserModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserModelViewHolder, position: Int, model: UserModel) {
        holder.name.text=model.username
        holder.number.text=model.phone
        var user=FirebaseAuth.getInstance().currentUser?.uid

      if(model.userId.equals(user)){
            holder.name.text= "${model.username} (Me)"
        }

        FirebaseUtil.getOtherProfilePicStorageRef(model.userId).downloadUrl.addOnCompleteListener { t->
            if(t.isSuccessful){
                val uri: Uri = t.result
                AndroidUtils.setProfilePic(context,uri,holder.image)
            }
        }
        holder.itemView.setOnClickListener {
            val intentd= Intent(context, ChatActivity::class.java)
            intentd.putExtra("userid",model.userId)
            intentd.putExtra("username",model.username)
            intentd.putExtra("fcmtoken",model.fcmtoken)
            intentd.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intentd)
        }
    }
}