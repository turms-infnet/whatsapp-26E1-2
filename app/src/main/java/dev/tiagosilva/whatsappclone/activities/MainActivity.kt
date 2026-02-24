package dev.tiagosilva.whatsappclone.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.adapters.MainTabsAdapter
import dev.tiagosilva.whatsappclone.data.Contact
import dev.tiagosilva.whatsappclone.services.Contacts
import dev.tiagosilva.whatsappclone.services.FirebaseConfiguration

class MainActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseConfiguration.getFirebaseAuth()
    private val PERMISSION_CODE = 100
    private val PERMISSION_LIST = arrayOf(
        android.Manifest.permission.READ_CONTACTS,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.title = "WhatsApp"
        setSupportActionBar(toolbar)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = MainTabsAdapter(supportFragmentManager)

        val tabs = findViewById<TabLayout>(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        checkAndRequestPermission()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkAndRequestPermission() {
        val listPermissionNeeded = ArrayList<String>();
        for(permission in PERMISSION_LIST) {
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(permission)
            }
        }

        if(listPermissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toTypedArray(), PERMISSION_CODE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu ?: return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.searchMenu -> true
            R.id.profileMenu -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.logoutMenu -> {
                firebaseAuth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_CODE) {
            val deniedPermission = ArrayList<String>()

            for(i in grantResults.indices) {
                Log.d("Analise de permissão", "Permissão negada: ${permissions[i]}")
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (permissions[i] == android.Manifest.permission.READ_CONTACTS) {
                        deniedPermission.add(permissions[i])
                    }
                }
            }

            if (deniedPermission.isEmpty()) {
                Toast.makeText(this, "Permissões concedidas", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "As seguintes permissões são obrigatórias nesse app: Lista de contatos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}