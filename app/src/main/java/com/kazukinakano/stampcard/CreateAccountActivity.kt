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
        toolbar.title = "新規登録"
        setSupportActionBar(toolbar)

        new_registration.setOnClickListener{
            createAccount(field_email.text.toString(), field_password.text.toString())
        }
    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        // [START create_user_with_email]
        repository.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    createCollection()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "新規登録に失敗しました", Toast.LENGTH_SHORT).show()
                }
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        // [END create_user_with_email]
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = field_email.text.toString()
        if (TextUtils.isEmpty(email)) {
            field_email.error = "入力してください"
            valid = false
        } else {
            field_email.error = null
        }

        val password = field_password.text.toString()
        if (TextUtils.isEmpty(password)) {
            field_password.error = "入力してください"
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
                Log.w(TAG, "Error adding document", e)
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
                    Toast.makeText(baseContext, "登録確認メールが送信されました", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.e(TAG, "sendEmailVerification:failure", task.exception)
                    Toast.makeText(baseContext, "登録確認メールの送信が失敗しました", Toast.LENGTH_SHORT).show()
                }
                // [END_EXCLUDE]
            }
        // [END send_email_verification]
    }

    companion object {
        private const val TAG = "CreateAccountActivity"
    }
}
