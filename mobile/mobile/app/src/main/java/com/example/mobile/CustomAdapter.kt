package com.example.mobile

import Note
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val mList: ArrayList<Note>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.titleView.text = ItemsViewModel.title
        holder.textView.text = ItemsViewModel.text

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun getNoteAt(position: Int): Note = mList[position]

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val titleView: TextView = itemView.findViewById(R.id.item_note_title)
        val textView: TextView = itemView.findViewById(R.id.item_note_content)
    }
}