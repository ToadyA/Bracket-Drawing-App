package com.example.bracketdrawer

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.FrameLayout
import android.widget.Button
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.widget.EditText
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

// The bracket is drawn and runs, is interactive, and can be saved!
class BracketDrawer : AppCompatActivity(){
    private var namesStatus: TextView? = null
    private var bracketLayout: FrameLayout? = null
    private val participantButtons = mutableMapOf<Int, MutableList<Button>>()
    private val buttonPairs = mutableMapOf<Button, Pair<Button, Button>>()
    private val buttonToTarget = mutableMapOf<Button, Button>()
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bracket_draw)

        namesStatus = findViewById(R.id.namesStatus)
        bracketLayout = findViewById(R.id.bracketLayout)
        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener{
            showSaveDialog()
        }

        val participantNames = intent.getStringArrayListExtra("participantNames")
        val bracketJson = intent.getStringExtra("savedBracket")
        if (bracketJson != null) {
            val gson = Gson()
            val bracketType = object : TypeToken<BracketState>() {}.type
            val savedBracket: BracketState = gson.fromJson(bracketJson, bracketType)
            loadBracketState(savedBracket)
        }

        if (participantNames != null) {
            println("Calling placeParticipants")
            placeParticipants(participantNames)
        }
    }

    private fun placeParticipants(participantNames: ArrayList<String>) {
        println("placeParticipants answered.")
        bracketLayout?.removeAllViews()
        participantButtons.clear()
        buttonPairs.clear()
        buttonToTarget.clear()

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

    private fun showSaveDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Save Bracket")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Save"){ dialog, _ ->
            val bracketName = input.text.toString()
            saveBracketState(bracketName)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel"){ dialog, _ -> dialog.cancel()}

        builder.show()
    }

    private fun saveBracketState(name: String){
        val buttonStateMap = participantButtons.mapValues{ entry ->
            entry.value.map{ button ->
                ButtonState(
                    button.text.toString(),
                    (button.background as? ColorDrawable)?.color ?: Color.GRAY,
                    button.isEnabled
                )
            }
        }
        val buttonPairsMap = buttonPairs.mapKeys { entry ->
            ButtonState(
                entry.key.text.toString(),
                (entry.key.background as? ColorDrawable)?.color ?: Color.GRAY,
                entry.key.isEnabled
            )
        }.mapValues { entry ->
            Pair(
                ButtonState(
                    entry.value.first.text.toString(),
                    (entry.value.first.background as? ColorDrawable)?.color ?: Color.GRAY,
                    entry.value.first.isEnabled
                ),
                ButtonState(
                    entry.value.second.text.toString(),
                    (entry.value.second.background as? ColorDrawable)?.color ?: Color.GRAY,
                    entry.value.second.isEnabled
                )
            )
        }
        val buttonToTargetMap = buttonToTarget.mapKeys { entry ->
            ButtonState(
                entry.key.text.toString(),
                (entry.key.background as? ColorDrawable)?.color ?: Color.GRAY,
                entry.key.isEnabled
            )
        }.mapValues { entry ->
            ButtonState(
                entry.value.text.toString(),
                (entry.value.background as? ColorDrawable)?.color ?: Color.GRAY,
                entry.value.isEnabled
            )
        }
        val bracketState = BracketState(
            name,
            buttonStateMap,
            buttonPairsMap,
            buttonToTargetMap
        )

        saveToStorage(bracketState)
        val intent = Intent(this, BracketHome::class.java)
        startActivity(intent)
    }

    private fun saveToStorage(bracketState: BracketState){
        val sharedPreferences = getSharedPreferences("bracket_storage", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = GsonBuilder()
            .registerTypeAdapter(ButtonState::class.java, ButtonStateAdapter())
            .create()
        val json = gson.toJson(bracketState)
        editor.putString(bracketState.name, json)
        editor.apply()
    }

    private fun loadBracketState(bracketState: BracketState){
        participantButtons.clear()
        bracketState.participantButtons.forEach { (key, value) ->
            participantButtons[key] = value.map { buttonState ->
                Button(this).apply{
                    text = buttonState.text
                    setBackgroundColor(buttonState.color)
                    isEnabled = buttonState.isEnabled
                    bracketLayout?.addView(this)
                }
            }.toMutableList()
        }

        buttonPairs.clear()
        bracketState.buttonPairs.forEach { (key, value) ->
            val buttonKey = findButtonByState(participantButtons, key)
            val pair = Pair(
                findButtonByState(participantButtons, value.first),
                findButtonByState(participantButtons, value.second)
            )
            if(buttonKey != null && pair.first != null && pair.second != null){
                buttonPairs[buttonKey] = pair as Pair<Button, Button>
            }
        }

        buttonToTarget.clear()
        bracketState.buttonToTarget.forEach { (key, value) ->
            val buttonKey = findButtonByState(participantButtons, key)
            val targetButton = findButtonByState(participantButtons, value)
            if(buttonKey != null && targetButton != null){
                buttonToTarget[buttonKey] = targetButton
            }
        }

        for(round in participantButtons.values){
            for(button in round){
                bracketLayout?.addView(button)
            }
        }
    }

    private fun findButtonByState(participantButtons: Map<Int, MutableList<Button>>, buttonState: ButtonState): Button? {
        participantButtons.values.flatten().forEach{ button ->
            if(button.text == buttonState.text &&
                (button.background as? ColorDrawable)?.color == buttonState.color &&
                button.isEnabled == buttonState.isEnabled){
                return button
            }
        }
        return null
    }

}
