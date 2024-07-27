package com.fraggjkee.smsconfirmationview

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.text.InputType
import android.util.AttributeSet
import android.view.ActionMode
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
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
import com.google.android.gms.auth.api.phone.SmsRetrieverClient

class SmsConfirmationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    /**
     * Getter & setter for the entered code. For setter, only digits are accepted so non-digit
     * symbols will be ignored.
     */
    var enteredCode: String = ""
        set(value) {
            val digits = value.digits()
            require(digits.length <= codeLength) { "enteredCode=$digits is longer than $codeLength" }
            field = digits
            onChangeListener?.onCodeChange(
                code = digits,
                isComplete = digits.length == codeLength
            )
            updateState()
        }

    /**
     * Change listener for the entered code. Will be called for all possible changes:
     * manual typing, pasting, SMS auto-detection, etc.
     */
    var onChangeListener: OnChangeListener? = null

    internal var style: Style = SmsConfirmationViewStyleUtils.getDefault(context)
        set(value) {
            if (field == value) return
            field = value
            removeAllViews()
            updateState()
        }

    private val smsDetectionMode: SmsDetectionMode get() = style.smsDetectionMode

    private var isReceiverRegistered: Boolean = false
    private val smsBroadcastReceiver: BroadcastReceiver = object : SmsRetrieverReceiver() {
        override fun onConsentIntentRetrieved(intent: Intent) {
            smsRetrieverResultLauncher?.run {
                hideKeyboard()
                launch(intent)
            }
        }
    }

    private val activityResultCallback = ActivityResultCallback<String?> { smsContent ->
        val view = this@SmsConfirmationView
        smsContent?.takeIf { it.isBlank().not() }
            ?.let { sms ->
                view.enteredCode = SmsParser.parseOneTimeCode(sms, view.codeLength).orEmpty()
            }
    }

    private val actionModeCallback: ActionMode.Callback = ActionModeCallback(context) { text ->
        enteredCode = text.digits().take(codeLength)
    }

    private var smsRetrieverResultLauncher: ActivityResultLauncher<Intent>? = null

    private val symbolSubviews: Sequence<SymbolView>
        get() = children.filterIsInstance<SymbolView>()

    init {
        orientation = HORIZONTAL
        isFocusable = true
        isFocusableInTouchMode = true

        style =
            if (attrs == null) SmsConfirmationViewStyleUtils.getDefault(context)
            else SmsConfirmationViewStyleUtils.getFromAttributes(attrs, context)
        updateState()

        if (smsDetectionMode != SmsDetectionMode.DISABLED) {
            // Registering here results in attaching to a parent Activity. We'll do
            // one more attempt from onAttachedToWindow to recheck if actual parent is a
            // Fragment.
            smsRetrieverResultLauncher = getActivity()
                ?.takeIf { it.lifecycle.currentState < Lifecycle.State.STARTED }
                ?.registerForActivityResult(SmsRetrieverContract(), activityResultCallback)
        }

        setOnClickListener {
            if (requestFocus()) {
                showKeyboard()
            }
        }
        setOnLongClickListener {
            showPopupMenuIfNeeded()
            true
        }
    }

    private fun updateState() {
        val codeLengthChanged = codeLength != symbolSubviews.count()
        if (codeLengthChanged) {
            setupSymbolSubviews()
        }

        val viewCode = symbolSubviews.map { it.state.symbol }
            .filterNotNull()
            .joinToString(separator = "")
        val isViewCodeOutdated = enteredCode != viewCode
        if (isViewCodeOutdated) {
            symbolSubviews.forEachIndexed { index, view ->
                view.state = SymbolView.State(
                    symbol = enteredCode.getOrNull(index),
                    isActive = (enteredCode.length == index)
                )
            }
        }
    }

    private fun setupSymbolSubviews() {
        removeAllViews()

        for (i in 0 until codeLength) {
            val symbolView = SymbolView(context, style.symbolViewStyle)
            symbolView.state = SymbolView.State(isActive = (i == enteredCode.length))
            addView(symbolView)

            if (i < codeLength.dec()) {
                val space = Space(context).apply {
                    layoutParams = ViewGroup.LayoutParams(style.symbolsSpacing, 0)
                }
                addView(space)
            }
        }
    }

    private fun showPopupMenuIfNeeded() {
        if (style.isPasteEnabled.not()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startActionMode(actionModeCallback, ActionMode.TYPE_FLOATING)
        } else {
            startActionMode(actionModeCallback)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        setOnKeyListener { _, keyCode, event -> handleKeyEvent(keyCode, event) }
        postDelayed(KEYBOARD_AUTO_SHOW_DELAY) {
            requestFocus()
            showKeyboard()
        }

        if (smsDetectionMode == SmsDetectionMode.DISABLED) return
        runCatching { findFragment<Fragment>() }
            .getOrNull()
            ?.takeIf { it.lifecycle.currentState < Lifecycle.State.RESUMED }
            ?.let { parentFragment ->
                smsRetrieverResultLauncher = parentFragment.registerForActivityResult(
                    SmsRetrieverContract(),
                    activityResultCallback
                )
            }

        if (smsDetectionMode == SmsDetectionMode.AUTO) {
            startListeningForIncomingMessagesInternal()
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
        return keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9
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

    override fun onCheckIsTextEditor(): Boolean = true

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        with(outAttrs) {
            inputType = InputType.TYPE_CLASS_NUMBER
            imeOptions = EditorInfo.IME_ACTION_DONE
        }
        return object : InputConnectionWrapper(BaseInputConnection(this, false), true) {

            override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
                return if (beforeLength == 1 && afterLength == 0) {
                    sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
                } else super.deleteSurroundingText(beforeLength, afterLength)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (isReceiverRegistered) {
            context.unregisterReceiver(smsBroadcastReceiver)
        }
    }

    /**
     * Trigger [SmsRetrieverClient.startSmsUserConsent] method which will make the view
     * listen for incoming messages for the next 5 minutes.
     *
     * Can only be used with [SmsDetectionMode.MANUAL]
     */
    fun startListeningForIncomingMessages() {
        if (smsDetectionMode != SmsDetectionMode.MANUAL) {
            throw IllegalStateException(
                "startListeningForIncomingMessages can only be used with SmsDetectionMode.MANUAL"
            )
        }
        startListeningForIncomingMessagesInternal()
    }

    private fun startListeningForIncomingMessagesInternal() {
        context.registerReceiver(
            smsBroadcastReceiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
            SmsRetriever.SEND_PERMISSION
        )

        SmsRetriever.getClient(context).startSmsUserConsent(null)

        isReceiverRegistered = true
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState: Parcelable? = super.onSaveInstanceState()
        return SavedState(superState, enteredCode)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)
        enteredCode = state.enteredCode
    }

    private class SavedState(
        superState: Parcelable?,
        val enteredCode: String
    ) : BaseSavedState(superState) {

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(enteredCode)
        }
    }

    /**
     * Interface definition for a callback invoked when the entered code changes.
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
        val isPasteEnabled: Boolean,
        val symbolsSpacing: Int,
        val symbolViewStyle: SymbolView.Style,
        val smsDetectionMode: SmsDetectionMode = SmsDetectionMode.AUTO
    )

    internal enum class SmsDetectionMode {
        /**
         * Prevent [SmsConfirmationView] from using SMS Consent API, i.e. this option
         * simply disables automatic SMS detection.
         */
        DISABLED,

        /**
         * Default option. [SmsConfirmationView] will try to use SMS Consent API to
         * detect incoming messages and read confirmation codes from it.
         */
        AUTO,

        /**
         * Like [AUTO] but gives you more control when to actually start listening for
         * incoming messages via [startListeningForIncomingMessages]. Can be useful in
         * some cases as SMS Consent API cannot be active for more than 5 minutes.
         */
        MANUAL
    }

    companion object {
        internal const val DEFAULT_CODE_LENGTH = 4
        private const val KEYBOARD_AUTO_SHOW_DELAY = 500L
    }
}
