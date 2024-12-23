package com.example.bracketdrawer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

//Make a new bracket or load a saved bracket.
class BracketHome : Activity() {

    private lateinit var startButton: Button
    private lateinit var bracketGrid: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bracket_home)

        startButton = findViewById<Button>(R.id.startButton)
        bracketGrid = findViewById(R.id.bracketGrid)

        startButton.setOnClickListener { v: View? ->
            val intent = Intent(this@BracketHome, BracketInit::class.java)
            startActivity(intent)
        }

        loadSavedBrackets()
    }

    private fun loadSavedBrackets() {
        val savedBrackets = getSavedBrackets()
        for(bracket in savedBrackets){
            val button = Button(this)
            button.text = bracket.name
            button.setOnClickListener{
                val intent = Intent(this, BracketDrawer::class.java)
                val gson = Gson()
                val bracketJson = gson.toJson(bracket)
                intent.putExtra("savedBracket", bracketJson)
                startActivity(intent)
            }
            bracketGrid.addView(button)
        }
    }

    private fun getSavedBrackets(): List<BracketState>{
        val sharedPreferences = getSharedPreferences("bracket_storage", MODE_PRIVATE)
        val gson = Gson()
        val savedBrackets = mutableListOf<BracketState>()
        val allEntries = sharedPreferences.all
        for((_, value) in allEntries){
            val bracketJson = value as String
            val bracketType = object : TypeToken<BracketState>() {}.type
            val bracketState: BracketState = gson.fromJson(bracketJson, bracketType)
            savedBrackets.add(bracketState)
        }

        return savedBrackets
    }
}