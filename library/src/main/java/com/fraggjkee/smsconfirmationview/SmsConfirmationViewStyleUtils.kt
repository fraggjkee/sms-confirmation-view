package com.fraggjkee.smsconfirmationview

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.R as MaterialR

internal object SmsConfirmationViewStyleUtils {

    private var defaultStyle: SmsConfirmationView.Style? = null

    fun getDefault(context: Context): SmsConfirmationView.Style {
        if (defaultStyle == null) {
            val resources = context.resources
            val symbolViewStyle = SymbolView.Style(
                showCursor = true,
                width = resources.getDimensionPixelSize(R.dimen.symbol_view_width),
                height = resources.getDimensionPixelSize(R.dimen.symbol_view_height),
                backgroundColor = context.getThemeColor(com.google.android.material.R.attr.colorSurface),
                borderColor = context.getThemeColor(MaterialR.attr.colorPrimary),
                borderColorActive = context.getThemeColor(MaterialR.attr.colorPrimary),
                borderWidth = resources.getDimensionPixelSize(R.dimen.symbol_view_stroke_width),
                borderCornerRadius = resources.getDimension(R.dimen.symbol_view_corner_radius),
                textColor = context.getThemeColor(MaterialR.attr.colorOnSurface),
                textSize = resources.getDimensionPixelSize(R.dimen.symbol_view_text_size)
            )
            defaultStyle = SmsConfirmationView.Style(
                codeLength = SmsConfirmationView.DEFAULT_CODE_LENGTH,
                symbolsSpacing = resources.getDimensionPixelSize(R.dimen.symbols_spacing),
                symbolViewStyle = symbolViewStyle,
                isPasteEnabled = true
            )
        }
        return defaultStyle!!
    }

    fun getFromAttributes(
        attrs: AttributeSet,
        context: Context
    ): SmsConfirmationView.Style {

        val defaultStyle: SmsConfirmationView.Style = getDefault(context)
        val defaultSymbolStyle: SymbolView.Style = defaultStyle.symbolViewStyle
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.SmsConfirmationView, 0, 0)

        return with(typedArray) {
            val showCursor: Boolean = getBoolean(
                R.styleable.SmsConfirmationView_scv_showCursor,
                defaultSymbolStyle.showCursor
            )
            val isPasteEnabled: Boolean = getBoolean(
                R.styleable.SmsConfirmationView_scv_pasteEnabled,
                defaultStyle.isPasteEnabled
            )
            val symbolWidth: Int = getDimensionPixelSize(
                R.styleable.SmsConfirmationView_scv_symbolWidth,
                defaultSymbolStyle.width
            )
            val symbolHeight: Int = getDimensionPixelSize(
                R.styleable.SmsConfirmationView_scv_symbolHeight,
                defaultSymbolStyle.height
            )
            val symbolBackgroundColor: Int = getColor(
                R.styleable.SmsConfirmationView_scv_symbolBackgroundColor,
                defaultSymbolStyle.backgroundColor
            )
            val symbolBorderColor: Int = getColor(
                R.styleable.SmsConfirmationView_scv_symbolBorderColor,
                defaultSymbolStyle.borderColor
            )
            val symbolBorderActiveColor: Int = getColor(
                R.styleable.SmsConfirmationView_scv_symbolBorderActiveColor,
                symbolBorderColor
            )
            val symbolBorderWidth: Int = getDimensionPixelSize(
                R.styleable.SmsConfirmationView_scv_symbolBorderWidth,
                defaultSymbolStyle.borderWidth
            )
            val symbolTextColor: Int = getColor(
                R.styleable.SmsConfirmationView_scv_symbolTextColor,
                defaultSymbolStyle.textColor
            )
            val symbolTextSize: Int = getDimensionPixelSize(
                R.styleable.SmsConfirmationView_scv_symbolTextSize,
                defaultSymbolStyle.textSize
            )
            val cornerRadius: Float = getDimension(
                R.styleable.SmsConfirmationView_scv_symbolBorderCornerRadius,
                defaultSymbolStyle.borderCornerRadius
            )
            val codeLength: Int = getInt(
                R.styleable.SmsConfirmationView_scv_codeLength,
                defaultStyle.codeLength
            )
            val symbolsSpacingPx: Int = getDimensionPixelSize(
                R.styleable.SmsConfirmationView_scv_symbolsSpacing,
                defaultStyle.symbolsSpacing
            )
            val smsDetectionMode: SmsConfirmationView.SmsDetectionMode = getInt(
                R.styleable.SmsConfirmationView_scv_smsDetectionMode,
                SmsConfirmationView.SmsDetectionMode.AUTO.ordinal
            ).let { SmsConfirmationView.SmsDetectionMode.values()[it] }


            val symbolTextFont = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getFont(R.styleable.SmsConfirmationView_scv_symbolTextFont)
                    ?: Typeface.DEFAULT_BOLD
            } else {
                val resId = getResourceId(R.styleable.SmsConfirmationView_scv_symbolTextFont, -1)
                if (resId == -1) {
                    Typeface.DEFAULT_BOLD
                } else {
                    ResourcesCompat.getFont(context, resId) ?: Typeface.DEFAULT_BOLD
                }
            }

            recycle()

            SmsConfirmationView.Style(
                codeLength = codeLength,
                isPasteEnabled = isPasteEnabled,
                symbolsSpacing = symbolsSpacingPx,
                symbolViewStyle = SymbolView.Style(
                    showCursor = showCursor,
                    width = symbolWidth,
                    height = symbolHeight,
                    backgroundColor = symbolBackgroundColor,
                    borderColor = symbolBorderColor,
                    borderColorActive = symbolBorderActiveColor,
                    borderWidth = symbolBorderWidth,
                    borderCornerRadius = cornerRadius,
                    textColor = symbolTextColor,
                    textSize = symbolTextSize,
                    typeface = symbolTextFont
                ),
                smsDetectionMode = smsDetectionMode
            )
        }
    }
}