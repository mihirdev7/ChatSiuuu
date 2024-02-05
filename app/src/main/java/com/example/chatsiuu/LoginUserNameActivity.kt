package com.example.chatsiuu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.chatsiuu.databinding.ActivityLoginUserNameBinding
import com.example.chatsiuu.model.ChatMessageModel
import com.example.chatsiuu.model.UserModel
import com.example.chatsiuu.utils.FirebaseUtil
import com.google.firebase.Timestamp
import com.google.firebase.messaging.FirebaseMessaging

class LoginUserNameActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginUserNameBinding
    private lateinit var usermodel:UserModel
    private lateinit var number:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginUserNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
         number=intent.extras!!.getString("phoneno").toString()
        getUsername()

       binding.usernamebtnid.setOnClickListener {
            setUsername() }
    }

    private fun setUsername() {
        val userName=binding.usernametextid.text.toString()
        if(userName.isEmpty() || userName.length<3){
            binding.usernametextid.error = "Username is too small"
            return }
        setInProgress(true)

        if(!::usermodel.isInitialized){
            usermodel=UserModel(number, userName, FirebaseUtil.currentUserID(), Timestamp.now())
        }else{
            usermodel.username=userName
        }
        FirebaseUtil.currentUserDetails().set(usermodel).addOnCompleteListener {
            setInProgress(false)
            if(it.isSuccessful){
                val intent2=Intent(this,MainActivity::class.java)
                //intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                //intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent2)
                finish()
            }
            else{
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUsername() {
        //setInProgress(true)
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener {
            setInProgress(false)
            if (it.isSuccessful) {
                val userDoc=it.result
                if(userDoc!=null && userDoc.exists()){
                   usermodel= userDoc.toObject(UserModel::class.java)!!

                    if(usermodel!=null){
                        binding.usernametextid.setText(usermodel.username)
                    }else{
                        Toast.makeText(this, "User document doesn't exist", Toast.LENGTH_SHORT).show() }
                }else{
                    Toast.makeText(this,"Enter a name",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Enter a Username",Toast.LENGTH_SHORT).show()
            }
        }
        }


    fun setInProgress(inProgress:Boolean) {
        if (inProgress) {
            binding.progressusernameid.visibility = View.VISIBLE
            binding.usernamebtnid.visibility = View.GONE
        } else {
            binding.progressusernameid.visibility = View.GONE
            binding.usernamebtnid.visibility = View.VISIBLE
        }
    }
}