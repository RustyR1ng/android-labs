package com.example.labs.pages

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.labs.R


class Lab1 : Fragment() {
    lateinit var buttonP:Button
    lateinit var progressBar:ProgressBar
    lateinit var buttonL: Button
    var progressStatus = 0
    var handler = Handler()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.lab1_frag, container, false)
        progressBar = root.findViewById(com.example.labs.R.id.progressBar)
        buttonP = root.findViewById(R.id.click_button)
        buttonP.setOnClickListener() {
            progressFill()
        }
        buttonL = root.findViewById(R.id.delay_button)
        buttonL.setOnLongClickListener(OnLongClickListener {
            progressFill()
            true
        })
        return root
    }
        fun progressFill() {
            Thread(Runnable {
                while (progressStatus < 100) {
                    // update progress status
                    progressStatus += 1

                    // sleep the thread for 100 milliseconds
                    Thread.sleep(100)

                    // update the progress bar
                    handler.post {
                        progressBar.progress = progressStatus
                    }
                }
                progressStatus = 0
            }).start()
        }
}
