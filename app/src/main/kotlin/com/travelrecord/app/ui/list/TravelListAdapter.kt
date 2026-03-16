package com.travelrecord.app.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travelrecord.app.data.entity.TravelRecord
import com.travelrecord.app.databinding.ItemTravelRecordBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView adapter for travel records list
 */
class TravelListAdapter(
    private val onItemClick: (TravelRecord) -> Unit,
    private val onDeleteClick: (TravelRecord) -> Unit
) : ListAdapter<TravelRecord, TravelListAdapter.TravelRecordViewHolder>(TravelRecordDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelRecordViewHolder {
        val binding = ItemTravelRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TravelRecordViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TravelRecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class TravelRecordViewHolder(
        private val binding: ItemTravelRecordBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        fun bind(travelRecord: TravelRecord) {
            binding.apply {
                textTitle.text = travelRecord.title
                textPurpose.text = travelRecord.purpose
                textTask.text = travelRecord.task
                textStartDate.text = dateFormat.format(Date(travelRecord.startDate))
                textTotalExpense.text = "¥${String.format("%.2f", travelRecord.totalExpense)}"
                
                // Set end date if available
                if (travelRecord.endDate != null) {
                    textEndDate.text = dateFormat.format(Date(travelRecord.endDate))
                } else {
                    textEndDate.text = "进行中"
                }
                
                // Click listeners
                root.setOnClickListener {
                    onItemClick(travelRecord)
                }
                
                buttonDelete.setOnClickListener {
                    onDeleteClick(travelRecord)
                }
            }
        }
    }
}

/**
 * DiffUtil callback for efficient list updates
 */
class TravelRecordDiffCallback : DiffUtil.ItemCallback<TravelRecord>() {
    override fun areItemsTheSame(oldItem: TravelRecord, newItem: TravelRecord): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: TravelRecord, newItem: TravelRecord): Boolean {
        return oldItem == newItem
    }
}