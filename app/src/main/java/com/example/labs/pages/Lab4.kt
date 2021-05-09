package com.example.labs.pages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.labs.R
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.authentication.LoginActivity


class Lab4 : Fragment() {

    lateinit var mSpotifyAppRemote: SpotifyAppRemote
    private lateinit var tokenView: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.lab4_frag, container, false)

        tokenView = root.findViewById(R.id.token)
        val signInBtn: Button = root.findViewById(R.id.sign_in_button)
        signInBtn.setOnClickListener {
            auth()
        }

        return root
    }


    private fun auth() {
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()
        SpotifyAppRemote.connect(context, connectionParams,
            object : Connector.ConnectionListener {
                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote
                    Log.d("MainActivity", "Connected! Yay!")
                    // Now you can start interacting with App Remote
                    connected()
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("MainActivity", throwable.message, throwable)

                    // Something went wrong when attempting to connect! Handle errors here
                }
            })
        val builder = AuthenticationRequest.Builder(CLIENT_ID,
            AuthenticationResponse.Type.TOKEN,
            REDIRECT_URI)

        builder.setScopes(SCOPES)

        val request = builder.build()

        AuthenticationClient.openLoginActivity(activity,
            LoginActivity.REQUEST_CODE,
            request)
    }

    private fun connected() {
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        //super.onActivityResult(requestCode, resultCode, intent)

        // Check if result comes from the correct activity
        if (requestCode == LoginActivity.REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    token = response.accessToken
                    tokenView.text = token
                }
                AuthenticationResponse.Type.ERROR -> {
                    //
                }
                else -> {
                    //
                }
            }
        }
    }


    companion object {
        const val BASE_URL = "https://api.spotify.com/v1/me/"
        private const val CLIENT_ID = "389518e6daf84f2ba4ded65b024e200e"
        private const val REDIRECT_URI = "http://com.example.labs/callback"
        private val SCOPES = arrayOf("streaming", "user-library-read")
        lateinit var token : String
    }
}

