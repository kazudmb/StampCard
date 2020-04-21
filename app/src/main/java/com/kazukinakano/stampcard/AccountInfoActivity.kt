package com.kazukinakano.stampcard

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_account_info.*

class AccountInfoActivity : AppCompatActivity() {

    private val repository = DataRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_info)

        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        toolbar.title = "アカウント情報"
        setSupportActionBar(toolbar)

        email.text = repository.auth.currentUser?.email ?: String.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_account_info, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                signOut()
                finish()
                return true
            }
            R.id.change_email -> {
                val intent = Intent(this, ChangeEmailActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.change_password -> {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        repository.auth.signOut()
        repository.numberOfVisits = 0
        Toast.makeText(baseContext, "ログアウトに成功しました", Toast.LENGTH_SHORT).show()
    }
}
