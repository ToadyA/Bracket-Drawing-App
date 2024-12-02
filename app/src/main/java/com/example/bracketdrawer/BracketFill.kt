package com.example.bracketdrawer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout

//Fill in names, entry number determined by BracketInit.
class BracketFill : Activity() {
    private var drawBracketButton: Button? = null
    private var nameFieldsLayout: LinearLayout? = null
    private var participants: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bracket_fill)

        participants = intent.getIntExtra("participants", 0)
        val elimination = intent.getStringExtra("elimination") ?: "Single Elimination"
        nameFieldsLayout = findViewById<LinearLayout>(R.id.nameFieldsLayout)
        for (i in 0 until participants) {
            val nameField = EditText(this)
            nameField.hint = "Participant " + (i + 1)
            nameFieldsLayout?.addView(nameField)
        }

        drawBracketButton = Button(this)
        drawBracketButton?.text = "Draw Bracket"
        nameFieldsLayout?.addView(drawBracketButton)

        drawBracketButton?.setOnClickListener {
            val participantNames = ArrayList<String>()
            for(i in 0 until participants){
                val nameField = nameFieldsLayout?.getChildAt(i) as EditText
                participantNames.add(nameField.text.toString())
            }

            val intent = Intent(this@BracketFill, BracketDrawer::class.java)
            intent.putExtra("participantNames", participantNames)
            intent.putExtra("elimination", elimination)
            startActivity(intent)
        }
    }
}