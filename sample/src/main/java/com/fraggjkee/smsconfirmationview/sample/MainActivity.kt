package com.fraggjkee.smsconfirmationview.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fraggjkee.smsconfirmationview.SmsConfirmationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = findViewById<SmsConfirmationView>(R.id.sms_code_view)
        view.onChangeListener = SmsConfirmationView.OnChangeListener { code, isComplete ->
            Toast.makeText(this, "value: $code, isComplete: $isComplete", Toast.LENGTH_SHORT)
                .show()
        }

        view.startListeningForIncomingMessages()
    }
}
