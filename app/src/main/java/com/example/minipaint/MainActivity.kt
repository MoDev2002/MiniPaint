package com.example.minipaint

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val myCanvasView = MyCanvasView(this)

        //make view take full screen
        window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN

        myCanvasView.contentDescription = getString(R.string.canvasContentDescription)
        //set the screen content to the custom view
        setContentView(myCanvasView)

    }
}