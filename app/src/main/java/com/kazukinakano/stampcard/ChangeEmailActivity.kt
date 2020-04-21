package com.kazukinakano.stampcard

import android.os.Bundle
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
        toolbar.title = "メールアドレス変更"
        setSupportActionBar(toolbar)

        change_email_button.setOnClickListener {

            val credential = EmailAuthProvider.getCredential(
                repository.auth.currentUser?.email.toString(), field_password.text.toString()
            )

            // Prompt the user to re-provide their sign-in credentials
            repository.auth.currentUser?.reauthenticate(credential)?.addOnCompleteListener {
                Log.d(TAG, "User re-authenticated.")
                repository.auth.currentUser?.updateEmail(field_email.text.toString())
                sendEmailVerification()
            }
        }
    }

    private fun sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        val user = repository.auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this) { task ->
                // [START_EXCLUDE]
                if (task.isSuccessful) {
                    Log.d(TAG, "sendEmailVerification:success")
                    Toast.makeText(baseContext, "変更確認メールが送信されました", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.e(TAG, "sendEmailVerification:failure", task.exception)
                    Toast.makeText(baseContext, "変更確認メールの送信が失敗しました", Toast.LENGTH_SHORT).show()
                }
                // [END_EXCLUDE]
            }
        // [END send_email_verification]
    }

    companion object {
        private const val TAG = "ChangeEmailActivity"
    }
}
