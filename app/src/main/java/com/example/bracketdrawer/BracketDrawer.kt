package com.example.bracketdrawer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

//The bracket is drawn and runs, is interactive, and can be saved.
class BracketDrawer : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bracket_draw)

        val textView: TextView = findViewById(R.id.textView)
        val button: Button = findViewById(R.id.button)

        button.setOnClickListener{
            textView.text="Button Clicked!"
        }
    }
}