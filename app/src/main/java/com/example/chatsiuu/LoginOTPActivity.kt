package com.example.chatsiuu

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.chatsiuu.databinding.ActivityLoginOtpactivityBinding
import com.example.chatsiuu.utils.AndroidUtils
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.PhoneAuthProvider.verifyPhoneNumber
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit


class LoginOTPActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginOtpactivityBinding
    private lateinit var Mauth:FirebaseAuth
    private lateinit var verificationCode : String
    private lateinit var forceResendingToken: ForceResendingToken
    private lateinit var number:String
    private var timer:Timer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Mauth=FirebaseAuth.getInstance()

        val number=intent.extras!!.getString("phone").toString()
            sendOTP(number,false)

        binding.otpbtnid.setOnClickListener {
            val otpnumber:String= binding.enterotpid.text.toString()
            if(otpnumber.isEmpty()){
                Toast.makeText(this,"Enter OTP",Toast.LENGTH_SHORT).show()
            }else{
                val credential2: PhoneAuthCredential=PhoneAuthProvider.getCredential(verificationCode,otpnumber)
                signInWithPhoneAuthCredential(credential2)
                setInProgress(true)
            }

        }
        binding.resendotptext.setOnClickListener {
            sendOTP(number,false)
        }
    }

    fun sendOTP( phonenumber:String,isSend:Boolean) {
        startResendTimer()
        setInProgress(true)

        val options=PhoneAuthOptions.newBuilder(Mauth)
            .setPhoneNumber(phonenumber)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : OnVerificationStateChangedCallbacks(){

                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(p0)
                    //signIn(p0)
                    setInProgress(false)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    AndroidUtils.showToast(this@LoginOTPActivity,"OTP verification failed")
                    setInProgress(false)
                }

                override fun onCodeSent(p0: String, p1: ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    verificationCode=p0
                    forceResendingToken=p1
                    AndroidUtils.showToast(this@LoginOTPActivity,"OTP sent successsfully")
                    setInProgress(false)
                }
            })
        if(isSend){
            verifyPhoneNumber(options.setForceResendingToken(forceResendingToken).build())
        }else{
            verifyPhoneNumber(options.build())
        }
    }

    fun setInProgress(inProgress:Boolean) {
        if (inProgress) {
            binding.progressotpid.visibility = View.VISIBLE
            binding.otpbtnid.visibility = View.GONE
        } else {
            binding.progressotpid.visibility = View.GONE
            binding.otpbtnid.visibility = View.VISIBLE
        }
    }
            private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){
                setInProgress(true)
                Mauth.signInWithCredential(credential).addOnCompleteListener(this){task ->
                    setInProgress(false)
                    if(task.isSuccessful){
                        val intent2=Intent(this,LoginUserNameActivity::class.java)
                        val number=intent.extras!!.getString("phone")
                        intent2.putExtra("phoneno",number)
                        startActivity(intent2)
                            finish()
                    }else{
                        AndroidUtils.showToast(this@LoginOTPActivity,"Wrong OTP")
                    }
                }
            }
    private fun startResendTimer() {
        binding.resendotptext.isClickable=false

        timer?.cancel()
        timer=null
        var timeOutSeconds=30L
        timer=Timer()
        timer?.schedule(object :TimerTask(){
            override fun run() {
                timeOutSeconds--
                runOnUiThread {
                    binding.resendotptext.text="Resend OTP in "+ timeOutSeconds +" Seconds"
            }
                if(timeOutSeconds<=0){
                timeOutSeconds=30L
                    timer?.cancel()
                    runOnUiThread {
                        binding.resendotptext.isClickable=true
                        binding.resendotptext.text="Resend OTP"
                        binding.resendotptext.setTextColor(Color.BLUE)
                    }
                }
                } },0,1000)}
    }
