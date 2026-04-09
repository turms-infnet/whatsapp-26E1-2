package dev.tiagosilva.whatsappclone.activities

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.services.FirebaseConfiguration
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import dev.tiagosilva.whatsappclone.services.Appwrite
import dev.tiagosilva.whatsappclone.utils.FileCast
import kotlinx.coroutines.launch
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

class ProfileActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseConfiguration.getFirebaseAuth()
    private val firebaseDatabase = FirebaseConfiguration.getFirebaseDatabase()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var circleImageView: ShapeableImageView
    private lateinit var sharedPreferences: SharedPreferences
    private var currentImageFile: java.io.File? = null

    private lateinit var phoneInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var nameInput: TextInputEditText
    private var imageUrl: String? = null


    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Glide.with(this).load(it).into(circleImageView)
            currentImageFile = FileCast.getFileFromUri(it, contentResolver, cacheDir)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            Glide.with(this).load(it).into(circleImageView)
            currentImageFile = FileCast.getFileFromBitmap(it, cacheDir)
        }
    }

    private val REQUEST_CODE_STORAGE = 1001
    private val REQUEST_CODE_CAMERA = 1002

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Permissão de armazenamento é necessária para selecionar imagens.", Toast.LENGTH_LONG).show()
        }
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE)
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Permissão de câmera é necessária para tirar fotos.", Toast.LENGTH_LONG).show()
        }
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("ProfileActivity", "Storage permission granted by user.")
                    galleryLauncher.launch("image/*")
                } else {
                    Toast.makeText(this, "Permissão de armazenamento negada.", Toast.LENGTH_LONG).show()
                }
            }
            REQUEST_CODE_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("ProfileActivity", "Camera permission granted by user.")
                    cameraLauncher.launch(null)
                } else {
                    Toast.makeText(this, "Permissão de câmera negada.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAnalytics = FirebaseConfiguration.getFirebaseAnalytics(this)

        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.title = "Perfil"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val gallery_button: FloatingActionButton = findViewById<FloatingActionButton>(R.id.gallery_button)
        val camera_button: FloatingActionButton = findViewById<FloatingActionButton>(R.id.camera_button)

        gallery_button.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.i("ProfileActivity", "Storage permission already granted. Launching gallery.")
                galleryLauncher.launch("image/*")
            } else {
                Log.w("ProfileActivity", "Storage permission not granted. Requesting permission.")
                requestStoragePermission()
            }
        }

        camera_button.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.i("ProfileActivity", "Camera permission already granted. Launching camera.")
                cameraLauncher.launch(null)
            } else {
                Log.w("ProfileActivity", "Camera permission not granted. Requesting permission.")
                requestCameraPermission()
            }
        }

        circleImageView = findViewById<ShapeableImageView>(R.id.circleImageView)
        nameInput = findViewById<TextInputEditText>(R.id.editTextName)
        phoneInput = findViewById<TextInputEditText>(R.id.editTextPhone)
        emailInput = findViewById<TextInputEditText>(R.id.editTextEmail)
        emailInput.isEnabled = false
        phoneInput.isEnabled = false

        setupThemeSelector()
        loadUserData()
        Appwrite.init(this)
    }

    fun loadUserData() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser?.email == null) return

        val email = currentUser.email!! // Safe to use !! after null check

        firebaseDatabase.child("users").orderByChild("email").equalTo(email).get().addOnSuccessListener  { snapshot ->
            if (snapshot.exists()) {
                val userNode = snapshot.children.first()

                emailInput.setText(userNode.child("email").value?.toString())
                phoneInput.setText(userNode.child("phone").value?.toString())

                // Modificáveis
                nameInput.setText(userNode.child("displayName").value?.toString())
                imageUrl = userNode.child("photoUrl").value?.toString()
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.padrao)
                    .into(circleImageView)
            }
        }
    }

    fun setupThemeSelector() {
        val themeSelector = findViewById<RadioGroup>(R.id.theme_selector)

        val savedTheme = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        when (savedTheme) {
            AppCompatDelegate.MODE_NIGHT_NO -> themeSelector.check(R.id.light_theme)
            AppCompatDelegate.MODE_NIGHT_YES -> themeSelector.check(R.id.dark_theme)
            else -> themeSelector.check(R.id.default_theme)
        }

        themeSelector.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.light_theme -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.dark_theme -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }

            sharedPreferences.edit().putInt("theme_mode", mode).apply()
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }


    fun update(@Suppress("UNUSED_PARAMETER") view: View) {
        lifecycleScope.launch {
            var url: String? = null
            if (currentImageFile !== null) {
                url = Appwrite.uploadImageAndGetUrl(currentImageFile!!)
            }
            val updates = mutableMapOf<String, Any>()
            url?.let { updates["photoUrl"] = it }
            nameInput.text?.toString()?.let { updates["displayName"] = it }

            if (updates.isNotEmpty()) {
                firebaseDatabase.child("users").child(phoneInput.text.toString()).updateChildren(updates).addOnSuccessListener { it ->
                    firebaseAnalytics.logEvent("update_profile") {
                        param("nameInput", nameInput.toString())
                        param("photoUrl", updates["photoUrl"].toString())
                    }
                    Toast.makeText(this@ProfileActivity, "Perfil atualizado com sucesso", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { exception ->
                    Toast.makeText(this@ProfileActivity, "Erro ao atualizar: ${exception.message} ", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}