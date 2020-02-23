package com.stylingandroid.readresolvelint

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val classes = mutableListOf<SealedClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        classes += SealedClass.ObjectWithReadResolve
        classes += SealedClass.ObjectWithoutReadResolve
    }
}
