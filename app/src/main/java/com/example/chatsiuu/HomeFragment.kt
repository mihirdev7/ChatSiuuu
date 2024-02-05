package com.example.chatsiuu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatsiuu.adapters.RecentChatMessageListAdapter
import com.example.chatsiuu.adapters.SearchUserRecyclerAdapter
import com.example.chatsiuu.model.ChatRoomModel
import com.example.chatsiuu.model.UserModel
import com.example.chatsiuu.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query


class HomeFragment : Fragment() {
    private lateinit var recyclervw:RecyclerView
    private lateinit var adapter:RecentChatMessageListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view : View= inflater.inflate(R.layout.fragment_home, container, false)
        recyclervw=view.findViewById(R.id.recyclerViewID)
        setUpSearchRecyclerView()
        return view
    }


    private fun setUpSearchRecyclerView() {
        val query: Query = FirebaseUtil.allChatRoomCollectionReference().whereArrayContains("userIds",FirebaseUtil.currentUserID())
                .orderBy("lastMessageTimeStamp",Query.Direction.DESCENDING)

        val options: FirestoreRecyclerOptions<ChatRoomModel> = FirestoreRecyclerOptions.Builder<ChatRoomModel>()
            .setQuery(query, ChatRoomModel::class.java).build()

        val layoutManager= LinearLayoutManager(requireContext())
        recyclervw.layoutManager=layoutManager
         adapter= RecentChatMessageListAdapter(options,requireContext())
        recyclervw.adapter=adapter
        adapter.startListening()
    }

    override fun onStart() {
        super.onStart()
        if(adapter != null){
            adapter.startListening()
        }
    }

    override fun onResume() {
        super.onResume()
        if(adapter != null){
            adapter.notifyDataSetChanged()
        }
    }

    override fun onStop() {
        super.onStop()
        if(adapter != null){
            adapter.stopListening()
        }
    }
}