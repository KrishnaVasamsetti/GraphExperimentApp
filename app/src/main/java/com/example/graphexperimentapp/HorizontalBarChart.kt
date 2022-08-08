package com.example.graphexperimentapp

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ScrollView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import java.util.*

class HorizontalBarChart : View {

    private var screenWidth = 1000F
    private var screenHeight = 500F

    var startPaddingPercent = 0.25F
    var endPaddingPercent = 0.1F
    var topPaddingPercent = 0.1F
    var bottomPaddingPercent = 0.1F

    var innerCircleRadius = 6F
    var outerCircleRadius = 25F
    var outerCircleAndValueSpace = 40F

    private var graphWidthPercent = 1 - (startPaddingPercent + endPaddingPercent)
    private var graphHeightPercent = 1 - (topPaddingPercent + bottomPaddingPercent)

    private val startPadding; get() = screenWidth * startPaddingPercent
    private val endPadding; get() = screenWidth * endPaddingPercent
    private val topPadding; get() = (screenHeight * topPaddingPercent).coerceAtMost(100F)
    private val bottomPadding; get() = (screenHeight * bottomPaddingPercent).coerceAtMost(100F)

    private val graphWidth; get() = screenWidth * graphWidthPercent
    private val graphHeight; get() = screenHeight * graphHeightPercent

    var extendedYAxisPercent = 0.05F
    private val extendedYAxisDistance; get() = screenWidth * extendedYAxisPercent
    private val extendedXAxisDistance; get() = screenWidth * extendedYAxisPercent

    internal val linePaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLUE
        style = Paint.Style.FILL
        strokeWidth = 15F
        strokeCap = Paint.Cap.ROUND
    }
    internal val sampleLinePaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.FILL
        strokeWidth = 2F
        strokeCap = Paint.Cap.ROUND
    }

    val yAxisNamePaint = TextPaint().apply {
        isAntiAlias = true
        color = Color.BLUE
        textSize = 25F
        textAlign = Paint.Align.RIGHT
    }

    val xAxisNamePaint = TextPaint().apply {
        isAntiAlias = true
        color = Color.BLUE
        textSize = 25F
        textAlign = Paint.Align.CENTER
    }

    val marksPointPaint = Paint().apply {
        isAntiAlias = true
        color = Color.RED
        strokeWidth = 30F
    }

    val marksPointCirclePaint = Paint().apply {
        isAntiAlias = true
        color = Color.MAGENTA
        style = Paint.Style.STROKE
        strokeWidth = 5F
    }

    val marksPointCircleLinePaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        strokeWidth = 3F
    }

    val marksPointCircleNamePaint = TextPaint().apply {
        isAntiAlias = true
        color = Color.BLACK
        textSize = 20F
        isFakeBoldText = true
        textAlign = Paint.Align.LEFT
    }

    private val subjectNameList = arrayListOf<String>()
    private val markList = arrayListOf<String>()

    private fun setBarGraphData() {

        for (i in 0..2) {
            subjectNameList.add("TELUGU")
            subjectNameList.add("ENGLISH")
            subjectNameList.add("HINDI")
            subjectNameList.add("SCIENCE")

            markList.add(Random().nextInt(100).toString())
            markList.add(Random().nextInt(100).toString())
            markList.add(Random().nextInt(100).toString())
            markList.add(Random().nextInt(100).toString())
        }
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

        startGraphAnimatorOnDelay()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        Log.d("TAG", "onDraw: called")
        canvas?.let {

            canvas.drawColor(Color.LTGRAY)

            drawYLineNames(canvas)
            drawYLine(canvas)
            drawYLineRows(canvas)

            drawXLineNames(canvas)
            drawXLine(canvas)

            drawMarkPoint(canvas)
        }
    }


    private fun drawYLineNames(canvas: Canvas) {

        val textHalfHeight =
            (yAxisNamePaint.ascent() + yAxisNamePaint.descent()) / 2

        for (index in subjectNameList.indices) {
            val subject = subjectNameList[index]
            val yPos = (graphHeight * index) / (subjectNameList.size)
            canvas.drawText(
                subject,
                startPadding - 30F,
                topPadding + yPos - textHalfHeight,
                yAxisNamePaint
            )
        }
    }

    private fun drawYLine(canvas: Canvas) {
        canvas.drawLine(
            startPadding,
            topPadding - extendedYAxisDistance,
            startPadding,
            topPadding + graphHeight,
            linePaint
        )
    }

    private fun drawYLineRows(canvas: Canvas) {

        for (index in 1..5) {
            val perValue = graphWidth * 0.2F
            val xPos = startPadding + (perValue * index)
            canvas.drawLine(
                xPos,
                topPadding - extendedYAxisDistance,
                xPos,
                topPadding + graphHeight,
                sampleLinePaint
            )
        }

    }

    private fun drawXLine(canvas: Canvas) {

        canvas.drawLine(
            startPadding,
            topPadding + graphHeight,
            startPadding + graphWidth + extendedXAxisDistance,
            topPadding + graphHeight,
            linePaint
        )
    }

    private fun drawXLineNames(canvas: Canvas) {

        val lineHeight =
            (linePaint.ascent() + linePaint.descent())

        val textHalfHeight =
            (xAxisNamePaint.ascent() + xAxisNamePaint.descent()) / 2

        val yPosEnd = graphHeight + topPadding - lineHeight - (textHalfHeight * 3F)

        for (index in 0..5) {
            val xName = (index * 10) * 2
            val perValue = graphWidth * 0.2F
            val xPos = startPadding + (perValue * index)
            canvas.drawText(xName.toString(), xPos, yPosEnd, xAxisNamePaint)
        }
    }

    private fun drawMarkPoint(canvas: Canvas) {
        val percent = graphWidth * 0.01F
        val textHeight =
            (marksPointCircleNamePaint.ascent() + marksPointCircleNamePaint.descent()) / 2
        val yAxisStroke = linePaint.strokeWidth / 2
        for (index in markList.indices) {
            val marks = markList[index].toInt()
            val xPos = startPadding + ((percent * marks) * animatedProgressValue)
            val yPos = (graphHeight * index) / (subjectNameList.size)

            canvas.drawCircle(
                xPos,
                topPadding + yPos,
                outerCircleRadius,
                marksPointCirclePaint
            )//Circle
            canvas.drawLine(
                startPadding + yAxisStroke,
                topPadding + yPos,
                xPos,
                topPadding + yPos,
                marksPointCircleLinePaint
            )//Line
            canvas.drawCircle(xPos, topPadding + yPos, innerCircleRadius, marksPointPaint)//Dot
            canvas.drawText(
                marks.toString(),
                outerCircleAndValueSpace + xPos,
                topPadding + yPos - textHeight,
                marksPointCircleNamePaint
            )//Label
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val desiredWidth: Float = when (widthMode) {
            MeasureSpec.UNSPECIFIED -> {
                screenWidth
            }
            MeasureSpec.EXACTLY -> {
                widthSize.toFloat()
            }
            else -> {
                screenWidth.coerceAtMost(widthSize.toFloat())
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
                screenHeight.coerceAtMost(heightSize.toFloat())
            }
        }

        Log.d("TAG", "onMeasure Before: width: $desiredWidth")
        Log.d("TAG", "onMeasure Before: height: $desiredHeight")

        screenWidth = desiredWidth
        screenHeight = desiredHeight.coerceAtLeast(subjectNameList.size * 150F)
        setMeasuredDimension(screenWidth.toInt(), screenHeight.toInt())

        Log.d("TAG", "onMeasure After: width: $desiredWidth")
        Log.d("TAG", "onMeasure After: height: $desiredHeight")
    }

    private var animatedProgressValue = 0F

    private fun startGraphAnimatorOnDelay() {
        postDelayed({
            startGraphAnimator()
        }, 1000)
    }

    private fun startGraphAnimator() {
        val loaderAnimator = ObjectAnimator.ofFloat(0F, 1F).apply {
            duration = 2000
//            this.interpolator = DecelerateInterpolator()
            addUpdateListener {
                Log.d("TAG", "startGraphAnimator: $animatedValue")
                animatedProgressValue = animatedValue as Float
                invalidate()
            }
        }
        loaderAnimator.start()
    }

    private fun isViewVisibleInScrollView(): Boolean {
        return if (parent is ScrollView) {
            val scrollBounds = Rect()
            (parent as ScrollView).getHitRect(scrollBounds)
            this.getLocalVisibleRect(scrollBounds)
        } else
            true
    }

}

