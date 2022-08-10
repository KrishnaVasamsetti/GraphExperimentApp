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

//        binding.horizontalBarchart.graphDataAsInfoList(
//            listOf<HorizontalBarChart.GraphDataInfo>(
//                GraphInfo("T", 76f),
//                GraphInfo("E", 23F),
//                GraphInfo("SCI", 43f),
//                GraphInfo("SS", 98f),
//            )
//        )
        binding.horizontalBarchart.startGraphAnimatorOnDelay()

    }
}

private data class GraphInfo(private val myKey: String, val myValue: Float):
    HorizontalBarChart.GraphDataInfo {
    override fun getKey() = myKey
    override fun getValue() = myValue
}