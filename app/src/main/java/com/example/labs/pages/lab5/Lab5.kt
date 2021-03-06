package com.example.labs.pages.lab5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.labs.R
import com.example.labs.pages.lab4.Lab4
import com.example.labs.pages.lab6.Lab6ViewModel
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.squareup.picasso.Picasso

data class Album(var name: String, var artists: MutableList<String>, var imgUrl: String)

class Lab5 : Fragment() {

    private lateinit var lab5ViewModel: Lab5ViewModel

    lateinit var albumsRV: RecyclerView
    lateinit var mSpotifyAppRemote: SpotifyAppRemote
    lateinit var token: String
    var albumsList = mutableListOf<Album>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        lab5ViewModel = ViewModelProvider(this).get(Lab5ViewModel::class.java)

        val root = inflater.inflate(R.layout.lab5_frag, container, false)



        albumsRV = root.findViewById(R.id.albums)

        albumsRV.layoutManager = LinearLayoutManager(context)
        albumsRV.adapter = AlbumsAdapter(albumsList)
        val reloadBtn: Button = root.findViewById(R.id.reload_btn)
        reloadBtn.setOnClickListener {
            token = Lab4.token
            val queue: RequestQueue = Volley.newRequestQueue(context);
            queue.add(spotifyAlbumsRequest())
        }
        return root
    }

    class AlbumsAdapter(private val albumsList: MutableList<Album>) :
        RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder>() {
        inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var name: TextView = itemView.findViewById(R.id.name)
            var artists: TextView = itemView.findViewById(R.id.artists)
            var img: ImageView = itemView.findViewById(R.id.album_cover)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.spotify_album, parent, false)
            return AlbumViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
            val item = albumsList[position]
            holder.apply {
                artists.text = item.artists.toString()
                name.text = item.name
                Picasso.get()
                    .load(item.imgUrl)
                    .into(img)
            }
        }

        override fun getItemCount() = albumsList.size

        fun updateData(newAlbums: MutableList<Album>){
            this.apply {
                albumsList.clear()
                albumsList.addAll(newAlbums)
            }
            notifyDataSetChanged()
        }
    }

    private fun spotifyAlbumsRequest(): JsonObjectRequest {

        val jsonObjectRequest = object : JsonObjectRequest(Method.GET,
            urlBuilder(Lab4.BASE_URL),
            null,
            { response ->
                var albums = response.getJSONArray("items")
                albumsList.clear()
                for (albumNum in 0 until albums.length()) {
                    val album = albums.getJSONObject(albumNum).getJSONObject("album")
                    val artists = album.getJSONArray("artists")
                    val img = album.getJSONArray("images")
                    val imgUrl = img.getJSONObject(0).getString("url")
                    var artistsList = mutableListOf<String>()
                    for (artistNum in 0 until artists.length()) {
                        val artist = artists.getJSONObject(artistNum).getString("name")
                        artistsList.add(artist)
                    }
                    albumsList.add(Album(album.getString("name"), artistsList,imgUrl))
                }

                updateUI()
            },
            { error ->
                // TODO: Handle error
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                val token = token
                val auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers
            }

        }
        return jsonObjectRequest
    }

    private fun updateUI() {
        albumsRV.adapter?.notifyDataSetChanged()
    }

    private fun urlBuilder(baseUrl: String): String {
        var url = baseUrl
        val params = "albums"
        url += params
        return url
    }


}
