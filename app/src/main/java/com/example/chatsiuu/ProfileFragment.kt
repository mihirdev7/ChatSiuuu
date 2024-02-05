package com.example.chatsiuu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.chatsiuu.model.UserModel
import com.example.chatsiuu.utils.AndroidUtils
import com.example.chatsiuu.utils.FirebaseUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.messaging.FirebaseMessaging


class ProfileFragment : Fragment() {
        private lateinit var currentUserModel : UserModel
    private lateinit var profilePic : ImageView
    private lateinit var userNameInput : EditText
    private lateinit var phoneInput : EditText
    private lateinit var updateProfileBtn : Button
    private lateinit var ProgressBar : ProgressBar
    private lateinit var logoutBtn : TextView
    private lateinit var imagePickLauncher : ActivityResultLauncher<Intent>
    private lateinit var selectedImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if(result.resultCode==Activity.RESULT_OK){
                val Data:Intent?=result?.data
                if(Data!=null && Data.data !=null){
                 selectedImageUri= Data.data!!
                    AndroidUtils.setProfilePic(requireContext(),selectedImageUri,profilePic)
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {

        val view:View= inflater.inflate(R.layout.fragment_profile, container, false)

         profilePic =view.findViewById(R.id.profile_image_view)
         userNameInput =view.findViewById(R.id.profile_username)
         phoneInput =view.findViewById(R.id.profile_phone)
         updateProfileBtn =view.findViewById(R.id.profle_update_btn)
         ProgressBar =view.findViewById(R.id.profile_progress_bar)
         logoutBtn =view.findViewById(R.id.logout_btn)

        getUserData()

        updateProfileBtn.setOnClickListener {
            updateBtnClick()
        }
        logoutBtn.setOnClickListener {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {task->
                if (task.isSuccessful){
                    FirebaseUtil.logout()
                    val intentb=Intent(requireContext(),SplashActivity::class.java)
                    intentb.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK )
                    startActivity(intentb)

                }
            }
        }
        profilePic.setOnClickListener {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                .createIntent {
                    imagePickLauncher.launch(it)
                    return@createIntent
                }
        }
        return view
    }

    private fun updateBtnClick() {
        val newUserName:String=userNameInput.text.toString()
        if(newUserName.isEmpty() || newUserName.length<3){
            userNameInput.error = "Username is too small"
            return }
            currentUserModel.username=newUserName
            setInProgress(true)

        if(selectedImageUri != null){
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri).addOnSuccessListener {task->
                updateToFirestore() }
        }else{
            updateToFirestore() }
    }
    private fun updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel).addOnCompleteListener {task->
            setInProgress(false)
            if(task.isSuccessful){
                AndroidUtils.showToast(requireContext(),"Updated Successfully")
            }else{
                AndroidUtils.showToast(requireContext(),"Update failed")
            }
        }
    }


    private fun getUserData() {
        setInProgress(true)
        FirebaseUtil.getCurrentProfilePicStorageRef().downloadUrl.addOnCompleteListener { task->
            if(task.isSuccessful){
                val uri:Uri=task.result
                AndroidUtils.setProfilePic(requireContext(),uri,profilePic)

            }
        }
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener { task->
            if(task.isSuccessful){
                currentUserModel=task.result.toObject(UserModel::class.java)!!
                userNameInput.setText(currentUserModel.username)
                phoneInput.setText(currentUserModel.phone)
                setInProgress(false)
            }
        }
    }
    fun setInProgress(inProgress:Boolean) {
        if (inProgress) {

            ProgressBar.visibility=View.VISIBLE
            updateProfileBtn.visibility=View.GONE
        } else {
            ProgressBar.visibility=View.GONE
            updateProfileBtn.visibility=View.VISIBLE
        }
    }


}