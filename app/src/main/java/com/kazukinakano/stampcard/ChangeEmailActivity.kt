package com.kazukinakano.stampcard

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.android.synthetic.main.activity_change_email.*

class ChangeEmailActivity : AppCompatActivity() {

    private val repository = DataRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)

        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        toolbar.title = getString(R.string.change_email)
        setSupportActionBar(toolbar)

        change_email_button.setOnClickListener {

            if (validateForm()) {
                val credential = EmailAuthProvider.getCredential(
                    repository.auth.currentUser?.email.toString(), field_password.text.toString()
                )

                // Prompt the user to re-provide their sign-in credentials
                repository.auth.currentUser?.reauthenticate(credential)?.addOnCompleteListener {
                    Log.d(TAG, getString(R.string.user_re_authenticated_log))
                    repository.auth.currentUser?.updateEmail(field_email.text.toString())
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                sendEmailVerification()
                                Log.d(TAG, getString(R.string.user_email_address_updated_success_log))
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    getString(R.string.user_email_address_updated_failure),
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.w(TAG, getString(R.string.user_email_address_updated_failure_log))
                            }
                        }
                }
            }
        }
    }

    private fun sendEmailVerification() {
        val user = repository.auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, getString(R.string.send_email_verification_success_log))
                    Toast.makeText(
                        baseContext,
                        getString(R.string.send_email_verification_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Log.e(TAG, getString(R.string.send_email_verification_failure_log), task.exception)
                    Toast.makeText(
                        baseContext,
                        getString(R.string.send_email_verification_failure),
                        Toast.LENGTH_SHORT
                    ).show()
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
        private const val TAG = "ChangeEmailActivity"
    }
}
