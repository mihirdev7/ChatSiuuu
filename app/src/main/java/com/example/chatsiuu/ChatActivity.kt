package com.example.chatsiuu

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsiuu.adapters.ChatMessageRecyclerAdapter
import com.example.chatsiuu.adapters.SearchUserRecyclerAdapter
import com.example.chatsiuu.databinding.ActivityChatBinding
import com.example.chatsiuu.model.ChatMessageModel
import com.example.chatsiuu.model.ChatRoomModel
import com.example.chatsiuu.model.UserModel
import com.example.chatsiuu.utils.AndroidUtils
import com.example.chatsiuu.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ChatActivity : AppCompatActivity() {
    private lateinit var chatRoomID:String
    private lateinit var userid:String
    private lateinit var token:String
    private lateinit var binding:ActivityChatBinding
    private lateinit var chatroommodel: ChatRoomModel
    private lateinit var chatMsgModel:ChatMessageModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userid=intent.extras?.get("userid").toString()
        token=intent.extras?.get("fcmtoken").toString()
        val name= intent.extras?.get("username").toString()

        chatRoomID=FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserID(),userid)
        binding.chatNameId.text=name

      getOrCreateChatRoom()
        setupChatRecyclerView()

        FirebaseUtil.getOtherProfilePicStorageRef(userid).downloadUrl.addOnCompleteListener { t->
            if(t.isSuccessful){
                val uri: Uri = t.result
                AndroidUtils.setProfilePic(this,uri,binding.chatImageId)
            }
        }

        binding.backbtnID.setOnClickListener {
            onBackPressed()
        }
        binding.sendbtnId.setOnClickListener {
            val message=binding.chatmessagetextID.text.toString()
            if(message.isEmpty()){
                return@setOnClickListener
            }
            sendMessageToUser(message)
        }
    }

    private fun sendNotification(message: String) {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener {task->
            if(task.isSuccessful){
                val currentUser:UserModel?=task.result?.toObject(UserModel::class.java)
                try {
                    val jsonObject= JSONObject()

                    val notificationObj =JSONObject()
                    notificationObj.put("title",currentUser?.username)
                    notificationObj.put("body",message)

                    val dataObj=JSONObject()
                    dataObj.put("userId",currentUser?.userId)

                    jsonObject.put("notification",notificationObj)
                    jsonObject.put("data",dataObj)
                    jsonObject.put("to",token)

                    callApi(jsonObject)

                }catch (e:Exception){}
            }
        }
    }

    fun callApi(jsonObject: JSONObject){
        val JSON:MediaType= "application/json".toMediaType()
        val client:OkHttpClient= OkHttpClient()
        val url:String="https://fcm.googleapis.com/fcm/send"
        val body:RequestBody=
            jsonObject.toString().toRequestBody(JSON)    //create(jsonObject.toString(),JSON)
        val request:Request=Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization","Bearer Your api key")
            .build()
        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {}
        })
    }

    private fun setupChatRecyclerView() {
        val query: Query =FirebaseUtil.getChatRoomMessageReference(chatRoomID).orderBy("timestamp",Query.Direction.DESCENDING)
        val options: FirestoreRecyclerOptions<ChatMessageModel> = FirestoreRecyclerOptions.Builder<ChatMessageModel>()
            .setQuery(query, ChatMessageModel::class.java).build()

        val layoutManager= LinearLayoutManager(this)
        layoutManager.reverseLayout=true
        binding.recycleChatId.layoutManager=layoutManager
        val adapter= ChatMessageRecyclerAdapter(options,this)
        binding.recycleChatId.adapter=adapter
        adapter.startListening()
       val adapterDataObserver=object:RecyclerView.AdapterDataObserver(){
           override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
               super.onItemRangeInserted(positionStart, itemCount)
                binding.recycleChatId.smoothScrollToPosition(0)
           }
       }
        adapter.registerAdapterDataObserver(adapterDataObserver)
    }

    private fun sendMessageToUser(message: String) {

        chatroommodel.lastMessageSenderId=FirebaseUtil.currentUserID()
        chatroommodel.lastMessageTimeStamp= Timestamp.now()
        chatroommodel.lastMessage=message
        FirebaseUtil.getChatRoomReference(chatRoomID).set(chatroommodel)

        chatMsgModel= ChatMessageModel(message,FirebaseUtil.currentUserID(),Timestamp.now())
        FirebaseUtil.getChatRoomMessageReference(chatRoomID).add(chatMsgModel).addOnSuccessListener {
            binding.chatmessagetextID.text=null
            sendNotification(message)

        }.addOnFailureListener {
            Toast.makeText(this,"error",Toast.LENGTH_SHORT).show()
        }
    }



    private fun getOrCreateChatRoom() {

        FirebaseUtil.getChatRoomReference(chatRoomID).get().addOnCompleteListener {task->
            if(task.isSuccessful) {
                val chatRoomDocument = task.result
                if (chatRoomDocument != null && chatRoomDocument.exists()) {
                    chatroommodel = chatRoomDocument.toObject(ChatRoomModel::class.java)!!

                    if (chatroommodel == null) {
                        Toast.makeText(this, "chat room data is missing", Toast.LENGTH_SHORT).show()
                    } else {
                        //Toast.makeText(this, "yoooooooo", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                }else{
                     chatroommodel=ChatRoomModel(chatRoomID, listOf(FirebaseUtil.currentUserID(),userid), Timestamp.now(),"")
                }
                FirebaseUtil.getChatRoomReference(chatRoomID).set(chatroommodel).addOnSuccessListener {
                    Toast.makeText(this, "new data stored", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "failed to load new data", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

