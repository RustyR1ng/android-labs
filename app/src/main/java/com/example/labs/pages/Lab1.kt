package com.example.labs.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
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
            var job = scope.launch {
                progressFill()
            }
        }
        buttonL = root.findViewById(R.id.delay_button)
        buttonL.setOnLongClickListener(OnLongClickListener {
            var job = scope.launch {
                progressFill()
            }
            true
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

