package com.example.bracketdrawer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.FrameLayout
import android.widget.Button
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewTreeObserver

// The bracket is drawn and runs, is interactive, and can unfortunately not be saved.
class BracketDrawer : AppCompatActivity(){
    private var namesStatus: TextView? = null
    private var eliminationStatus: TextView? = null
    private var bracketLayout: FrameLayout? = null
    private val participantButtons = mutableMapOf<Int, MutableList<Button>>()
    private val buttonPairs = mutableMapOf<Button, Pair<Button, Button>>()
    private val buttonToTarget = mutableMapOf<Button, Button>()
    private lateinit var lineDraw: LinesDrawer
    private val lineIndices = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bracket_draw)

        namesStatus = findViewById(R.id.namesStatus)
        eliminationStatus = findViewById(R.id.eliminationStatus)
        bracketLayout = findViewById(R.id.bracketLayout)
        lineDraw = findViewById(R.id.linesDrawer)

        val participantNames = intent.getStringArrayListExtra("participantNames")
        val elimination = intent.getStringExtra("elimination") ?: "Single Elimination"

        if(lineDraw != null){
            println("LineDrawer is initialized!")
        }
        else{
            println("LineDrawer is not initialized.")
        }
        bracketLayout?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout(){
                bracketLayout?.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                println("line1")
                lineDraw.addLine(50f, 100f, 200f, 300f)
                println("line2")
                lineDraw.addLine(-50f, -100f, -200f, -300f)
                println("line3")
                lineDraw.addLine(-50f, -100f, 200f, 300f)
            }
        })

        if (participantNames != null) {
            println("Calling placeParticipants")
            placeParticipants(participantNames, elimination)
        }
    }

    private fun placeParticipants(participantNames: ArrayList<String>, elimination: String) {
        println("placeParticipants answered.")
        bracketLayout?.removeAllViews()
        participantButtons.clear()
        buttonPairs.clear()

        val horOffset = 400
        val vertSpacing = 200
        var topMargin = 0

        participantButtons[1] = mutableListOf()
        for (name in participantNames) {
            val nameButton = createButton(name, 50, topMargin)
            nameButton.setOnClickListener {
                println("Button for $name clicked in round 1")
                onRoundButtonClicked(nameButton, 1)
            }
            participantButtons[1]?.add(nameButton)
            topMargin += vertSpacing
        }
        bracketLayout?.post {
            nextRound(2, horOffset, vertSpacing)
        }
    }

    private fun nextRound(round: Int, horOffset: Int, vertSpacing: Int){
        println("and so begins round $round")
        if((participantButtons[round - 1]?.size ?: 0) < 2) {
            return
        }
        participantButtons[round] = mutableListOf()
        val previousRoundButtons = participantButtons[round - 1] ?: return
        val numberOfPairs = (previousRoundButtons.size / 2)

        for (i in 0 until numberOfPairs) {
            val button1 = previousRoundButtons[i * 2]
            val button2 = previousRoundButtons.getOrNull((i * 2) + 1)
            if (button2 != null) {
                val midHeight = ((button1.y + button2.y) / 2)
                val additionalButton = createButton("", horOffset * round, midHeight.toInt())
                additionalButton.setOnClickListener {
                    println("Button clicked in round $round")
                    onRoundButtonClicked(additionalButton, round)
                }
                participantButtons[round]?.add(additionalButton)
                buttonPairs[additionalButton] = Pair(button1, button2)
                buttonToTarget[button1] = additionalButton
                buttonToTarget[button2] = additionalButton
                println("placeParticipants: about to call addLines for $button1 $button2 $additionalButton in round $round")
                addLines(button1, button2, additionalButton)
                println("placeParticipants: thus ends the call for addLines for $button1 $button2 $additionalButton in round $round")
            }
        }

        if (previousRoundButtons.size % 2 != 0) {
            val lastButton = previousRoundButtons.last()
            val additionalButton = createButton(
                lastButton.text.toString(),
                horOffset * round,
                lastButton.y.toInt()
            )
            additionalButton.setOnClickListener {
                println("Button clicked in round $round (odd)")
                onRoundButtonClicked(additionalButton, round)
            }
            participantButtons[round]?.add(additionalButton)
            buttonToTarget[lastButton] = additionalButton
        }
        println("thus ends round $round")
        bracketLayout?.post {
           nextRound((round + 1), horOffset, vertSpacing)
        }
    }

    private fun createButton(name: String, leftMargin: Int, topMargin: Int): Button{
        println("Creating button for $name at x: $leftMargin, y: $topMargin")
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

    private fun addLines(button1: Button, button2: Button, targetButton: Button){
        targetButton.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                targetButton.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val startX1 = (button1.left.toFloat() + (button1.width / 2))
                val startY1 = (button1.top.toFloat() + (button1.height / 2))
                val startX2 = (button2.left.toFloat() + (button2.width / 2))
                val startY2 = (button2.top.toFloat() + (button2.height / 2))
                val endX = (targetButton.left.toFloat() + (targetButton.width / 2))
                val endY = (targetButton.top.toFloat() + (targetButton.height / 2))

                println("drawing a line from ($startX1, $startY1) to ($endX, $endY)")
                lineDraw.addLine(startX1, startY1, endX, endY)
                println("drawing a line from ($startX2, $startY2 to ($endX, $endY)")
                lineDraw.addLine(startX2, startY2, endX, endY)

                val lineIndex1 = (lineDraw.lines.size - 2)
                val lineIndex2 = (lineDraw.lines.size - 1)

                lineIndices[button1.text.toString()] = lineIndex1
                lineIndices[button2.text.toString()] = lineIndex2
            }
        })
    }

    private fun onRoundButtonClicked(button: Button, round: Int){
        println("updateRoundButtonClicked called for round $round")
        val isRed = (button.background as? ColorDrawable)?.color == Color.RED
        val name = button.text.toString()
        val linkedPair = buttonPairs.entries.find{it.value.first == button || it.value.second == button}

        if(linkedPair != null){
            val otherButton = if(linkedPair.value.first == button) linkedPair.value.second else linkedPair.value.first
            if (isRed){
                button.setBackgroundColor(Color.GRAY)
                otherButton.isEnabled = true
                updateNextRoundButton(button, name, round, false)
            }
            else{
                button.setBackgroundColor(Color.RED)
                otherButton.isEnabled = false
                updateNextRoundButton(button, name, round, true)
            }
        }
        else{
            if (isRed){
                button.setBackgroundColor(Color.GRAY)
                updateNextRoundButton(button, name, round, false)
            }
            else{
                button.setBackgroundColor(Color.RED)
                updateNextRoundButton(button, name, round, true)
            }
        }

        val lineIndex = lineIndices[name]
        if(lineIndex != null){
            val color2 = if(isRed) Color.GRAY else Color.RED
            lineDraw.updateLineColor(lineIndex, color2)
        }
    }

    private fun updateNextRoundButton(button: Button, name: String, round: Int, add: Boolean){
        println("updateNextRoundButton called for round $round")
        val nextRoundButtons = participantButtons[round + 1] ?: return
        val nextButton = nextRoundButtons.find{it.text.isEmpty() || it.text == name}
        val targetButton = buttonToTarget[button]

        if(targetButton != null){
            println("Target button for $name is $targetButton")
            if(add){
                targetButton.text = name
                targetButton.setBackgroundColor(Color.GRAY)
            }
            else{
                targetButton.text = ""
                targetButton.setBackgroundColor(Color.LTGRAY)
            }
        }
        else{
            println("No target button found for $name")
        }

        if(add && nextButton != null){
            updateNextRoundButton(nextButton, nextButton.text.toString(), (round + 1), true)
        }
        else if(!add && nextButton != null){
            updateNextRoundButton(nextButton, nextButton.text.toString(), (round + 1), false)
        }
    }
}
