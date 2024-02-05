package com.example.chatsiuu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatsiuu.adapters.SearchUserRecyclerAdapter
import com.example.chatsiuu.databinding.ActivitySearchBinding
import com.example.chatsiuu.model.ChatMessageModel
import com.example.chatsiuu.model.UserModel
import com.example.chatsiuu.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

class SearchActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySearchBinding
    private var adapter:SearchUserRecyclerAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edittextID.requestFocus()

        binding.backbtnID.setOnClickListener {
            onBackPressed()
        }

        binding.searchbtnID.setOnClickListener {
            val searchName=binding.edittextID.text.toString()
            if(searchName.isEmpty() || searchName.length<3){
                binding.edittextID.error = "Invalid Username"
                return@setOnClickListener
            }
            setUpSearchRecyclerView(searchName)
        }
    }

    private fun setUpSearchRecyclerView(searchName: String) {
        val user=FirebaseUtil.currentUserDetails().toString()
        val query:Query=FirebaseUtil.allUserCollectionReference().whereEqualTo("username",searchName)
        val options:FirestoreRecyclerOptions<UserModel> = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query,UserModel::class.java).build()


        adapter=SearchUserRecyclerAdapter(options,this)
        val layoutManager=LinearLayoutManager(this)
        binding.recycleSearchID.layoutManager=layoutManager
        if(adapter !=null){
            binding.recycleSearchID.adapter=adapter
            adapter!!.startListening()
        }

    }

    override fun onStart() {
        super.onStart()
            adapter?.startListening()
    }
    override fun onStop() {
        super.onStop()
            adapter?.stopListening()
    }
    override fun onResume() {
        super.onResume()
            adapter?.startListening()
    }

}