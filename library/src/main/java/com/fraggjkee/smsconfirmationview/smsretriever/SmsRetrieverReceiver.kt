package com.fraggjkee.smsconfirmationview.smsretriever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

internal abstract class SmsRetrieverReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION != intent.action) return

        val extras = intent.extras
        val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status
        if (smsRetrieverStatus?.statusCode == CommonStatusCodes.SUCCESS) {
            val consentIntent: Intent? = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT)
            consentIntent?.let { onConsentIntentRetrieved(it) }
        }
    }

    abstract fun onConsentIntentRetrieved(intent: Intent)
}