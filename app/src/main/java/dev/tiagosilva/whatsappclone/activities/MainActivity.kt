package dev.tiagosilva.whatsappclone.activities

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.data.Contact
import dev.tiagosilva.whatsappclone.services.Contacts

class MainActivity : AppCompatActivity() {
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

        checkAndRequestPermission()
//        val contacts: List<Contact> = Contacts.listContacts(this)
        
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