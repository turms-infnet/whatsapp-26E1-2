package dev.tiagosilva.whatsappclone.utils

import android.content.Context
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
import dev.tiagosilva.whatsappclone.R

class Validations {
    companion object {
        @JvmStatic
        fun validateUserInputs(
            context: Context,
            emailInput: TextInputEditText,
            passwordInput: TextInputEditText,
            passwordConfirmInput: TextInputEditText? = null,
            nameInput: TextInputEditText? = null,
            phoneInput: TextInputEditText? = null,
        ) : Boolean {
            if (nameInput != null) {
                if (nameInput.text.isNullOrEmpty() || nameInput.text.isNullOrBlank()) {
                    nameInput.error = context.getString(R.string.error_name_required)
                    return false
                }
            }

            if (phoneInput != null) {
                if (phoneInput.text.isNullOrEmpty() || phoneInput.text.isNullOrBlank()) {
                    phoneInput.error = context.getString(R.string.error_phone_required)
                    return false
                }

                if (phoneInput.text!!.length < 9) {
                    phoneInput.error = context.getString(R.string.error_phone_length)
                    return false
                }
            }

            val emailText = emailInput.text
            if (emailText.isNullOrEmpty() || emailText.isNullOrBlank()) {
                emailInput.error = context.getString(R.string.error_email_required)
                return false
            }

            val patternEmail = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
            if (!patternEmail.matches(emailText.toString())) {
                emailInput.error = context.getString(R.string.error_email_not_valid)
                return false
            }
            if (passwordInput.text.isNullOrEmpty() || passwordInput.text.isNullOrBlank()) {
                passwordInput.error = context.getString(R.string.error_password_required)
                return false
            }

            if (passwordInput.text!!.length < 6) {
                passwordInput.error = context.getString(R.string.error_password_should_be_at_least_6_characters)
                return false
            }

            val patternPassword = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{6,}\$")
            if (!patternPassword.matches(passwordInput.text.toString())) {
                passwordInput.error = context.getString(R.string.error_password_should_contain_at_least_one_uppercase_letter)
                return false
            }

            if (passwordConfirmInput != null) {
                if (passwordConfirmInput.text.isNullOrEmpty() || passwordConfirmInput.text.isNullOrBlank()) {
                    passwordConfirmInput.error = context.getString(R.string.error_password_confirm_required)
                    return false
                }
                if (passwordConfirmInput.text.toString() != passwordInput.text.toString()) {
                    passwordConfirmInput.error = context.getString(R.string.error_password_should_be_equal)
                }
            }


            return true
        }
    }
}