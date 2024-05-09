package com.example.clone_instagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.clone_instagram.databinding.ActivityUploadBinding

class UploadActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }


    fun selectImage(view : View){

    }

    fun uploadButtonClicked(view : View){

    }
}