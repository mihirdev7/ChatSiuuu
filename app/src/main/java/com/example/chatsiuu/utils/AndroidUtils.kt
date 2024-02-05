package com.example.chatsiuu.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class AndroidUtils {

    companion object{
        fun showToast(context:Context,message:String){
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
        }
        fun setProfilePic(context: Context,imageUri:Uri,imageView:ImageView){
            Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView)
        }
    }
}