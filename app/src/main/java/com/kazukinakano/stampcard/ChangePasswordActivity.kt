package com.kazukinakano.stampcard

import android.os.Bundle
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
        toolbar.title = "パスワードの再設定"
        setSupportActionBar(toolbar)

        send_button.setOnClickListener {
            sendPasswordResetEmail()
        }
    }

    private fun sendPasswordResetEmail() {
        repository.auth.sendPasswordResetEmail(field_email.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "再設定メールが送信されました", Toast.LENGTH_SHORT).show()
                    finish()
                    Log.d(TAG, "Email sent:success")
                } else {
                    Toast.makeText(baseContext, "再設定メールの送信に失敗しました", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "Email sent:failure")
                }
            }
            .addOnFailureListener {
                Toast.makeText(baseContext, "正しいメールアドレスを入力してください", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Email sent:failure", it)
            }
    }

        companion object {
        private const val TAG = "ResettingPassword"
    }
}
