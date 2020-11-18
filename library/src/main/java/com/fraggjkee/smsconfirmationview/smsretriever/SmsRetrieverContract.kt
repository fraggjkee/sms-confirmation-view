package com.fraggjkee.smsconfirmationview.smsretriever

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.phone.SmsRetriever

internal class SmsRetrieverContract : ActivityResultContract<Intent, String?>() {

    override fun createIntent(context: Context, input: Intent): Intent = input

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        if (resultCode != Activity.RESULT_OK || intent == null) {
            return null
        }

        return intent.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
    }
}