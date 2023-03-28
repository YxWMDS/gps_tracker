package com.example.gpstracker.utils

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.example.gpstracker.R

object DialogManager {

    fun showLocationDialog(context: Context, listener: Listener){
        val alertDialog = AlertDialog.Builder(context)

    }

    interface Listener{
        fun onAcceptClick()
    }
}