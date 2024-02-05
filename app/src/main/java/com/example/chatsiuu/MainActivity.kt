package com.example.chatsiuu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chatsiuu.model.UserModel
import com.example.chatsiuu.utils.AndroidUtils
import com.example.chatsiuu.utils.FirebaseUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView:BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        bottomNavigationView=findViewById(R.id.bottomNavigationView)
        val framelayout:FrameLayout=findViewById(R.id.framelayout)
        val searchUserID=findViewById<ImageButton>(R.id.searchUserID)

            val fragment1=HomeFragment()
            val fragment2=ReelFragment()
            val fragment3=ProfileFragment()

        bottomNavigationView.setOnItemSelectedListener {menuItem->
            when(menuItem.itemId){
               R.id.chat->{
                   loadFragment(fragment1)
                   return@setOnItemSelectedListener  true
               }R.id.status->{
                   loadFragment(fragment2)
                        return@setOnItemSelectedListener true
               }R.id.profile->{
                   loadFragment(fragment3)
                    return@setOnItemSelectedListener true
               }
            }
            false
        }
        loadFragment(fragment1)

        searchUserID.setOnClickListener {
            val intenta=Intent(this,SearchActivity::class.java)
            startActivity(intenta)
        }
        GetFCMToken()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.framelayout,fragment).commit()
    }

    private fun GetFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {task->
            if(task.isSuccessful){
                val token: String=task.result
                FirebaseUtil.currentUserDetails().update("fcmtoken",token)
            }
        }
    }

}