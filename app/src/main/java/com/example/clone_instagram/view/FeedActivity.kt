package com.example.clone_instagram.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clone_instagram.R
import com.example.clone_instagram.adapter.FeedRecyclerAdapter
import com.example.clone_instagram.databinding.ActivityFeedBinding
import com.example.clone_instagram.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Query.Direction

class FeedActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFeedBinding

    //Firebase tanımlaması signout gibi yapılacak işlemler için
    private lateinit var auth :FirebaseAuth
    private lateinit var db : FirebaseFirestore

    private lateinit var postArrayList : ArrayList<Post>

    private lateinit var feedAdapter : FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Firebase Instance initialise
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        postArrayList = ArrayList<Post>() // () parantes boş dizi anlamında gelir

        getData()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        feedAdapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter = feedAdapter
    }

    private fun getData() {
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error != null){
                //hata var ise
                Toast.makeText(this@FeedActivity,error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                if (value != null){
                    if (!value.isEmpty){
                        // vlue null ve empty değilse
                        val documents = value.documents // Post içindeki tüm document'i liste şeklinde verdi
                        postArrayList.clear()
                        for (doc in documents){
                            // liste içindeki her docment'i tek tek gezebiliriz
                            // casting
                            val comment = doc.get("comment") as String
                            val useremail = doc.get("userEmail") as? String
                            val imageUrlInStorage = doc.get("imageUrlInStorage") as String

                            val post = Post(useremail!!,comment,imageUrlInStorage)
                            postArrayList.add(post)
                        }
                        feedAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.feed_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_post){
            val intent = Intent(this@FeedActivity, UploadActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.signout){
            auth.signOut()
            val intent = Intent(this@FeedActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)

    }
}