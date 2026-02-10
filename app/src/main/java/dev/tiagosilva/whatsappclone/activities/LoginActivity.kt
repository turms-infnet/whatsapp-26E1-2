package dev.tiagosilva.whatsappclone.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.services.FirebaseConfiguration

class LoginActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseConfiguration.getFirebaseAuth()
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
    }

    fun signIn(view: View) {
        println(emailInput.text.toString())
        println(passwordInput.text.toString())

        Toast.makeText(this@LoginActivity, "Login realizado com sucesso", Toast.LENGTH_LONG).show();
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finish()
    }

    fun forgotPassword(view: View) {
        println(emailInput.text.toString())

        Toast.makeText(this@LoginActivity, "Um e-mail de recuperação foi enviado para você.", Toast.LENGTH_LONG).show();
    }

    fun goToRegisterPage(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}