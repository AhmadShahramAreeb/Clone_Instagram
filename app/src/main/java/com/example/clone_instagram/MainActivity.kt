package com.example.clone_instagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.clone_instagram.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    //Declaration of Firebase Instance
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view =binding.root
        setContentView(view)

        //Firebase Instance Initialization
        //auth = FirebaseAuth.getInstance()
        auth = Firebase.auth

        /*Kullancı giriş sonrası Uygulamayı kapatırsa sürekli tekrar giriş yapmamak için
        * Firebase dan giriş bilgilerini isteyelim*/
        val currentUser = auth.currentUser
        if (currentUser != null){
            val intent = Intent(this@MainActivity,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signInClicked(view : View){
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                //Succed
                val intent = Intent(this@MainActivity,FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        } else {
            // email veya password Boş
            Toast.makeText(this@MainActivity,"Enter Email or Password Please",Toast.LENGTH_LONG).show()
        }

    }

    fun signUpClicked(view : View){
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()){

            /*Neden addOnSuccessListener veya addOnFailureListener kullandık?
            * çünkü Firebase sunucusundan kullancı oluştur talebimizin sonucunu bilmiyoruz*/
            auth.createUserWithEmailAndPassword(email ,password).addOnSuccessListener {
                //Succed dönerse burası çalışır
                val intent = Intent(this@MainActivity,FeedActivity::class.java)
                startActivity(intent)
                finish() // bu ekrani kapatti
            }.addOnFailureListener {
                //it.localizedMesaage - hatayı basitçe kullanıcıya anlatır
                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }

        } else  {
            Toast.makeText(this,"Enter Email and Password!",Toast.LENGTH_LONG).show()
        }
    }
}