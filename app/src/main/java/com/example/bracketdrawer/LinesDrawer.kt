package com.example.bracketdrawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LinesDrawer @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defStyleAttribute: Int = 0
) : View(context, attributes, defStyleAttribute){

    private val paint = Paint().apply{
        color = Color.GRAY
        strokeWidth = 5f
    }

    val lines = mutableListOf<Line>()
    data class Line(val startX: Float, val startY: Float, val endX: Float, val endY: Float, var color: Int = Color.GRAY)

    fun addLine(startX: Float, startY: Float, endX: Float, endY: Float){
        lines.add(Line(startX, startY, endX, endY))
        invalidate()
    }

    fun updateLineColor(index: Int, color: Int){
        if(index in lines.indices){
            lines[index].color = color
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas){
        super.onDraw(canvas)
        for(line in lines){
            paint.color = line.color
            canvas.drawLine(line.startX, line.startY, line.endX, line.endY, paint)
        }
    }
}