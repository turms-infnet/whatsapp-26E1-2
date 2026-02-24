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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import dev.tiagosilva.whatsappclone.R
import dev.tiagosilva.whatsappclone.data.User
import dev.tiagosilva.whatsappclone.services.FirebaseConfiguration
import dev.tiagosilva.whatsappclone.utils.Validations

class RegisterActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseConfiguration.getFirebaseAuth()
    private val firebaseDatabase = FirebaseConfiguration.getFirebaseDatabase()
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var passwordConfirmInput: TextInputEditText
    private lateinit var nameInput: TextInputEditText
    private lateinit var phoneInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        passwordConfirmInput = findViewById(R.id.editTextPasswordConfirm);
        nameInput = findViewById(R.id.editTextName);
        phoneInput = findViewById(R.id.editTextPhone);
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

    fun signUp(view: View) {
        if(!Validations.validateUserInputs(this, emailInput, passwordInput, passwordConfirmInput, nameInput, phoneInput)) return

        val emailInputText = emailInput.text.toString()
        val passwordInputText = passwordInput.text.toString()
        val displaNameInputText = nameInput.text.toString()
        val phoneInputText = phoneInput.text.toString()

        firebaseAuth.createUserWithEmailAndPassword(emailInputText, passwordInputText)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser ?: task.result?.user

                    val _user =
                        User(user!!.uid, displaNameInputText, emailInputText, phoneInputText, null)
                    firebaseDatabase.child("users").child(phoneInputText).setValue(_user)

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displaNameInputText)
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateProfileTask ->
                        if (updateProfileTask.isSuccessful) {
                            Toast.makeText(this, "Usuário cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Erro ao atualizar perfil do usuário", Toast.LENGTH_SHORT).show()
                        }
                    }


                } else {
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthWeakPasswordException -> "Senha fraca, digite uma senha mais forte"
                        is FirebaseAuthInvalidCredentialsException -> "Dados de usuário inválido."
                        is FirebaseAuthUserCollisionException -> "Usuário já cadastrado."
                        else -> "Erro ao cadastrar usuário: ${task.exception?.message}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun goToLoginPage(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}