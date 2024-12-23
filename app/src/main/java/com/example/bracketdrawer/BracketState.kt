package com.example.bracketdrawer

data class BracketState (
    val name: String,
    val participantButtons: Map<Int, List<ButtonState>>,
    val buttonPairs: Map<ButtonState, Pair<ButtonState, ButtonState>>,
    val buttonToTarget: Map<ButtonState, ButtonState>
)

data class ButtonState (
    val text: String,
    val color: Int,
    val isEnabled: Boolean
)
