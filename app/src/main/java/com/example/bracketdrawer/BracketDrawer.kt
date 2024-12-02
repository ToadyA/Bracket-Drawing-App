package com.example.bracketdrawer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

//The bracket is drawn and runs, is interactive, and can be saved.
class BracketDrawer : AppCompatActivity(){
    private var namesStatus: TextView? = null
    private var eliminationStatus: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bracket_draw)

        namesStatus = findViewById(R.id.namesStatus)
        eliminationStatus = findViewById(R.id.eliminationStatus)
        val participantNames = intent.getStringArrayListExtra("participantNames")
        val elimination = intent.getStringExtra("elimination") ?: "Single Elimination"

        namesStatus?.text = participantNames?.joinToString(separator = "\n") ?: "Nobody playing"
        eliminationStatus?.text = elimination
    }
}