@BindingAdapter("graphStartOffsetPercent")
fun HorizontalBarChart.graphStartOffsetPercent(graphStartOffsetPercent: Float) {
    this.startPaddingPercent = graphStartOffsetPercent
}

@BindingAdapter("graphEndOffsetPercent")
fun HorizontalBarChart.graphEndOffsetPercent(graphEndOffsetPercent: Float) {
    this.endPaddingPercent = graphEndOffsetPercent
}

@BindingAdapter("graphTopOffsetPercent")
fun HorizontalBarChart.graphTopOffsetPercent(graphTopOffsetPercent: Float) {
    this.topPaddingPercent = graphTopOffsetPercent
}

@BindingAdapter("graphBottomOffsetPercent")
fun HorizontalBarChart.graphBottomOffsetPercent(graphBottomOffsetPercent: Float) {
    this.bottomPaddingPercent = graphBottomOffsetPercent
}

@BindingAdapter("extendedYAxisPercent")
fun HorizontalBarChart.extendedYAxisPercent(extendedYAxisPercent: Float) {
    this.extendedYAxisPercent = extendedYAxisPercent
}

@BindingAdapter("xAxisTextSize")
fun HorizontalBarChart.xAxisTextSize(xAxisTextSize: Float) {
    Log.d("TAG", "xAxisTextSize: ${xAxisNamePaint.textSize} -> $xAxisTextSize")
    this.xAxisNamePaint.apply {
        textSize = xAxisTextSize
    }
}

