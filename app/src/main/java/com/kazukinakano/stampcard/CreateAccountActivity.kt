package com.kazukinakano.stampcard

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_user.*

class CreateAccountActivity : AppCompatActivity() {

    private val repository = DataRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        toolbar.title = getString(R.string.new_registration)
        setSupportActionBar(toolbar)

        new_registration.setOnClickListener {
            createAccount(field_email.text.toString(), field_password.text.toString())
        }
    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        repository.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(TAG, getString(R.string.create_user_with_email_success_log))
                    createCollection()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, getString(R.string.create_user_with_email_failure_log), task.exception)
                    Toast.makeText(
                        baseContext,
                        getString(R.string.create_user_with_email_failure),
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

    private fun createCollection() {
        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "NumberOfVisits" to 0
        )

        // Add a new document with a generated ID
        db.collection("users")
            .document(repository.auth.currentUser?.uid.toString())
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: ${repository.auth.currentUser?.uid.toString()}")
                repository.numberOfVisits = 0
                sendEmailVerification()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, getString(R.string.adding_document_failure_log), e)
            }
    }

    private fun sendEmailVerification() {
        val user = repository.auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, getString(R.string.send_email_verification_of_registration_success_log))
                    Toast.makeText(
                        baseContext,
                        getString(R.string.send_email_verification_of_registration_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Log.e(TAG, getString(R.string.send_email_verification_of_registration_failure_log), task.exception)
                    Toast.makeText(
                        baseContext,
                        getString(R.string.send_email_verification_of_registration_failure),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    companion object {
        private const val TAG = "CreateAccountActivity"
    }
}
