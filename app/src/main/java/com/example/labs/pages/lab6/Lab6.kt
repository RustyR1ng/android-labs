package com.example.labs.pages.lab6

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.labs.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class Lab6 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.lab6_frag, container, false)

        val decryptBtn : Button = root.findViewById(R.id.encrypt)
        val encryptBtn : Button = root.findViewById(R.id.decrypt)

        val keyInput : TextInputEditText = root.findViewById(R.id.key)

        val resultTV : TextView = root.findViewById(R.id.result)

        encryptBtn.setOnClickListener{
            val text = getFileText()
            val key = keyInput.text.toString()
            resultTV.text = encrypt(key, text)
        }

        decryptBtn.setOnClickListener{
            val text = getFileText()
            val key = keyInput.text.toString()
            resultTV.text = decrypt(key, text)
        }

        return root
    }

    fun encrypt(key: String, text: String) : String{
        var res = ""
        return res
    }

    fun decrypt(key: String, text: String) : String{
        var res = ""
        return res
    }

    fun getFileText(): String{
        var text = ""
        return text
    }
}
