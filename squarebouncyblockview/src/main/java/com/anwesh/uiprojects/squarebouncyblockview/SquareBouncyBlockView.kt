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
import android.util.Log

val nodes : Int = 5
val squares : Int = 4
val scGap : Float = 0.01f
val delay : Long = 30
val foreColors : Array<String> = arrayOf("#311B92", "#1B5E20", "#0D47A1", "#004D40")
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawSquareBouncyBlock(i : Int, gap : Float, size : Float, scale : Float, paint : Paint) {
    val sc : Float = scale.sinify().divideScale(i, squares)
    Log.d("i:$i", "sc:$sc")
    paint.color = Color.parseColor(foreColors[i])
    save()
    translate(2 * i * gap + gap / 2 + (2 * gap - size / 2) * sc, 0f)
    drawRect(RectF(-size / 2, -size / 2, size / 2, size / 2), paint)
    restore()
}

fun Canvas.drawSquareBouncyBlocks(w : Float, scale : Float, paint : Paint) {
    val gap : Float = w / (2 * squares + 1)
    for (j in 0..(squares - 1)) {
        drawSquareBouncyBlock(j, gap, gap, scale, paint)
    }
}

fun Canvas.drawSBBNode(i : Int, scale : Float, paint : Paint) {
    val h : Float = height.toFloat()
    val w : Float = width.toFloat()
    val gap : Float = h / (nodes + 1)
    save()
    translate(0f, gap * (i + 1))
    drawSquareBouncyBlocks(w, scale, paint)
    restore()
}

class SquareBouncyBlockView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SBBNode(var i : Int, val state : State = State()) {

        private var next : SBBNode? = null
        private var prev : SBBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = SBBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSBBNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SBBNode {
            var curr : SBBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class SquareBouncyBlock(var i : Int) {

        private val root : SBBNode = SBBNode(0)
        private var curr : SBBNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : SquareBouncyBlockView) {

        private val animator : Animator = Animator(view)
        private val sbb : SquareBouncyBlock = SquareBouncyBlock(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            sbb.draw(canvas, paint)
            animator.animate {
                sbb.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            sbb.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : SquareBouncyBlockView {
            val view : SquareBouncyBlockView = SquareBouncyBlockView(activity)
            activity.setContentView(view)
            return view
        }
    }
}