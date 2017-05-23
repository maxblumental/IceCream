package com.example.icecream.wrapper

import android.content.Context
import android.widget.Toast

interface ToastMaker {
    fun show(text: String)
}

class ToastMakerImpl(private val context: Context) : ToastMaker {

    override fun show(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}