package com.fraggjkee.smsconfirmationview

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.MaterialColors

@ColorInt
internal fun Context.getThemeColor(@AttrRes attrRes: Int): Int {
    return MaterialColors.getColor(this, attrRes, Color.BLACK)
}

internal fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

internal fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

// https://stackoverflow.com/a/32973351/984014
internal fun View.getActivity(): AppCompatActivity? {
    var context = context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

internal fun Context.registerReceiver(
    receiver: BroadcastReceiver,
    intentFilter: IntentFilter,
    permission: String
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        @Suppress("UnspecifiedRegisterReceiverFlag")
        registerReceiver(
            receiver,
            intentFilter,
            permission,
            null
        )
    } else {
        registerReceiver(
            receiver,
            intentFilter,
            permission,
            null,
            Context.RECEIVER_EXPORTED
        )
    }
}
