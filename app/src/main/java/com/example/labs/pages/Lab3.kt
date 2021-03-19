package com.example.labs.pages

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import com.example.labs.R

/* Fragment used as page 1 */
class Lab3 : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root =inflater.inflate(R.layout.lab3_frag, container, false)

        return root

    }

}