package com.anwesh.uiprojects.linkedsquarebouncyblockview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.squarebouncyblockview.SquareBouncyBlockView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SquareBouncyBlockView.create(this)
    }
}
