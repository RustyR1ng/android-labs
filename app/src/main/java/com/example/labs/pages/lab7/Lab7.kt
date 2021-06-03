package com.example.labs.pages.lab7

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.labs.R
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

class Lab7 : Fragment() {
    lateinit var botTV: TextView
    lateinit var userTV: TextView

    lateinit var messageEdit : EditText
    lateinit var sendMessageBtn : ImageButton

    lateinit var connectBtn : Button

    private lateinit var webSocketClient: WebSocketClient
    val socketUri: URI? = URI(WEB_SOCKET_URL)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.lab7_frag, container, false)

        botTV = root.findViewById(R.id.bot)
        userTV = root.findViewById(R.id.user)

        messageEdit = root.findViewById(R.id.message)
        sendMessageBtn = root.findViewById(R.id.send_message)

        connectBtn = root.findViewById(R.id.connect_to_socket)

        connectBtn.setOnClickListener{
            createWebSocketClient(socketUri)
            webSocketClient.connect()

        }
        sendMessageBtn.setOnClickListener{
            webSocketClient.send("Фото")
        }


        return root
    }

    private fun createWebSocketClient(uri: URI?) {
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {

                Log.d("data", handshakedata.toString())
            }

            override fun onMessage(message: String?) {
                if (message != null) {
                    Log.d("BOT", message)
                }
            }


            override fun onClose(code: Int, reason: String?, remote: Boolean) {
              webSocketClient.close()
            }

            override fun onError(ex: Exception?) {
                Log.d("ERROR", ex.toString())
            }

        }
    }

    companion object {
        private const val WEB_SOCKET_URL = ""
    }
}