package com.example.gpstracker.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    @SuppressLint("SimpleDateFormat")
    val formatter = SimpleDateFormat("HH.mm.ss")

    fun getTime(timeInMillis: Long): String{
        val calendar = Calendar.getInstance()
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        calendar.timeInMillis = timeInMillis
        return formatter.format(calendar.time)
    }
}