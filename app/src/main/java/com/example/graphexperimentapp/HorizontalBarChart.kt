package com.example.graphexperimentapp

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import java.util.*
import kotlin.math.absoluteValue

class HorizontalBarChart : View {

    //region fields
    private var screenWidth = 1000F // default screen width
    private var screenHeight = 500F // default screen height

    internal var innerCircleRadius = 10F
    internal var outerCircleRadius = 25F
    internal var outerCircleAndValueSpace = 10F

    internal var graphStartOffset = 150F
    internal var graphEndOffset = 100F
    internal var graphTopOffset = 150F
    internal var graphBottomOffset = 100F

    private val graphWidth; get() = screenWidth - graphStartOffset - graphEndOffset
    private val graphHeight; get() = screenHeight - graphTopOffset - graphBottomOffset
    private var animatedProgressValue = 1F

    internal var extendedYAxisDistance = 50F
    internal var extendedXAxisDistance = 50F

    internal var delayAnimationInMillis = 0L

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
        strokeWidth = 5F
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
        textAlign = Paint.Align.LEFT
    }

    internal val graphData = arrayListOf<GraphDataInfo>()
//    internal val markList = arrayListOf<String>()

    internal var graphMetaData: GraphMetaData? = GraphMetaDataImpl()

    private val regionList = arrayListOf<Region>()

    //endregion fields

    //region default setup
    private fun setBarGraphData() {

        for (i in 1..6) {
            graphData.add(GraphDataInfoImpl("$i", Random().nextInt(10).toFloat()))
//            graphData.add(GraphDataInfoImpl("Hindi", Random().nextInt(100).toFloat()))
//            graphData.add(GraphDataInfoImpl("English", Random().nextInt(100).toFloat()))
//            graphData.add(GraphDataInfoImpl("Science", Random().nextInt(100).toFloat()))
//            graphData.add(GraphDataInfoImpl("Maths", Random().nextInt(100).toFloat()))
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

    //endregion default setup

    //region draw graph

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        Log.d("TAG", "onDraw: called")
        canvas?.let {
            clearGraphDataBeforeDraw()

            drawYTitle(canvas)
            drawYLineNames(canvas)
            drawYLine(canvas)
            drawYSampleRows(canvas)

            drawXTitle(canvas)
            drawXLineNames(canvas)
            drawXLine(canvas)

            drawMarkPoint(canvas)
        }
    }

    private fun clearGraphDataBeforeDraw() {
        regionList.clear()
    }

    //region Y axis
    private fun drawYTitle(canvas: Canvas) {

        graphMetaData?.getYAxisTitle()?.let { title ->
            val path = Path()
            val startX = 50F
            path.moveTo(startX, graphTopOffset)
            path.lineTo(startX, graphTopOffset + graphHeight)
            path.close()

            canvas.drawTextOnPath(
                title,
                path,
                graphHeight / 2,
                0F,
                xAxisNamePaint
            )
        }
    }

    private fun drawYLineNames(canvas: Canvas) {

        val textHalfHeight =
            (yAxisNamePaint.ascent() + yAxisNamePaint.descent()) / 2

        for (index in graphData.indices) {
            val subject = graphData[index].getKey()
            val yPos = (graphHeight * (index + 1)) / (graphData.size)
            canvas.drawText(
                subject,
                graphStartOffset - 60F,
                graphTopOffset + yPos - textHalfHeight,
                yAxisNamePaint
            )
        }
    }

    private fun drawYLine(canvas: Canvas) {
        canvas.drawLine(
            graphStartOffset,
            graphTopOffset - extendedYAxisDistance,
            graphStartOffset,
            graphTopOffset + graphHeight + extendedYAxisDistance,
            linePaint
        )
    }

    private fun drawYSampleRows(canvas: Canvas) {

        for (index in 1..graphMetaData!!.getXAxisNames().size) {
            val perValue = graphWidth / graphMetaData!!.numberOfXPortions()
            val xPos = graphStartOffset + (perValue * index)
            canvas.drawLine(
                xPos,
                graphTopOffset - 30F,
                xPos,
                graphTopOffset,
                sampleLinePaint
            )
        }

    }
    //endregion Y axis

    //region X axis
    private fun drawXLine(canvas: Canvas) {

        canvas.drawLine(
            graphStartOffset - extendedXAxisDistance,
            graphTopOffset,
            graphStartOffset + graphWidth + extendedXAxisDistance,
            graphTopOffset,
            linePaint
        )
    }

    private fun drawXTitle(canvas: Canvas) {
        graphMetaData?.getXAxisTitle()?.let { title ->
            val textHeight =
                (xAxisNamePaint.ascent() + xAxisNamePaint.descent())

            canvas.drawText(
                title,
                graphStartOffset + (graphWidth / 2),
                textHeight.absoluteValue + 10,
                xAxisNamePaint
            )
        }
    }

    private fun drawXLineNames(canvas: Canvas) {

        val yPosStart = graphTopOffset - extendedYAxisDistance

        if (graphMetaData != null) {
            for (index in 0 until graphMetaData!!.getXAxisNames().size) {
                val xName = graphMetaData!!.getXAxisNames()[index]
                val perValue = graphWidth / graphMetaData!!.numberOfXPortions()
                var xPos = graphStartOffset + (perValue * index)
                if (graphMetaData!!.skipXAxisInitialName()) {
                    xPos += perValue
                }
                canvas.drawText(xName, xPos, yPosStart, xAxisNamePaint)
            }
        }
    }
    //endregion X axis

    //region graph info rendering
    private fun drawMarkPoint(canvas: Canvas) {
        val percent = graphWidth / graphMetaData!!.numberOfXPortions()
        val textHeight =
            (marksPointCircleNamePaint.ascent() + marksPointCircleNamePaint.descent()) / 2
        val circleOccupiedWidth = marksPointPaint.strokeWidth

        val yAxisStroke = linePaint.strokeWidth / 2
        for (index in graphData.indices) {
            val marks = graphData[index].getValue()
            val xPos = graphStartOffset + ((percent * marks) * animatedProgressValue)
            val xPosHalf = graphStartOffset + (((percent * marks) * animatedProgressValue) / 2)
            val yPos = (graphHeight * (index + 1)) / graphData.size

            val lineStart = graphStartOffset + yAxisStroke
            val lineEnd = lineStart.coerceAtLeast(xPos - outerCircleRadius)
            canvas.drawLine(
                lineStart,
                graphTopOffset + yPos,
                lineEnd,
                graphTopOffset + yPos,
                marksPointCircleLinePaint
            )//Line

            canvas.drawCircle(
                xPos,
                graphTopOffset + yPos,
                innerCircleRadius,
                marksPointPaint.apply {
                    color = graphData[index].getInnerCircleColor()
                })//Dot

            canvas.drawCircle(
                xPos,
                graphTopOffset + yPos,
                outerCircleRadius,
                marksPointCirclePaint
            )// outer circle

            val rect = Rect(
                (xPos - outerCircleRadius).toInt(),
                (graphTopOffset + yPos - outerCircleRadius).toInt(),
                (xPos + outerCircleRadius).toInt(),
                (graphTopOffset + yPos + outerCircleRadius).toInt()
            )
            val clickRegion = Region(rect)
            regionList.add(clickRegion)

            if (graphData[index].getValue() > 2) {
                canvas.drawText(
                    graphData[index].getValueString(),
                    xPosHalf,
                    graphTopOffset + yPos + textHeight,
                    marksPointCircleNamePaint.apply {
                        textAlign = Paint.Align.CENTER
                    }
                )//Label
            } else {
                canvas.drawText(
                    graphData[index].getValueString(),
                    xPos + circleOccupiedWidth + outerCircleAndValueSpace + 10F,
                    graphTopOffset + yPos - textHeight,
                    marksPointCircleNamePaint.apply {
                        textAlign = Paint.Align.LEFT
                    }
                )//Label

            }

        }
    }

    //endregion graph info rendering

    //endregion draw graph

    //region onMeasure
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
        screenHeight = desiredHeight.coerceAtLeast(graphData.size * 150F)
        setMeasuredDimension(screenWidth.toInt(), screenHeight.toInt())

        Log.d("TAG", "onMeasure After: width: $desiredWidth")
        Log.d("TAG", "onMeasure After: height: $desiredHeight")
    }

    //endregion onMeasure

    //region animation
    fun startGraphAnimatorOnDelay() {
        if (delayAnimationInMillis <= 1) {
            Log.d("TAG", "startGraphAnimatorOnDelay: NOT STARTED delay: $delayAnimationInMillis")
            animatedProgressValue = 1F
        } else {
            Log.d("TAG", "startGraphAnimatorOnDelay: STARTED delay: $delayAnimationInMillis")
            animatedProgressValue = 0F
            postDelayed({
                startGraphAnimator()
            }, delayAnimationInMillis)
        }
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

    //endregion animation

    //region helper methods
    private fun isViewVisibleInScrollView(): Boolean {
        return if (parent is ScrollView) {
            val scrollBounds = Rect()
            (parent as ScrollView).getHitRect(scrollBounds)
            this.getLocalVisibleRect(scrollBounds)
        } else
            true
    }

    //endregion helper methods

    //region item click listener
    private var lineItemClickAction: (Int, GraphDataInfo) -> Unit = { index, info ->
        Log.d("HorizontalBarChart", "itemClick not handled: $index")
    }
    fun setOnItemClicked(clicked: (Int, GraphDataInfo) -> Unit) {
        lineItemClickAction = clicked
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val point = Point()
                point.x = event.x.toInt()
                point.y = event.y.toInt()
                for (index in 0 until regionList.size) {
                    if (regionList[index].contains(point.x, point.y)) {
                        lineItemClickAction.invoke(index, graphData[index])
                        return true
                    }
                }
            }
        }
        return false
    }
    //endregion item click listener

    //region helper classes
    interface GraphDataInfo {
        fun getKey(): String
        fun getValue(): Float
        fun getValueString(): String

        @ColorInt
        fun getInnerCircleColor(): Int
        fun getStatus(): String
    }

    private data class GraphDataInfoImpl(
        private val myKey: String,
        val myValue: Float,
        val myStatus: String = ""
    ) : GraphDataInfo {
        override fun getKey() = myKey
        override fun getValue() = myValue
        override fun getValueString(): String {
            return "${myValue}m 00s"
        }

        override fun getInnerCircleColor(): Int {
            return Color.LTGRAY
        }

        override fun getStatus() = myStatus
    }

    interface GraphMetaData {

        fun getXAxisNames(): List<String>

        fun skipXAxisInitialName(): Boolean

        fun numberOfXPortions(): Int

        fun getXAxisTitle(): String?

        fun getYAxisTitle(): String?
    }

    class GraphMetaDataImpl : GraphMetaData {
        override fun getXAxisNames(): List<String> {
            return arrayListOf<String>().apply {
                for (i in 1..8) {
                    add("a$i")
                }
            }
        }

        override fun skipXAxisInitialName(): Boolean {
            return true
        }

        override fun numberOfXPortions(): Int {
            return getXAxisNames().size + 2
        }

        override fun getXAxisTitle(): String {
            return "Time Taken ( Minutes )"
        }

        override fun getYAxisTitle(): String {
            return "Questions"
        }

    }

    //endregion helper classes
}

