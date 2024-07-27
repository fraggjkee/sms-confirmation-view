package com.fraggjkee.smsconfirmationview

internal fun String.digits(): String = filter { char -> char.isDigit() }