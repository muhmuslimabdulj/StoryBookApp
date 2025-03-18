package com.mmuslimabdulj.storybookapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mmuslimabdulj.storybookapp.R
import com.mmuslimabdulj.storybookapp.model.Story
import com.mmuslimabdulj.storybookapp.ui.DetailStoryActivity
import com.mmuslimabdulj.storybookapp.ui.ListStoryActivity
import com.mmuslimabdulj.storybookapp.util.Constants
import com.squareup.picasso.Picasso

class ListStoryAdapter(private val storyList: List<Story>, private val context: Context) :
    RecyclerView.Adapter<ListStoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = storyList[position]
            holder.tvTitle.text = story.title
            Picasso.get().load(story.image).into(holder.imgCover)
            holder.layClick.setOnClickListener {
                Constants.title = story.title
                Constants.storyText = story.storyText
                Constants.audio = story.audio
                Constants.image = story.image
            val intent = Intent(context, DetailStoryActivity::class.java)
            context.startActivity(intent)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_story_title)
        val imgCover: ImageView = itemView.findViewById(R.id.iv_story_cover)
        val layClick: CardView = itemView.findViewById(R.id.layclick)
    }
}