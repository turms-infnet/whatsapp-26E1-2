package dev.tiagosilva.whatsappclone.utils

import com.google.android.material.textfield.TextInputEditText

class Validations {
    companion object {
        @JvmStatic
        fun validateUserInputs(
            emailInput: TextInputEditText,
            passwordInput: TextInputEditText,
            nameInput: TextInputEditText? = null,
        ) : Boolean {
            return true
        }
    }
}