@BindingAdapter("yAxisTextSize")
fun HorizontalBarChart.yAxisTextSize(yAxisTextSize: Float) {
    Log.d("TAG", "yAxisTextSize: ${yAxisNamePaint.textSize} -> $yAxisTextSize")
    this.yAxisNamePaint.apply {
        textSize = yAxisTextSize
    }
}

@BindingAdapter("valueTextSize")
fun HorizontalBarChart.valueTextSize(valueTextSize: Float) {
    Log.d("TAG", "valueTextSize: ${marksPointCircleNamePaint.textSize} -> $valueTextSize")
    this.marksPointCircleNamePaint.apply {
        textSize = valueTextSize
    }
}

@BindingAdapter("valueLineStroke")
fun HorizontalBarChart.valueLineStroke(valueLineStroke: Float) {
    Log.d("TAG", "valueLineStroke: ${marksPointCircleLinePaint.strokeWidth} -> $valueLineStroke")
    this.marksPointCircleLinePaint.apply {
        strokeWidth = valueLineStroke
    }
}

@BindingAdapter("innerCircleStroke")
fun HorizontalBarChart.innerCircleStroke(innerCircleStroke: Float) {
    Log.d("TAG", "innerCircleStroke: ${marksPointPaint.textSize} -> $innerCircleStroke")
    this.marksPointPaint.apply {
        textSize = innerCircleStroke
    }
}

@BindingAdapter("outerCircleStroke")
fun HorizontalBarChart.outerCircleStroke(outerCircleStroke: Float) {
    Log.d("TAG", "outerCircleStroke: ${marksPointCirclePaint.strokeWidth} -> $outerCircleStroke")
    this.marksPointCirclePaint.apply {
        strokeWidth = outerCircleStroke
    }
}

@BindingAdapter("axisLineStroke")
fun HorizontalBarChart.axisLineStroke(axisLineStroke: Float) {
    Log.d("TAG", "axisLineStroke: ${linePaint.strokeWidth} -> $axisLineStroke")
    this.linePaint.apply {
        strokeWidth = axisLineStroke
    }
}

@BindingAdapter("axisSampleLineYStroke")
fun HorizontalBarChart.axisSampleLineYStroke(axisSampleLineYStroke: Float) {
    Log.d("TAG", "axisLineStroke: ${linePaint.strokeWidth} -> $axisSampleLineYStroke")
    this.sampleLinePaint.apply {
        strokeWidth = axisSampleLineYStroke
    }
}

@BindingAdapter("outerCircleRadius")
fun HorizontalBarChart.outerCircleRadius(outerCircleRadius: Float) {
    Log.d("TAG", "outerCircleRadius: ${this.outerCircleRadius} -> $outerCircleRadius")
    this.outerCircleRadius = outerCircleRadius
}

@BindingAdapter("innerCircleRadius")
fun HorizontalBarChart.innerCircleRadius(innerCircleRadius: Float) {
    Log.d("TAG", "innerCircleRadius: ${this.innerCircleRadius} -> $innerCircleRadius")
    this.innerCircleRadius = innerCircleRadius
}

@BindingAdapter("outerCircleAndValueSpace")
fun HorizontalBarChart.outerCircleAndValueSpace(outerCircleAndValueSpace: Float) {
    Log.d(
        "TAG",
        "outerCircleAndValueSpace: ${this.outerCircleAndValueSpace} -> $outerCircleAndValueSpace"
    )
    this.outerCircleAndValueSpace = outerCircleAndValueSpace
}

@BindingAdapter("axisLineColor")
fun HorizontalBarChart.axisLineColor(@ColorInt axisLineColor: Int) {
    this.linePaint.color = axisLineColor
}

@BindingAdapter("axisSampleYLineColor")
fun HorizontalBarChart.axisSampleYLineColor(@ColorInt axisSampleYLineColor: Int) {
    this.sampleLinePaint.color = axisSampleYLineColor
}

@BindingAdapter("xAxisNameColor")
fun HorizontalBarChart.xAxisNameColor(@ColorInt xAxisNameColor: Int) {
    this.xAxisNamePaint.color = xAxisNameColor
}

@BindingAdapter("yAxisNameColor")
fun HorizontalBarChart.yAxisNameColor(@ColorInt yAxisNameColor: Int) {
    this.yAxisNamePaint.color = yAxisNameColor
}

@BindingAdapter("innerCircleColor")
fun HorizontalBarChart.innerCircleColor(@ColorInt innerCircleColor: Int) {
    this.marksPointPaint.color = innerCircleColor
}

@BindingAdapter("outerCircleColor")
fun HorizontalBarChart.outerCircleColor(@ColorInt outerCircleColor: Int) {
    this.marksPointCirclePaint.color = outerCircleColor
}

@BindingAdapter("valueLineColor")
fun HorizontalBarChart.valueLineColor(@ColorInt valueLineColor: Int) {
    this.marksPointCircleLinePaint.color = valueLineColor
}

@BindingAdapter("valueTextColor")
fun HorizontalBarChart.valueTextColor(@ColorInt valueTextColor: Int) {
    this.marksPointCircleNamePaint.color = valueTextColor
}
