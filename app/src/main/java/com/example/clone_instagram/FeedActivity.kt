package com.example.clone_instagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.clone_instagram.databinding.ActivityFeedBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class FeedActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFeedBinding

    //Firebase tanımlaması signout gibi yapılacak işlemler için
    private lateinit var auth :FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Firebase Instance initialise
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.feed_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_post){
            val intent = Intent(this@FeedActivity,UploadActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.signout){
            auth.signOut()
            val intent = Intent(this@FeedActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)

    }
}