//region custom attributes

@BindingAdapter("graphData")
fun HorizontalBarChart.graphDataAsInfoList(data: List<HorizontalBarChart.GraphDataInfo>) {
    this.graphData.clear()
    this.graphData.addAll(data)
    invalidate()
}

@BindingAdapter("graphStartOffset")
fun HorizontalBarChart.graphStartOffset(graphStartOffset: Float) {
    this.graphStartOffset = graphStartOffset
}

@BindingAdapter("graphEndOffset")
fun HorizontalBarChart.graphEndOffset(graphEndOffset: Float) {
    this.graphEndOffset = graphEndOffset
}

@BindingAdapter("graphTopOffset")
fun HorizontalBarChart.graphTopOffset(graphTopOffset: Float) {
    this.graphTopOffset = graphTopOffset
}

@BindingAdapter("graphBottomOffset")
fun HorizontalBarChart.graphBottomOffset(graphBottomOffset: Float) {
    this.graphBottomOffset = graphBottomOffset
}

@BindingAdapter("extendedYAxisDistance")
fun HorizontalBarChart.extendedYAxisDistance(extendedYAxisDistance: Float) {
    this.extendedYAxisDistance = extendedYAxisDistance
}

@BindingAdapter("extendedXAxisDistance")
fun HorizontalBarChart.extendedXAxisDistance(extendedXAxisDistance: Float) {
    this.extendedXAxisDistance = extendedXAxisDistance
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

@BindingAdapter("animationInitialDelay")
fun HorizontalBarChart.animationInitialDelay(animationInitialDelay: Int) {
    this.delayAnimationInMillis = animationInitialDelay.toLong()
}

//endregion custom attributes
