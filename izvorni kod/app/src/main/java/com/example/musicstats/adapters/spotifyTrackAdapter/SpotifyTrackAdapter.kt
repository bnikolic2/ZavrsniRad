package com.example.musicstats.adapters.spotifyTrackAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.musicstats.R
import com.google.android.material.imageview.ShapeableImageView
import org.w3c.dom.Text

class SpotifyTrackAdapter(private val trackList: ArrayList<SpotifyTrackData>): RecyclerView.Adapter<SpotifyTrackAdapter.SpotifyTrackViewHolder>() {


    class SpotifyTrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val titleImage: ShapeableImageView = itemView.findViewById(R.id.trackTitleImage)
        val trackTitle: TextView = itemView.findViewById(R.id.trackListTitle)
        val trackArtist: TextView = itemView.findViewById(R.id.trackListArtist)
        val trackAlbum: TextView = itemView.findViewById(R.id.trackListAlbum)
        val trackRanking: TextView = itemView.findViewById(R.id.trackRanking)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotifyTrackViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.track_list_item, parent, false)
        return SpotifyTrackViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: SpotifyTrackViewHolder, position: Int) {
        val currentItem = trackList[position]
        holder.titleImage.load(currentItem.trackTitleImage)
        holder.trackTitle.text = currentItem.trackTitle
        holder.trackArtist.text = currentItem.trackArtist
        holder.trackAlbum.text = currentItem.trackAlbum
        holder.trackRanking.text = currentItem.trackRanking
    }

}