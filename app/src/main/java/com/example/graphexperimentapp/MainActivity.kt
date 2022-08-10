package com.example.graphexperimentapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.graphexperimentapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)

        binding.horizontalBarchart.graphDataAsInfoList(
            listOf<HorizontalBarChart.GraphDataInfo>(
                GraphInfo("1", 5f), //5
                GraphInfo("2", 0F),//3.6
                GraphInfo("3", 4.3f),//7.7
                GraphInfo("4", 8f),//6.3
                GraphInfo("5", 7.8f), //7.7
                GraphInfo("6", 4.2f),   //5.6
            )
        )
        binding.horizontalBarchart.startGraphAnimatorOnDelay()

    }
}

private data class GraphInfo(private val myKey: String, val myValue: Float, val myStatus: String = ""):
    HorizontalBarChart.GraphDataInfo {
    override fun getKey() = myKey
    override fun getValue() = myValue
    override fun getStatus() = myStatus
}