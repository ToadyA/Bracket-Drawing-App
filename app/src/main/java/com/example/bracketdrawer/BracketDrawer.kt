package com.example.bracketdrawer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.FrameLayout
import android.widget.Button
import android.view.View
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

// The bracket is drawn and runs, is interactive, and can be saved.
class BracketDrawer : AppCompatActivity(){
    private var namesStatus: TextView? = null
    private var eliminationStatus: TextView? = null
    private var bracketLayout: FrameLayout? = null
    private val participantButtons = mutableMapOf<Int, MutableList<Button>>()
    private val lineBoxes = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?){
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

    private fun placeParticipants(participantNames: ArrayList<String>, elimination: String){
        bracketLayout?.removeAllViews()
        participantButtons.clear()
        lineBoxes.clear()

        val horOffset = 400
        val vertSpacing = 200
        var topMargin = 0

        // Create initial round (round 1) buttons
        participantButtons[1] = mutableListOf()
        for (name in participantNames) {
            val nameButton = createButton(name, 50, topMargin)
            nameButton.setOnClickListener { onRoundButtonClicked(nameButton, 1) }
            participantButtons[1]?.add(nameButton)
            topMargin += vertSpacing
        }

        // Create additional rounds (rounds 2, 3, ..., n)
        var round = 2
        while (participantButtons[round - 1]?.size ?: 0 >= 2){
            participantButtons[round] = mutableListOf()
            val previousRoundButtons = participantButtons[round - 1] ?: break
            val numberOfPairs = previousRoundButtons.size / 2

            for (i in 0 until numberOfPairs){
                val button1 = previousRoundButtons[i * 2]
                val button2 = previousRoundButtons.getOrNull(i * 2 + 1)

                if (button1 != null && button2 != null){
                    val midHeight = (button1.y + button2.y) / 2
                    val additionalButton = createButton("", horOffset * round, midHeight.toInt())
                    additionalButton.setOnClickListener { onRoundButtonClicked(additionalButton, round) }
                    participantButtons[round]?.add(additionalButton)

                    // Add line boxes
                    addLineBoxes(button1, button2, additionalButton)
                }
            }

            // Handle the odd participant case by advancing the last button to the next round
            if (previousRoundButtons.size % 2 != 0){
                val lastButton = previousRoundButtons.last()
                val additionalButton = createButton(lastButton.text.toString(), horOffset * round, lastButton.y.toInt())
                additionalButton.setOnClickListener{
                    onRoundButtonClicked(additionalButton, round)
                }
                participantButtons[round]?.add(additionalButton)
            }

            round++
        }
    }

    private fun createButton(name: String, leftMargin: Int, topMargin: Int): Button{
        val button = Button(this)
        button.text = name
        button.setBackgroundColor(Color.GRAY)
        val buttonParams = FrameLayout.LayoutParams(
            250,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        buttonParams.setMargins(leftMargin, topMargin, 0, 0)
        button.layoutParams = buttonParams
        bracketLayout?.addView(button)
        return button
    }

    private fun addLineBoxes(button1: Button, button2: Button, targetButton: Button){
        val midHeight = (button1.y + button2.y) / 2
        val lineBox1 = createLineBox(button1.x.toInt(), button1.y.toInt(), targetButton.x.toInt(), midHeight.toInt())
        val lineBox2 = createLineBox(button2.x.toInt(), button2.y.toInt(), targetButton.x.toInt(), midHeight.toInt())
        lineBoxes.add(lineBox1)
        lineBoxes.add(lineBox2)
    }

    private fun createLineBox(startX: Int, startY: Int, endX: Int, endY: Int): View{
        val lineBox = View(this)
        lineBox.setBackgroundColor(Color.GRAY)
        val lineBoxParams = FrameLayout.LayoutParams(
            endX - startX,
            10
        )
        lineBoxParams.setMargins(startX, (startY + endY) / 2, 0, 0)
        lineBox.layoutParams = lineBoxParams
        bracketLayout?.addView(lineBox)
        return lineBox
    }

    private fun onRoundButtonClicked(button: Button, round: Int){
        val isRed = (button.background as? ColorDrawable)?.color == Color.RED
        val name = button.text.toString()

        if (isRed){
            button.setBackgroundColor(Color.GRAY)
            updateNextRoundButton(name, round, false)
        }
        else{
            button.setBackgroundColor(Color.RED)
            updateNextRoundButton(name, round, true)
        }
    }

    private fun updateNextRoundButton(name: String, round: Int, add: Boolean){
        val nextRoundButtons = participantButtons[round + 1] ?: return
        val nextButton = nextRoundButtons.find { it.text.isEmpty() || it.text == name }

        if (add) {
            if (nextButton != null){
                nextButton.text = name
                nextButton.setBackgroundColor(Color.GRAY)
            }
        }
        else{
            nextButton?.text = ""
        }

        for (lineBox in lineBoxes){
            if (lineBox.tag == name){
                lineBox.setBackgroundColor(if (add) Color.RED else Color.GRAY)
            }
        }
    }
}


