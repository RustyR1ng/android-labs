package com.example.labs.pages.lab3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.labs.R
import com.example.labs.pages.lab6.Lab6ViewModel
import kotlinx.coroutines.*
import org.jsoup.Jsoup

class Lab3 : Fragment() {
    private lateinit var lab3ViewModel: Lab3ViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lab3ViewModel = ViewModelProvider(this).get(Lab3ViewModel::class.java)

        val root = inflater.inflate(R.layout.lab3_frag, container, false)
        val scope = CoroutineScope(Job())
        val number: TextView = root.findViewById(R.id.number)
        val responseView: TextView = root.findViewById(R.id.response)
        val downloadBtn: Button = root.findViewById(R.id.download_button)
        var url =
            "https://yandex.ru/pogoda/moscow?utm_source=serp&utm_campaign=wizard&utm_medium=desktop&utm_content=wizard_desktop_main&utm_term=title"
        var response: String
        downloadBtn.setOnClickListener {
            val job = scope.async {
                fetchWebsiteContents(url)
            }
            MainScope().launch {
                responseView.text = job.await()
                val text = responseView.text
                val key = "Текущая температура"
                val start = text.indexOf(key) + key.length
                val end = text.indexOf(" ", start)
                number.text = text.substring(start, end)
            }

        }

        return root
    }

    suspend fun fetchWebsiteContents(url: String): String {

        return Jsoup.connect(url).get().text()
    }


}
