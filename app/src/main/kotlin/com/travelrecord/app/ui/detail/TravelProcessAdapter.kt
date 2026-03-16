package com.travelrecord.app.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travelrecord.app.data.entity.TravelProcess
import com.travelrecord.app.databinding.ItemTravelProcessBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView adapter for travel processes
 */
class TravelProcessAdapter(
    private val onEditClick: (TravelProcess) -> Unit,
    private val onDeleteClick: (TravelProcess) -> Unit
) : ListAdapter<TravelProcess, TravelProcessAdapter.TravelProcessViewHolder>(TravelProcessDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelProcessViewHolder {
        val binding = ItemTravelProcessBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TravelProcessViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TravelProcessViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class TravelProcessViewHolder(
        private val binding: ItemTravelProcessBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        
        fun bind(process: TravelProcess) {
            binding.apply {
                textDescription.text = process.description
                textTimestamp.text = dateFormat.format(Date(process.timestamp))
                
                buttonEdit.setOnClickListener {
                    onEditClick(process)
                }
                
                buttonDelete.setOnClickListener {
                    onDeleteClick(process)
                }
            }
        }
    }
}

class TravelProcessDiffCallback : DiffUtil.ItemCallback<TravelProcess>() {
    override fun areItemsTheSame(oldItem: TravelProcess, newItem: TravelProcess): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: TravelProcess, newItem: TravelProcess): Boolean {
        return oldItem == newItem
    }
}