package com.example.clone_instagram.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.clone_instagram.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.util.UUID

class UploadActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUploadBinding

    //Firebase Instance for user Authentication
    private lateinit var auth : FirebaseAuth
    //Firebase Firestore NoSql veritabani
    private lateinit var firestore : FirebaseFirestore
    //Firebase storage - fotoğraf ve Videolar için
    private lateinit var storage : FirebaseStorage

    //ActivityResultLauncher - başka bir Activity'e gitmek ve oradan sonuç olarak bir şey döndürmek - Gallery
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>

    //Gallery den dönen görselin URI tutuyorum Bitmap'e çevirmeye gerek yok direkt Firebase'e yüklenebilir
    private var selectedPicture : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //acivityResultLauncher ve permissionLauncher - initialization
        registerLauncher()

        //Firebase Initialization
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = Firebase.storage

    }



    fun selectImage(view : View) {
        //Android SDK kontrolü
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //Android 33+ -> READ_MEDIA_IMAGES
            //Daha önce izin verilmiş mi ?
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                //izin yokmuş
                //izin isteme öncesi android rasyonel gösterilsin mi Android OS soralim
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){

                    //Anroid Rasyonel gösterilsin diye karar verdi ve True döndürdü
                    Snackbar.make(view,"Permission for Gallery is Required! To Upload Picture",Snackbar.LENGTH_INDEFINITE).setAction("Approve Permission"){
                        //request permission - izin istensin
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }.show()
                } else {
                    //request permission - android resyonel gösterilmesin diye karar verdi ancak yinede resyonel göstermeden izin istenecek kullanıcıdan
                    //permissionLauncher ile izin iste
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                // izin var elimizde , belki daha önce verilmiş - peki izinle ne işlem yapacağız ?
                // cevap galleryi gitcez ve fotoğraf alacağız
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //activityResultLauncher ile Gallery'e gidebilirim
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            // Android 32 -> READ_EXTERNAL_STORAGE
            //Daha önce izin verilmiş mi ?
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //izin yokmuş
                        //izin isteme öncesi android rasyonel gösterilsin mi Android OS soralim
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE )){
                            //Anroid Rasyonel gösterilsin diye karar verdi ve True döndürdü
                            Snackbar.make(view,"Permission for Gallery is Required! To Upload Picture",Snackbar.LENGTH_INDEFINITE).setAction("Approve Permission"){
                                //request permission - izin istensin
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }.show()
                        } else {
                            //request permission - android resyonel gösterilmesin diye karar verdi ancak yinede resyonel göstermeden izin istenecek kullanıcıdan
                            //permissionLauncher ile izin iste
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    } else {
                        // izin var elimizde , belki daha önce verilmiş - peki izinle ne işlem yapacağız ?
                        // cevap galleryi gitcez ve fotoğraf alacağız
                        val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        //activityResultLauncher ile Gallery'e gidebilirim
                        activityResultLauncher.launch(intentToGallery)
                    }
        }

    }

    @SuppressLint("SuspiciousIndentation")
    fun uploadButtonClicked(view : View){
        //yükelenecek fotoğraflar için rasgele unique isim üreteceğiz
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        //Firebase storage da reference ile gezineceğiz ve klasörler oluşturacağız
        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName) /*Storage'in için images klasörü yoksa oluştur ve içinde
        images.jpg isiminde fotoğrafa ulaş*/


        if (selectedPicture !=  null){

            imageReference.putFile(selectedPicture!!).addOnSuccessListener {

            //Upload başarılı olursa -> url - firestora kaydetmek gerek
            val uploadPictureReference = storage.reference.child("images").child(imageName) /* bu adres yolundaki dosyaya ulai*/
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val imageUrlInStorage = it.toString()

                    val postMap = hashMapOf<String,Any>()
                    postMap.put("imageUrlInStorage",imageUrlInStorage)
                    postMap.put("userEmail",auth.currentUser!!.email!!)
                    postMap.put("comment",binding.commentText.text.toString())
                    postMap.put("date",Timestamp.now())

                    //Post koleksiyon oluştur yoksa ve verilen koleksiyonu oraya ekle
                    firestore.collection("Posts").add(postMap).addOnSuccessListener {
                        finish()
                    }.addOnFailureListener{ it1 ->
                        Toast.makeText(this@UploadActivity,it1.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }
            }.addOnFailureListener{
                Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerLauncher() {
            //Biz bir intent yapacağız gallery gibi başka bir Activity'e gideceğiz ve oradan result olarak veri döndüreceğiz
            activityResultLauncher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK){
                //kullanıcı bir seçim yaptı ve bir görseli seçti
                val intentFromResult = result.data  // nullable intent tipinde görseli döndürdü
                if (intentFromResult != null){
                    selectedPicture = intentFromResult.data
                    selectedPicture?.let {
                        //null olmaktan çıkardım - diğer yöntem Bitmap'a çevirmek
                        binding.uploadImageView.setImageURI(it)
                        }
                    }
                }
            }

            permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()/*izin iste*/){ result ->
                // izin isteyecek ve sonucu Boolean döndürür
                if (result){
                    //permission granted - izin verildi
                    val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    //activityResultLauncher ile Gallery'e gidebilirim
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    //permission denied - izin verilmedi - işlem yapamıyoruz
                    Toast.makeText(this@UploadActivity,"Permission Needed!",Toast.LENGTH_LONG).show()
                }
            }
    }
}