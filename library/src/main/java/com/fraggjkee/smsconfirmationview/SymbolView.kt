package com.fraggjkee.smsconfirmationview

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Size
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px

private const val textPaintAlphaAnimDuration = 200L
private const val borderPaintAlphaAnimDuration = 300L

@SuppressLint("ViewConstructor")
internal class SymbolView(context: Context, private val symbolStyle: Style) : View(context) {

    var symbol: Char? = null
        set(value) {
            if (field == value) return
            field = value
            textSize = calculateTextSize(symbol)
            if (value == null) invalidate()
            else animateText()
        }

    var isActive: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            animateBorderColorChange(field)
        }

    private val desiredW: Int
    private val desiredH: Int
    private val textSizePx: Int
    private val cornerRadius: Float

    private val backgroundRect = RectF()

    private val backgroundPaint: Paint
    private val borderPaint: Paint
    private val textPaint: Paint

    private var textSize: Size

    init {
        desiredW = symbolStyle.width
        desiredH = symbolStyle.height
        textSizePx = symbolStyle.textSize
        cornerRadius = symbolStyle.borderCornerRadius

        textSize = calculateTextSize(symbol)

        backgroundPaint = Paint().apply {
            this.color = symbolStyle.backgroundColor
            this.style = Paint.Style.FILL
        }

        borderPaint = Paint().apply {
            this.isAntiAlias = true
            this.color = symbolStyle.borderColor
            this.style = Paint.Style.STROKE
            this.strokeWidth = symbolStyle.borderWidth.toFloat()
        }

        textPaint = Paint().apply {
            this.isAntiAlias = true
            this.color = symbolStyle.textColor
            this.textSize = textSizePx.toFloat()
            this.typeface = Typeface.DEFAULT_BOLD
            this.textAlign = Paint.Align.CENTER
        }
    }

    private fun calculateTextSize(symbol: Char?): Size {
        return symbol?.let {
            val textBounds = Rect()
            textPaint.getTextBounds(it.toString(), 0, 1, textBounds)
            Size(textBounds.width(), textBounds.height())
        } ?: Size(0, 0)
    }

    private fun animateText() {
        ObjectAnimator.ofInt(textPaint, "alpha", 0, 255)
            .apply {
                duration = textPaintAlphaAnimDuration
                addUpdateListener { invalidate() }
            }
            .start()
    }

    private fun animateBorderColorChange(isActive: Boolean) {
        val borderColor = symbolStyle.borderColor
        val borderColorActive = symbolStyle.borderColorActive
        if (borderColor == borderColorActive) {
            return
        }

        val colorFrom =
            if (isActive) borderColor
            else borderColorActive
        val colorTo =
            if (isActive) borderColorActive
            else borderColor
        ObjectAnimator.ofObject(borderPaint, "color", ArgbEvaluator(), colorFrom, colorTo)
            .apply {
                duration = borderPaintAlphaAnimDuration
                addUpdateListener { invalidate() }
            }
            .start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = resolveSizeAndState(desiredW, widthMeasureSpec, 0)
        val h = resolveSizeAndState(desiredH, heightMeasureSpec, 0)
        setMeasuredDimension(w, h)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val borderWidthHalf = borderPaint.strokeWidth / 2
        backgroundRect.left = borderWidthHalf
        backgroundRect.top = borderWidthHalf
        backgroundRect.right = measuredWidth.toFloat() - borderWidthHalf
        backgroundRect.bottom = measuredHeight.toFloat() - borderWidthHalf
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(
            backgroundRect,
            cornerRadius,
            cornerRadius,
            backgroundPaint
        )

        canvas.drawRoundRect(
            backgroundRect,
            cornerRadius,
            cornerRadius,
            borderPaint
        )

        canvas.drawText(
            symbol?.toString() ?: "",
            backgroundRect.width() / 2 + borderPaint.strokeWidth / 2,
            backgroundRect.height() / 2 + textSize.height / 2 + borderPaint.strokeWidth / 2,
            textPaint
        )
    }

    data class Style(
        @Px val width: Int,
        @Px val height: Int,
        @ColorInt val backgroundColor: Int,
        @ColorInt val borderColor: Int,
        @ColorInt val borderColorActive: Int,
        @Px val borderWidth: Int,
        val borderCornerRadius: Float,
        @ColorInt val textColor: Int,
        @Px val textSize: Int
    )
}
