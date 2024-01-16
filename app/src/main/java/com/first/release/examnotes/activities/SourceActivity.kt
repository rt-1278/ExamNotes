package com.first.release.examnotes.activities

import android.content.ComponentCallbacks2
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

abstract class SourceActivity: AppCompatActivity(), ComponentCallbacks2 {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("DEBUG","${this.javaClass.name} onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.v("DEBUG","${this.javaClass.name} onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.v("DEBUG","${this.javaClass.name} onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.v("DEBUG","${this.javaClass.name} onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.v("DEBUG","${this.javaClass.name} onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("DEBUG","${this.javaClass.name} onDestroy")
    }
}