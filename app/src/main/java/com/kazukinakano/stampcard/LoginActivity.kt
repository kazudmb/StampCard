package com.kazukinakano.stampcard

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val repository = DataRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        toolbar.title = getString(R.string.login)
        setSupportActionBar(toolbar)

        login_button.setOnClickListener {
            signIn(field_email.text.toString(), field_password.text.toString())
        }

        new_registration.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        forgot_password.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        repository.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(TAG, getString(R.string.sign_in_with_email_success_log))
                    Toast.makeText(
                        baseContext,
                        getString(R.string.sign_in_with_email_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, getString(R.string.sign_in_with_email_failure_log), task.exception)
                    Toast.makeText(
                        baseContext,
                        getString(R.string.sign_in_with_email_failure),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                if (!task.isSuccessful) {
                }
            }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = field_email.text.toString()
        if (TextUtils.isEmpty(email)) {
            field_email.error = getString(R.string.input_text)
            valid = false
        } else {
            field_email.error = null
        }

        val password = field_password.text.toString()
        if (TextUtils.isEmpty(password)) {
            field_password.error = getString(R.string.input_text)
            valid = false
        } else {
            field_password.error = null
        }

        return valid
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}