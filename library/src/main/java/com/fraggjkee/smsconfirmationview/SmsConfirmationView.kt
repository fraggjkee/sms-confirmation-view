package com.fraggjkee.smsconfirmationview

import android.annotation.SuppressLint
import android.content.*
import android.text.InputType
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import android.widget.Space
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.children
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.Lifecycle
import com.fraggjkee.smsconfirmationview.smsretriever.SmsParser
import com.fraggjkee.smsconfirmationview.smsretriever.SmsRetrieverContract
import com.fraggjkee.smsconfirmationview.smsretriever.SmsRetrieverReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever

class SmsConfirmationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var enteredCode: String = ""
        set(value) {
            require(value.length <= codeLength) { "enteredCode=$value is longer than $codeLength" }
            field = value
            onChangeListener?.onCodeChange(
                code = value,
                isComplete = value.length == codeLength
            )
            updateState()
        }

    var onChangeListener: OnChangeListener? = null

    internal var style: Style = SmsConfirmationViewStyleUtils.getDefault(context)
        set(value) {
            if (field == value) return
            field = value
            removeAllViews()
            updateState()
        }

    private var smsRetrieverResultLauncher: ActivityResultLauncher<Intent>? = null
    private val smsBroadcastReceiver: BroadcastReceiver

    private val activityResultCallback = ActivityResultCallback<String?> { smsContent ->
        val view = this@SmsConfirmationView
        smsContent?.takeIf { it.isBlank().not() }
            ?.let { SmsParser.parseOneTimeCode(it, view.codeLength) }
            ?.let { view.enteredCode = it }

    }

    private val symbolSubviews: Sequence<SymbolView>
        get() = children.filterIsInstance<SymbolView>()

    init {
        orientation = HORIZONTAL
        isFocusable = true
        isFocusableInTouchMode = true

        // Registering here results in attaching to a parent Activity. We'll do
        // one more attempt from onAttachedToWindow to recheck if actual parent is a
        // Fragment.
        smsRetrieverResultLauncher = getActivity()
            ?.takeIf { it.lifecycle.currentState < Lifecycle.State.STARTED }
            ?.registerForActivityResult(SmsRetrieverContract(), activityResultCallback)

        style =
            if (attrs == null) SmsConfirmationViewStyleUtils.getDefault(context)
            else SmsConfirmationViewStyleUtils.getFromAttributes(attrs, context)

        updateState()

        smsBroadcastReceiver = object : SmsRetrieverReceiver() {
            override fun onConsentIntentRetrieved(intent: Intent) {
                smsRetrieverResultLauncher?.launch(intent)
            }
        }

        if (isInEditMode) {
            repeat(codeLength) {
                enteredCode += 0.toString()
            }
        }
    }

    private fun updateState() {
        val codeLengthChanged = codeLength != symbolSubviews.count()
        if (codeLengthChanged) {
            setupSymbolSubviews()
        }

        val viewCode = symbolSubviews.map { it.symbol }
            .filterNotNull()
            .joinToString(separator = "")
        val isViewCodeOutdated = enteredCode != viewCode
        if (isViewCodeOutdated) {
            symbolSubviews.forEachIndexed { index, view ->
                view.symbol = enteredCode.getOrNull(index)
            }
        }
    }

    private fun setupSymbolSubviews() {
        removeAllViews()

        for (i in 0 until codeLength) {
            val symbolView = SymbolView(context, style.symbolViewStyle)
            addView(symbolView)

            if (i < codeLength.dec()) {
                val space = Space(context).apply {
                    layoutParams = ViewGroup.LayoutParams(style.symbolsSpacing, 0)
                }
                addView(space)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val parentFragment = runCatching { findFragment<Fragment>() }.getOrNull()
        if (parentFragment != null) {
            smsRetrieverResultLauncher = parentFragment.registerForActivityResult(
                SmsRetrieverContract(),
                activityResultCallback
            )
        }

        context.registerSmsVerificationReceiver(smsBroadcastReceiver)
        setOnKeyListener { _, keyCode, event -> handleKeyEvent(keyCode, event) }
        postDelayed(KEYBOARD_AUTO_SHOW_DELAY) {
            requestFocus()
            showKeyboard()
        }
    }

    private fun handleKeyEvent(keyCode: Int, event: KeyEvent): Boolean = when {
        event.action != KeyEvent.ACTION_DOWN -> false
        event.isDigitKey() -> {
            val enteredSymbol = event.keyCharacterMap.getNumber(keyCode)
            appendSymbol(enteredSymbol)
            true
        }
        event.keyCode == KeyEvent.KEYCODE_DEL -> {
            removeLastSymbol()
            true
        }
        event.keyCode == KeyEvent.KEYCODE_ENTER -> {
            hideKeyboard()
            true
        }
        else -> false
    }

    private fun KeyEvent.isDigitKey(): Boolean {
        return this.keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9
    }

    private fun appendSymbol(symbol: Char) {
        if (enteredCode.length == codeLength) {
            return
        }

        this.enteredCode = enteredCode + symbol
    }

    private fun removeLastSymbol() {
        if (enteredCode.isEmpty()) {
            return
        }

        this.enteredCode = enteredCode.substring(0, enteredCode.length - 1)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && requestFocus()) {
            showKeyboard()
            return true
        }

        return super.onTouchEvent(event)
    }

    override fun onCheckIsTextEditor(): Boolean = true

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        with(outAttrs) {
            inputType = InputType.TYPE_CLASS_NUMBER
            imeOptions = EditorInfo.IME_ACTION_DONE
        }

        return BaseInputConnection(this, false)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        context.unregisterReceiver(smsBroadcastReceiver)
    }

    /**
     * Interface definition for a callback invoked when a views's entered code is changed.
     */
    fun interface OnChangeListener {
        /**
         * Called when the entered code changes.
         * @param code new value of the entered code
         * @param isComplete true when the [code]'s length matches [codeLength] and false otherwise
         */
        fun onCodeChange(code: String, isComplete: Boolean)
    }

    internal data class Style(
        val codeLength: Int,
        val symbolsSpacing: Int,
        val symbolViewStyle: SymbolView.Style
    )

    companion object {
        internal const val DEFAULT_CODE_LENGTH = 4
        private const val KEYBOARD_AUTO_SHOW_DELAY = 500L
    }
}

private fun Context.registerSmsVerificationReceiver(receiver: BroadcastReceiver) {
    registerReceiver(
        receiver,
        IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
        SmsRetriever.SEND_PERMISSION,
        null
    )

    SmsRetriever.getClient(this).startSmsUserConsent(null)
}