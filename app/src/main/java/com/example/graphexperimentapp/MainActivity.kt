package com.example.graphexperimentapp

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.graphexperimentapp.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)

        binding.horizontalBarchart.graphDataAsInfoList(
            listOf<HorizontalBarChart.GraphDataInfo>(
                GraphInfo("1", 10f), //5
                GraphInfo("2", 0F),//3.6
                GraphInfo("3", 2f),//7.7
                GraphInfo("4", 3f),//6.3
                GraphInfo("5", 4f), //7.7
                GraphInfo("6", 9f),   //5.6
            )
        )
        binding.horizontalBarchart.startGraphAnimatorOnDelay()

        binding.horizontalBarchart.setOnItemClicked { index, graphDataInfo ->
            Toast.makeText(applicationContext, "Clicked: $index - ${graphDataInfo.getValueString()}", Toast.LENGTH_SHORT).show()
        }

    }
}

private data class GraphInfo(private val myKey: String, val myValue: Float, val myStatus: String = ""):
    HorizontalBarChart.GraphDataInfo {
    override fun getKey() = myKey
    override fun getValue() = myValue
    override fun getValueString(): String {
        return "${getValue().roundToInt()}m 08s"
    }

    override fun getInnerCircleColor(): Int {
        return Color.MAGENTA
    }

    override fun getStatus() = myStatus
}