package com.example.bracketdrawer

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

class ButtonStateAdapter : TypeAdapter<ButtonState>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, buttonState: ButtonState){
        out.beginObject()
        out.name("text").value(buttonState.text)
        out.name("color").value(buttonState.color)
        out.name("isEnabled").value(buttonState.isEnabled)
        out.endObject()
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): ButtonState{
        var text = ""
        var color = 0
        var isEnabled = false

        `in`.beginObject()
        while(`in`.hasNext()){
            when(`in`.nextName()){
                "text" -> text = `in`.nextString()
                "color" -> color = `in`.nextInt()
                "isEnabled" -> isEnabled = `in`.nextBoolean()
            }
        }
        `in`.endObject()

        return ButtonState(text, color, isEnabled)
    }

}