package dev.tiagosilva.whatsappclone.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.services.FirebaseConfiguration
import dev.tiagosilva.whatsappclone.utils.Validations

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

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signIn(view: View) {
        if(!Validations.validateUserInputs(this, emailInput, passwordInput)) return

        val emailInputText = emailInput.text.toString()
        val passwordInputText = passwordInput.text.toString()

        firebaseAuth.signInWithEmailAndPassword(emailInputText, passwordInputText)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.d("Erro", task.exception.toString())
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException, is FirebaseAuthInvalidUserException -> "Senha fraca, digite uma senha mais forte"
                        else -> "Erro ao realizar login de usuário: ${task.exception?.message}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun forgotPassword(view: View) {
        println(emailInput.text.toString())

        Toast.makeText(this@LoginActivity, R.string.message_forgot_password_success, Toast.LENGTH_LONG).show();
    }

    fun goToRegisterPage(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}