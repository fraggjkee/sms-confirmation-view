@file:Suppress("unused")

package com.fraggjkee.smsconfirmationview

/**
 * @see R.styleable.SmsConfirmationView_scv_codeLength
 */
var SmsConfirmationView.codeLength: Int
    set(value) {
        require(value >= 0) { "invalid code length - $value" }
        this.style = style.copy(codeLength = value)
    }
    get() = style.codeLength

/**
 * @see R.styleable.SmsConfirmationView_scv_symbolsSpacing
 */
var SmsConfirmationView.symbolsSpacing: Int
    set(value) {
        this.style = style.copy(symbolsSpacing = value)
    }
    get() = style.symbolsSpacing

/**
 * @see R.styleable.SmsConfirmationView_scv_showCursor
 */
var SmsConfirmationView.showCursor: Boolean
    set(value) {
        val updatedStyle = style.symbolViewStyle.copy(
            showCursor = value
        )
        this.style = style.copy(symbolViewStyle = updatedStyle)
    }
    get() = style.symbolViewStyle.showCursor

/**
 * @see R.styleable.SmsConfirmationView_scv_pasteEnabled
 */
var SmsConfirmationView.isPasteEnabled: Boolean
    set(value) {
        this.style = style.copy(isPasteEnabled = value)
    }
    get() = style.isPasteEnabled

/**
 * @see R.styleable.SmsConfirmationView_scv_symbolWidth
 */
var SmsConfirmationView.symbolWidth: Int
    set(value) {
        val updatedStyle = style.symbolViewStyle.copy(
            width = value
        )
        this.style = style.copy(symbolViewStyle = updatedStyle)
    }
    get() = style.symbolViewStyle.width

/**
 * @see R.styleable.SmsConfirmationView_scv_symbolHeight
 */
var SmsConfirmationView.symbolHeight: Int
    set(value) {
        val updatedStyle = style.symbolViewStyle.copy(
            height = value
        )
        this.style = style.copy(symbolViewStyle = updatedStyle)
    }
    get() = style.symbolViewStyle.height

/**
 * @see R.styleable.SmsConfirmationView_scv_symbolTextColor
 */
var SmsConfirmationView.symbolTextColor: Int
    set(value) {
        val updatedStyle = style.symbolViewStyle.copy(
            textColor = value
        )
        this.style = style.copy(symbolViewStyle = updatedStyle)
    }
    get() = style.symbolViewStyle.textColor

/**
 * @see R.styleable.SmsConfirmationView_scv_symbolTextSize
 */
var SmsConfirmationView.symbolTextSize: Int
    set(value) {
        val updatedStyle = style.symbolViewStyle.copy(
            textSize = value
        )
        this.style = style.copy(symbolViewStyle = updatedStyle)
    }
    get() = style.symbolViewStyle.textSize

/**
 * @see R.styleable.SmsConfirmationView_scv_symbolBackgroundColor
 */
var SmsConfirmationView.symbolBackgroundColor: Int
    set(value) {
        val updatedStyle = style.symbolViewStyle.copy(
            backgroundColor = value
        )
        this.style = style.copy(symbolViewStyle = updatedStyle)
    }
    get() = style.symbolViewStyle.backgroundColor

/**
 * @see R.styleable.SmsConfirmationView_scv_symbolBorderColor
 */
var SmsConfirmationView.symbolBorderColor: Int
    set(value) {
        val updatedStyle = style.symbolViewStyle.copy(
            borderColor = value
        )
        this.style = style.copy(symbolViewStyle = updatedStyle)
    }
    get() = style.symbolViewStyle.borderColor

/**
 * @see R.styleable.SmsConfirmationView_scv_symbolBorderActiveColor
 */
var SmsConfirmationView.symbolBorderActiveColor: Int
    set(value) {
        val updatedStyle = style.symbolViewStyle.copy(
            borderColorActive = value
        )
        this.style = style.copy(symbolViewStyle = updatedStyle)
    }
    get() = style.symbolViewStyle.borderColorActive

/**
 * @see R.styleable.SmsConfirmationView_scv_symbolBorderWidth
 */
var SmsConfirmationView.symbolBorderWidth: Int
    set(value) {
        val updatedStyle = style.symbolViewStyle.copy(
            borderWidth = value
        )
        this.style = style.copy(symbolViewStyle = updatedStyle)
    }
    get() = style.symbolViewStyle.borderWidth

/**
 * @see R.styleable.SmsConfirmationView_scv_symbolBorderCornerRadius
 */
var SmsConfirmationView.symbolBorderCornerRadius: Float
    set(value) {
        val updatedStyle = style.symbolViewStyle.copy(
            borderCornerRadius = value
        )
        this.style = style.copy(symbolViewStyle = updatedStyle)
    }
    get() = style.symbolViewStyle.borderCornerRadius