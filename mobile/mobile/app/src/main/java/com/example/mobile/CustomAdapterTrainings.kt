package com.example.mobile


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapterTrainings(private val mList: ArrayList<Training>) : RecyclerView.Adapter<CustomAdapterTrainings.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_training, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        val count = "View count: " + ItemsViewModel.viewsCount;

        holder.imageView.setImageBitmap(ItemsViewModel.image)
        holder.titleView.text = ItemsViewModel.title
        holder.countView.text = count
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun getTrainingAt(position: Int): Training = mList[position]

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val imageView: ImageView = itemView.findViewById(R.id.imageTraining)
        val titleView: TextView = itemView.findViewById(R.id.titleTraining)
        val countView: TextView = itemView.findViewById(R.id.countTraining)

    }
}