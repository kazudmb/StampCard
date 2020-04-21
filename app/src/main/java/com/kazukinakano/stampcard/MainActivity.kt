package com.kazukinakano.stampcard

import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val repository = DataRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository.auth = FirebaseAuth.getInstance()

        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        toolbar.title = "Stamp Card"
        setSupportActionBar(toolbar)

        button.setOnClickListener {
            if (repository.auth.currentUser == null) {
                Toast.makeText(baseContext, "ログインしてください", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, QRCodeDisplayActivity::class.java)
                startActivity(intent)
            }
        }

        swipe_refresh_layout.setOnRefreshListener {
            MyTask().execute()
        }
    }

    public override fun onStart() {
        super.onStart()
    }

    public override fun onResume() {
        super.onResume()
        getNumberOfVisits()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (repository.auth.currentUser == null) {
            menu?.findItem(R.id.accountInfo)?.isVisible = false
            menu?.findItem(R.id.login)?.isVisible = true
        } else {
            menu?.findItem(R.id.accountInfo)?.isVisible = true
            menu?.findItem(R.id.login)?.isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.login -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.accountInfo -> {
                val intent = Intent(this, AccountInfoActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getNumberOfVisits() {
        if (repository.auth.currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .document(repository.auth.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { result ->
                    repository.numberOfVisits = result.data?.get("NumberOfVisits") as Long
                    setNumberOfVisitsAndRank()
                    setStamp()
                    Log.d(TAG, "Success getting documents.")
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        } else {
            return
        }
    }

    private fun setNumberOfVisitsAndRank() {
        number_of_visits.text = "来店回数：" + repository.numberOfVisits + "回"
        when {
            repository.numberOfVisits < 40 -> rank.text = "会員ランク：メンバー"
            repository.numberOfVisits < 80 -> rank.text = "会員ランク：シルバーVIP"
            repository.numberOfVisits >= 80 -> rank.text = "会員ランク：ゴールドVIP"
        }
    }

    private fun setStamp() {

        val stamp = resources.getDrawable(R.drawable.logo_stamp_area_icon)
        val approved = resources.getDrawable(R.drawable.logo_approved)
        val layers = arrayOf<Drawable>(stamp, approved)
        val layerDrawable = LayerDrawable(layers)

        for (i in 1..10) {
            var name = "stamp_area$i"
            var id = resources.getIdentifier(name, "id", packageName)
            findViewById<ImageView>(id).setImageDrawable(stamp)
        }

        var loopCount: Int
        val numberOfVisits = repository.numberOfVisits.toString()
        val numberOfCutOut = numberOfVisits.substring(numberOfVisits.length - 1).toInt()

        if (numberOfCutOut == 0 && numberOfVisits.toInt() < 10) {
            // do nothing
            return
        } else {
            loopCount = if (numberOfCutOut == 0) {
                10
            } else {
                numberOfCutOut
            }
        }

        for (i in 1..loopCount) {
            val name = "stamp_area$i"
            val id = resources.getIdentifier(name, "id", packageName)
            findViewById<ImageView>(id).setImageDrawable(layerDrawable)
        }
    }

    inner class MyTask : AsyncTask<Void, String, Void>() {

        override fun doInBackground(vararg p0: Void?): Void? {
            getNumberOfVisits()
            Thread.sleep(1000)
//            publishProgress()
            return null
        }

        override fun onProgressUpdate(vararg values: String?) {
            // do nothing
        }

        override fun onPostExecute(result: Void?) {
            swipe_refresh_layout.isRefreshing = false
        }
    }

    companion object {
        private val TAG = "MainActivity"
    }
}