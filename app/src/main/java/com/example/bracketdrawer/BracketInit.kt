package com.example.bracketdrawer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

//Determine how many participants are in the bracket, and whether it is single- or double-elimination.
class BracketInit : Activity() {
    private var nextButton: Button? = null
    private var participantsField: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bracket_init)

        nextButton = findViewById(R.id.nextButton)
        participantsField = findViewById(R.id.participantsField)

        nextButton?.setOnClickListener {
            val participants = participantsField?.getText().toString().toInt()

            val intent = Intent(this@BracketInit, BracketFill::class.java)
            intent.putExtra("participants", participants)
            startActivity(intent)
        }
    }
}