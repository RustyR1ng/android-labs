package com.example.labs.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.htmlEncode
import androidx.core.text.parseAsHtml
import androidx.fragment.app.Fragment
import com.example.labs.R
import kotlinx.android.synthetic.main.lab3_frag.*
import kotlinx.coroutines.*
import java.util.*
import java.net.URL
import org.jsoup.Jsoup

typealias LumaListener = (luma: Double) -> Unit


class Lab3 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.lab3_frag, container, false)
        val scope = CoroutineScope(Job())
        val number : TextView = root.findViewById(R.id.number)
        val responseView : TextView = root.findViewById(R.id.response)
        val downloadBtn : Button = root.findViewById(R.id.download_button)
        var url = "https://yandex.ru/pogoda/moscow?utm_source=serp&utm_campaign=wizard&utm_medium=desktop&utm_content=wizard_desktop_main&utm_term=title"
        var response : String
        downloadBtn.setOnClickListener{
            val job = scope.async {
                fetchWebsiteContents(url)
            }
            MainScope().launch {
                responseView.text = job.await()
                val text = responseView.text
                val key ="Текущая температура"
                val start = text.indexOf(key) + key.length
                val end = text.indexOf(" ", start)
                number.text = text.substring(start,end)
            }

        }

        return root
    }
    suspend fun fetchWebsiteContents(url: String):String {

        return Jsoup.connect(url).get().text()
    }


}
