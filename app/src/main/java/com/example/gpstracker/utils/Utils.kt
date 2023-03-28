package com.example.gpstracker.utils

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gpstracker.R

fun Fragment.openFragment(fragment: Fragment){
    (activity as AppCompatActivity).supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.placeHolder, fragment).commit()
}

fun AppCompatActivity.openFragment(fragment: Fragment){

    if(supportFragmentManager.fragments.isNotEmpty()){
        if(supportFragmentManager.fragments[0].javaClass == fragment.javaClass) return
    }
    supportFragmentManager.beginTransaction()
        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        .replace(R.id.placeHolder, fragment).commit()
}

fun Fragment.checkPermission(permission: String): Boolean{
    return when(PackageManager.PERMISSION_GRANTED){
        ContextCompat.checkSelfPermission(activity as AppCompatActivity, permission) -> true
        else -> false
    }
}