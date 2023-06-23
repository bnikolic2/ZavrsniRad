package com.example.musicstats.adapters.trackRecommendationAdapter


import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load

import com.example.musicstats.R

import com.google.android.material.imageview.ShapeableImageView


@Suppress("NAME_SHADOWING")
class TrackRecommendationAdapter(
    val trackList: ArrayList<TrackRecommendationData>
) : RecyclerView.Adapter<TrackRecommendationAdapter.TrackRecommendationViewHolder>() {

    class TrackRecommendationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recommendedTrackImage: ShapeableImageView =
            itemView.findViewById(R.id.recommendedTrackImage)
        val recommendedTrackTitle: TextView = itemView.findViewById(R.id.recommendedTrackName)
        val recommendedTrackArtist: TextView = itemView.findViewById(R.id.recommendedTrackArtist)
        val recommendedTrackDuration: TextView =
            itemView.findViewById(R.id.recommendedTrackDuration)
        val recommendedTrackLink: Button = itemView.findViewById(R.id.recommendedTrackLink)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackRecommendationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recommendation_list_item, parent, false)
        return TrackRecommendationViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackRecommendationViewHolder, position: Int) {
        val currentItem = trackList[position]

        holder.recommendedTrackImage.load(currentItem.recommendedTrackImage)
        holder.recommendedTrackTitle.text = currentItem.recommendedTrackName
        holder.recommendedTrackArtist.text = currentItem.recommendedTrackArtist
        holder.recommendedTrackDuration.text = currentItem.recommendedTrackDuration

        if(currentItem.recommendedTrackDuration.length > 0){
            holder.recommendedTrackDuration.text = "release date: " + currentItem.recommendedTrackDuration
        }else{
            holder.recommendedTrackDuration.text = "No release date available"
        }
        //button
        if (currentItem.recommendedTrackLink.length > 0){
            holder.recommendedTrackLink.setOnClickListener{
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.recommendedTrackLink))
                holder.itemView.context.startActivity(i)
            }
        }else{

            holder.recommendedTrackLink.setOnClickListener{
                Toast.makeText(holder.itemView.context,"No playback available for this track", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
