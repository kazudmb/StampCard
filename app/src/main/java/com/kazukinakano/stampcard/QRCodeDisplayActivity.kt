package com.kazukinakano.stampcard

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class QRCodeDisplayActivity : AppCompatActivity() {

    private val repository = DataRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)

        val dp = 300
        val scale = resources.displayMetrics.density
        val size = (dp * scale + 0.5f).toInt()

        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(repository.auth.uid.toString(), BarcodeFormat.QR_CODE, size, size)
            val imageQr = findViewById<ImageView>(R.id.qr_code)
            imageQr.setImageBitmap(bitmap)
            Log.d(TAG, getString(R.string.generate_qrcode_success_log))
        } catch (exception: Exception) {
            Log.w(TAG, getString(R.string.generate_qrcode_failure_log), exception)
        }
    }

    companion object {
        private const val TAG = "QRCodeDisplayActivity"
    }
}