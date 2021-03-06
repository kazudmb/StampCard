package com.kazukinakano.stampcard

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : AppCompatActivity() {

    private val repository = DataRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        toolbar.title = getString(R.string.resetting_password)
        setSupportActionBar(toolbar)

        send_button.setOnClickListener {
            sendPasswordResetEmail()
        }
    }

    private fun sendPasswordResetEmail() {
        if(validateForm()){
            repository.auth.sendPasswordResetEmail(field_email.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, getString(R.string.email_sent_success), Toast.LENGTH_SHORT)
                            .show()
                        finish()
                        Log.d(TAG, getString(R.string.email_sent_success_log))
                    } else {
                        Toast.makeText(baseContext, getString(R.string.email_sent_failure), Toast.LENGTH_SHORT)
                            .show()
                        Log.w(TAG, getString(R.string.email_sent_failure_log))
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(baseContext, getString(R.string.input_correct_email), Toast.LENGTH_SHORT).show()
                    Log.w(TAG, getString(R.string.email_sent_failure_log), it)
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

        return valid
    }

    companion object {
        private const val TAG = "ResettingPassword"
    }
}
