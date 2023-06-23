package com.example.musicstats.adapters.spotifyArtistAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.musicstats.R
import com.google.android.material.imageview.ShapeableImageView

class SpotifyArtistAdapter(private val artistList: ArrayList<SpotifyArtistData>):RecyclerView.Adapter<SpotifyArtistAdapter.SpotifyArtistViewHolder>() {

    class SpotifyArtistViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val artistImage: ShapeableImageView = itemView.findViewById(R.id.artistImage)
        val artistName: TextView = itemView.findViewById(R.id.artistName)
        val artistGenres: TextView = itemView.findViewById(R.id.artistGenres)
        val artistPopularity: TextView = itemView.findViewById(R.id.artistPopularity)
        val artistRanking: TextView = itemView.findViewById(R.id.artistRanking)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotifyArtistViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.artists_list_item, parent, false)
        return SpotifyArtistViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return artistList.size
    }

    override fun onBindViewHolder(holder: SpotifyArtistViewHolder, position: Int) {
        val currentItem = artistList[position]
        holder.artistImage.load(currentItem.artistImage)
        holder.artistName.text = currentItem.artistName
        holder.artistGenres.text = currentItem.artistGenres
        holder.artistPopularity.text = currentItem.artistPopularity
        holder.artistRanking.text = currentItem.artistRanking
    }

}