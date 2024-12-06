package com.example.bracketdrawer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.LinearLayout
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.graphics.Color

//The bracket is drawn and runs, is interactive, and can be saved.
class BracketDrawer : AppCompatActivity() {
    private var namesStatus: TextView? = null
    private var eliminationStatus: TextView? = null
    private var bracketLayout: LinearLayout? = null
    private val participantBoxes = mutableMapOf<String, TextView>()
    private val lineBoxes = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bracket_draw)

        namesStatus = findViewById(R.id.namesStatus)
        eliminationStatus = findViewById(R.id.eliminationStatus)
        bracketLayout = findViewById(R.id.bracketLayout)
        val participantNames = intent.getStringArrayListExtra("participantNames")
        val elimination = intent.getStringExtra("elimination") ?: "Single Elimination"

        if (participantNames != null) {
            placeParticipants(participantNames, elimination)
        }
    }

    private fun placeParticipants(participantNames: ArrayList<String>, elimination: String) {
        bracketLayout?.removeAllViews()
        participantBoxes.clear()
        lineBoxes.clear()

        for (name in participantNames){
            val nameBox = TextView(this)
            nameBox.text = name
            nameBox.setBackgroundColor(Color.GRAY)
            nameBox.gravity = Gravity.CENTER
            nameBox.setPadding(16, 16, 16, 16)
            val nameBoxParams = LinearLayout.LayoutParams(
                250,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            nameBoxParams.setMargins(8, 8, 8, 8)
            nameBox.layoutParams = nameBoxParams
            nameBox.setOnClickListener { onBoxClicked(name) }
            bracketLayout?.addView(nameBox)
            participantBoxes[name] = nameBox

            for (i in 1..2) {
                val lineBox = View(this)
                lineBox.setBackgroundColor(Color.GRAY)
                val lineBoxParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    10
                )
                lineBox.layoutParams = lineBoxParams
                bracketLayout?.addView(lineBox)
                lineBoxes.add(lineBox)
            }
        }
    }
/*
    private fun lineBoxDimensions(role: Int): Quadruple<Int, Int, Int, Int>{
        val width
        val height
        val marginStart
        val marginTop
    }
*/
    private fun onBoxClicked(participantName: String) {
        val nameBox = participantBoxes[participantName]
        val isRed = nameBox?.background?.current?.constantState == getDrawable(android.R.color.holo_red_dark)?.constantState

        if (nameBox != null) {
            if (isRed) {
                nameBox.setBackgroundColor(Color.GRAY)
            } else {
                nameBox.setBackgroundColor(Color.RED)
            }
        }

        for (lineBox in lineBoxes) {
            if (isRed) {
                lineBox.setBackgroundColor(Color.GRAY)
            } else {
                lineBox.setBackgroundColor(Color.RED)
            }
        }
    }
    //data class Quadruple<out A, out B, out C, out D>(val first: A, val second: B, val third C, val fourth: D)
}
