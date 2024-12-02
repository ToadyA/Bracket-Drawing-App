package com.example.bracketdrawer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ToggleButton

//Determine how many participants are in the bracket, and whether it is single- or double-elimination.
class BracketInit : Activity() {
    private var nextButton: Button? = null
    private var participantsField: EditText? = null
    private var eliminationToggle: ToggleButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bracket_init)

        nextButton = findViewById(R.id.nextButton)
        eliminationToggle = findViewById(R.id.eliminationToggle)
        participantsField = findViewById(R.id.participantsField)

        nextButton?.setOnClickListener(View.OnClickListener {
            val participants = participantsField?.getText().toString().toInt()
            val elimination = if (eliminationToggle?.isChecked == true) "Double Elimination" else "Single Elimination"

            val intent = Intent(this@BracketInit, BracketFill::class.java)
            intent.putExtra("participants", participants)
            intent.putExtra("elimination", elimination)
            startActivity(intent)
        })
    }
}