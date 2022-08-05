package com.example.graphexperimentapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View

class HorizontalBarChart : View {

    private var screenWidth = 1000F
    private var screenHeight = 500F

    private var startPaddingPercent = 0.25F
    private var endPaddingPercent = 0.05F
    private var topPaddingPercent = 0.25F
    private var bottomPaddingPercent = 0.25F

    private var graphWidthPercent = 1 - startPaddingPercent - endPaddingPercent
    private var graphHeightPercent = 1 - topPaddingPercent - bottomPaddingPercent

    private val startPadding; get() = screenWidth * startPaddingPercent
    private val endPadding; get() = screenWidth * endPaddingPercent
    private val topPadding; get() = screenWidth * topPaddingPercent
    private val bottomPadding; get() = screenWidth * bottomPaddingPercent

    private val graphWidth; get() = screenWidth * graphWidthPercent
    private val graphHeight; get() = screenHeight * graphHeightPercent

    private val linePaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLUE
        style = Paint.Style.FILL
        strokeWidth = 20F
    }

    private val yAxisNamePaint = TextPaint().apply {
        isAntiAlias = true
        color = Color.BLUE
        textSize = 35F
        textAlign = Paint.Align.RIGHT
    }

    private val xAxisNamePaint = TextPaint().apply {
        isAntiAlias = true
        color = Color.BLUE
        textSize = 35F
        textAlign = Paint.Align.CENTER
    }

    private val marksPointPaint = Paint().apply {
        isAntiAlias = true
        color = Color.RED
        strokeWidth = 30F
    }

    private val marksPointCirclePaint = Paint().apply {
        isAntiAlias = true
        color = Color.MAGENTA
        style = Paint.Style.STROKE
        strokeWidth = 5F
    }

    private val marksPointCircleLinePaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        strokeWidth = 3F
    }

    private val marksPointCircleNamePaint = TextPaint().apply {
        isAntiAlias = true
        color = Color.BLACK
        textSize = 30F
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
    }

    private val subjectNameList = arrayListOf<String>()
    private val markList = arrayListOf<String>()

    private fun setBarGraphData() {
        subjectNameList.add("TELUGU")
        subjectNameList.add("ENGLISH")
        subjectNameList.add("HINDI")
        subjectNameList.add("SCIENCE")
        subjectNameList.add("SOCIAL")

        markList.add("0")
        markList.add("10")
        markList.add("23")
        markList.add("50")
        markList.add("100")
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    init {
        setBarGraphData()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        Log.d("TAG", "onDraw: called")
        canvas?.let {

            canvas.drawColor(Color.LTGRAY)

            drawYLineNames(canvas)
            drawYLine(canvas)

            drawXLineNames(canvas)
            drawXLine(canvas)

            drawParams(canvas)

            drawMarkPoint(canvas)
        }
    }


    private fun drawYLineNames(canvas: Canvas) {

        val textHalfHeight =
            (yAxisNamePaint.ascent() + yAxisNamePaint.descent()) / 2

        for (index in subjectNameList.indices) {
            val subject = subjectNameList[index]
            val yPos = (graphHeight * (index + 1)) / (subjectNameList.size)
            canvas.drawText(subject, startPadding - 30F, topPadding + yPos - textHalfHeight, yAxisNamePaint)
        }
    }

    private fun drawYLine(canvas: Canvas) {
        canvas.drawLine(startPadding, topPadding, startPadding, topPadding+graphHeight, linePaint)
    }

    private fun drawXLine(canvas: Canvas) {

        canvas.drawLine(startPadding, topPadding + graphHeight, startPadding + graphWidth, topPadding + graphHeight, linePaint)
    }

    private fun drawXLineNames(canvas: Canvas) {

        val textHalfHeight =
            (xAxisNamePaint.ascent() + xAxisNamePaint.descent()) / 2

        val yPosEnd = graphHeight + topPadding + 80F

        for (index in 0..6) {
            val xName = (index * 10) * 2
            val perValue = graphWidth * 0.2F
            val xPos = startPadding + (perValue * index)
            canvas.drawText(xName.toString(), xPos, yPosEnd + textHalfHeight, xAxisNamePaint)
        }
    }

    private fun drawMarkPoint(canvas: Canvas) {
        val percent = graphWidth * 0.01F
        val textHeight =
            (marksPointCircleNamePaint.ascent() + marksPointCircleNamePaint.descent()) / 2
        for (index in markList.indices) {
            val marks = markList[index].toInt()
            val xPos = startPadding + (percent * marks)
            val yPos = (graphHeight * (index + 1)) / (subjectNameList.size)

            canvas.drawCircle(xPos, topPadding + yPos, 25F, marksPointCirclePaint)//Circle
            canvas.drawLine(startPadding, topPadding + yPos, xPos, topPadding + yPos, marksPointCircleLinePaint)//Line
            canvas.drawCircle(xPos, topPadding + yPos, 6F, marksPointPaint)//Dot
            canvas.drawText(
                marks.toString(),
                60F + xPos,
                topPadding + yPos - textHeight,
                marksPointCircleNamePaint
            )//Label
        }
    }

    private fun drawParams(canvas: Canvas) {
        val yPosStart = topPadding
        val yPosEnd = graphHeight

        canvas.drawText(
            "yPosStart: $yPosStart  yPosEnd: $yPosEnd ${marksPointCircleNamePaint.ascent()}  ${marksPointCircleNamePaint.descent()}",
            150F,
            900F,
            yAxisNamePaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        Log.d("TAG", "onMeasure Width: $widthMode")
        Log.d("TAG", "onMeasure Height: $heightMode")
        val desiredWidth: Float = when (widthMode) {
            MeasureSpec.UNSPECIFIED -> {
                screenWidth
            }
            MeasureSpec.EXACTLY -> {
                widthSize.toFloat()
            }
            else -> {
                widthSize.toFloat()
            }
        }

        val desiredHeight: Float = when (heightMode) {
            MeasureSpec.UNSPECIFIED -> {
                screenHeight
            }
            MeasureSpec.EXACTLY -> {
                heightSize.toFloat()
            }
            else -> {
                heightSize.toFloat()
            }
        }

        Log.d("TAG", "onMeasure Before: width: $desiredWidth")
        Log.d("TAG", "onMeasure Before: height: $desiredHeight")

        screenWidth = desiredWidth
        screenHeight = desiredHeight
        setMeasuredDimension(desiredWidth.toInt(), desiredHeight.toInt())

        Log.d("TAG", "onMeasure After: width: $desiredWidth")
        Log.d("TAG", "onMeasure After: height: $desiredHeight")
    }

}