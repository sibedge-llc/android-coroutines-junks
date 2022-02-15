package com.sibedge.corutinesdemo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.sibedge.corutinesdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val model: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private var counter: Int = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        with(binding) {
            coroutineOnMainThread.setOnClickListener {
                model.start(FunctionType.OnMainThread)
            }
            coroutineOnIOThread.setOnClickListener {
                model.start(FunctionType.OnIOThread)
            }
            coroutineCallback.setOnClickListener {
                model.start(FunctionType.Callback)
            }
            coroutineSuspend.setOnClickListener {
                model.start(FunctionType.Suspend)
            }
        }

        model.getData().observe(this) {
            binding.mainText.text = it
        }

        runnable = Runnable {
            counter++
            binding.timer.text = "counter = $counter"
            handler.postDelayed(runnable, 1000)
        }
        runnable.run()
    }
}