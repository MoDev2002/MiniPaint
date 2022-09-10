package com.example.minipaint

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs

//the width of the stroke or painting brush
private const val STROKE_WIDTH = 12f

class MyCanvasView @JvmOverloads constructor(context : Context, attributeSet: AttributeSet? = null, defStyle : Int = 0) : View(context, attributeSet, defStyle) {

    //make variables to save the bitmap and canvas in
    private lateinit var extraBitmap: Bitmap
    private lateinit var extraCanvas: Canvas

    //background of canvas and color of drawing line
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    //initialize a path object that will follow the user touch path
    private var path = Path()

    //object to make a rectangle frame
    private lateinit var frame : Rect

    //initialize motion touch events coordinates
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    //coordinates for the next starting points
    private var currentX = 0f
    private var currentY = 0f

    //determine with the touch movement requires view redrawing
    private var touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    //define the paint brush
    private val paint = Paint().apply {
        color = drawColor               //assign the paint color
        isAntiAlias = true              //smooth edges of the stroke
        isDither = true                 //lower sample down color for low resolution screens
        style = Paint.Style.STROKE      //make style of paint stroke to make a line
        strokeJoin = Paint.Join.ROUND   //make the segments rounded when they join paths
        strokeCap = Paint.Cap.ROUND     //make the end of the stroke rounded
        strokeWidth = STROKE_WIDTH      //assign stroke width
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //assign the view contents to the extraBitmap and extraCanvas whenever size change
        //check if the bitmap is initialized free it to save another value in it
        if(::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)

        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        canvas.drawRect(frame, paint) //draw rectangle frame
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //cache event coordinates
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        //make action for each state
        when(event.action){
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun touchStart() {
        path.reset()        //reset the path
        //set the path to the motion touch event coordinates of x and y
        path.moveTo(motionTouchEventX, motionTouchEventY)
        //set current coordinates to motion coordinates
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        //calculate the distance of x and y
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)

        //if the movement requires drawing
        //draw a quad (makes smooth edges)
        if(dx >= touchTolerance || dy >= touchTolerance) {
            path.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2, (motionTouchEventY + currentY) / 2 )

            currentX = motionTouchEventX
            currentY = motionTouchEventY

            //add the path to the cache
            extraCanvas.drawPath(path, paint)
        }

        invalidate()
    }

    private fun touchUp() {
        path.reset()
    }

    fun delete() {
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
        path = Path()
        invalidate()
    }

}