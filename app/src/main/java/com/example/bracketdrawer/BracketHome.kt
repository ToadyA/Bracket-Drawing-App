package com.example.bracketdrawer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button

//Make a new bracket or load a saved bracket.
class BracketHome : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bracket_home)

        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener { v: View? ->
            val intent = Intent(this@BracketHome, BracketInit::class.java)
            startActivity(intent)
        }
    }
}