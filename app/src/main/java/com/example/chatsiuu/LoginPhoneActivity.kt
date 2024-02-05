package com.example.chatsiuu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.chatsiuu.databinding.ActivityPhoneLoginBinding

class LoginPhoneActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPhoneLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPhoneLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progresslogin.visibility = View.GONE
        binding.countrycode.registerCarrierNumberEditText(binding.numberid)
        binding.sendotpbtnid.setOnClickListener {
            if(!binding.countrycode.isValidFullNumber){
                binding.numberid.error = "Enter a Valid Number"
                return@setOnClickListener
            }
            val intent=Intent(this@LoginPhoneActivity,LoginOTPActivity::class.java)
            intent.putExtra("phone",binding.countrycode.fullNumberWithPlus)
            startActivity(intent)
            finish()
        }

    }
}