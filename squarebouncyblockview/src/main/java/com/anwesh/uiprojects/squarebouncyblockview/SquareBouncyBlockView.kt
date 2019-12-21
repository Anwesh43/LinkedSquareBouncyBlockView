package com.anwesh.uiprojects.squarebouncyblockview

/**
 * Created by anweshmishra on 21/12/19.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.app.Activity
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

val nodes : Int = 5
val squares : Int = 4
val scGap : Float = 0.005f
val delay : Long = 30
val foreColor : Int = Color.parseColor("#311B92")
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n)
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawSquareBouncyBlock(i : Int, gap : Float, size : Float, scale : Float, paint : Paint) {
    val sc : Float = scale.sinify().divideScale(i, squares)
    save()
    translate(i * (gap + 1) + gap * sc, 0f)
    drawRect(RectF(-size / 2, -size / 2, size / 2, size / 2), paint)
    restore()
}

fun Canvas.drawSquareBouncyBlocks(w : Float, scale : Float, paint : Paint) {
    val gap : Float = scale / (2 * squares + 1)
    for (j in 0..(squares)) {
        drawSquareBouncyBlock(j, gap, gap, scale, paint)
    }
}

fun Canvas.drawSBBNode(i : Int, scale : Float, paint : Paint) {
    val h : Float = height.toFloat()
    val w : Float = width.toFloat()
    paint.color = foreColor
    val gap : Float = h / (nodes + 1)
    save()
    translate(0f, gap * (i + 1))
    drawSquareBouncyBlocks(w, scale, paint)
    restore()
}
