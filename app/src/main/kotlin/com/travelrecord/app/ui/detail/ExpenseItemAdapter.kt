package com.travelrecord.app.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travelrecord.app.data.entity.ExpenseItem
import com.travelrecord.app.databinding.ItemExpenseBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView adapter for expense items
 */
class ExpenseItemAdapter(
    private val onEditClick: (ExpenseItem) -> Unit,
    private val onDeleteClick: (ExpenseItem) -> Unit
) : ListAdapter<ExpenseItem, ExpenseItemAdapter.ExpenseItemViewHolder>(ExpenseItemDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseItemViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseItemViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ExpenseItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ExpenseItemViewHolder(
        private val binding: ItemExpenseBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        
        fun bind(expense: ExpenseItem) {
            binding.apply {
                textDescription.text = expense.description
                textCategory.text = expense.category
                textAmount.text = "¥${String.format("%.2f", expense.amount)}"
                textTimestamp.text = dateFormat.format(Date(expense.timestamp))
                
                buttonEdit.setOnClickListener {
                    onEditClick(expense)
                }
                
                buttonDelete.setOnClickListener {
                    onDeleteClick(expense)
                }
            }
        }
    }
}

class ExpenseItemDiffCallback : DiffUtil.ItemCallback<ExpenseItem>() {
    override fun areItemsTheSame(oldItem: ExpenseItem, newItem: ExpenseItem): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: ExpenseItem, newItem: ExpenseItem): Boolean {
        return oldItem == newItem
    }
}