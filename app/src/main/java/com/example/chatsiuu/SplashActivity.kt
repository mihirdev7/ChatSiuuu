package com.example.chatsiuu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.chatsiuu.model.UserModel
import com.example.chatsiuu.utils.AndroidUtils
import com.example.chatsiuu.utils.FirebaseUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.toObject

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val id=FirebaseAuth.getInstance().currentUser?.uid

            if (id !=null){
                val intent3=Intent(this,MainActivity::class.java)
                startActivity(intent3)
            }else {
                val intent = Intent(this, LoginPhoneActivity::class.java)
                startActivity(intent)
            }
            finish()
        },2000)

        /*if(this.intent.extras != null){
            //from notification
            val userIds =intent.extras?.getString("userId").toString()

                FirebaseUtil.allUserCollectionReference().document(userIds).get().addOnCompleteListener {task->
                    if(task.isSuccessful){

                        val user: UserModel? =task.result?.toObject(UserModel::class.java)

                        val mainIntent=Intent(this,MainActivity::class.java)
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        startActivity(mainIntent)

                        val intent5= Intent(this,ChatActivity::class.java)
                        intent5.putExtra("userid",user?.userId)
                        intent5.putExtra("username",user?.username)
                        intent5.putExtra("fcmtoken",user?.fcmtoken)
                        intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent5)
                        finish()
                    }
                }.addOnFailureListener {
                    Log.e("errorss",it.toString())
                    AndroidUtils.showToast(this,it.toString())
                }

        }else{
            Handler(Looper.getMainLooper()).postDelayed({
                val id=FirebaseAuth.getInstance().currentUser?.uid

                if (id !=null){
                    val intent3=Intent(this,MainActivity::class.java)
                    startActivity(intent3)
                }else {
                    val intent = Intent(this, LoginPhoneActivity::class.java)
                    startActivity(intent)
                }
                finish()
            },2000)
        }*/

    }
}