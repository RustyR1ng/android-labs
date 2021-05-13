package com.example.labs.pages.lab1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.labs.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class Lab1 : Fragment() {
    lateinit var buttonP: Button
    lateinit var progressBar: ProgressBar
    lateinit var buttonL: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val root = inflater.inflate(R.layout.lab1_frag, container, false)
        progressBar = root.findViewById(R.id.progressBar)
        buttonP = root.findViewById(R.id.click_button)
        val scope = CoroutineScope(Job())
        buttonP.setOnClickListener() {
            progressBar.progress = 0
        }
        buttonL = root.findViewById(R.id.delay_button)
        var pressed = false
        buttonL.setOnTouchListener(OnTouchListener { v, event ->

            if (event.action == MotionEvent.ACTION_DOWN) {
                pressed = true
                var job = scope.launch {
                    while (pressed) {
                        progressBar.progress += 1
                        delay(50)
                    }
                }
            }
            if (event.action == MotionEvent.ACTION_UP) {

                pressed = false
                progressBar.progress = if (progressBar.progress == 100) 100 else 0
            }

            false
        })
        return root
    }

    suspend fun progressFill() {
        progressBar.progress = 0
        while (progressBar.progress < 100) {
            progressBar.progress += 1
            delay(100)
        }
        progressBar.progress = 0
    }
}

