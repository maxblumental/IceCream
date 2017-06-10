package com.example.modelviewintent.utils

fun String.parseIntSafe(): Int = try {
    this.toInt()
} catch (e: NumberFormatException) {
    0
}