package com.t_bank.financial_assistant.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.t_bank.financial_assistant.R
import com.t_bank.financial_assistant.databinding.ItemTransactionBinding
import com.t_bank.local.room.entity.TransactionEntity

class TransactionAdapter(
    private val onDeleteClick: (Transaction) -> Unit
) : ListAdapter<TransactionAdapter.Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    class TransactionViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction, onDeleteClick: (Transaction) -> Unit) {
            binding.editTextDate.text = transaction.date
            binding.iconTransaction.setImageResource(transaction.icon)
            binding.nameReceiptText.text = transaction.source
            binding.categoryText.text = transaction.category
            binding.amountText.text = transaction.amount
            binding.commentText.text = transaction.comment

            // Установите обработчик нажатия на кнопку удаления
            binding.deleteBtn.setOnClickListener {
                onDeleteClick(transaction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction, onDeleteClick)
    }

    data class Transaction(
        val date: String,
        val icon: Int = R.color.textColorHint,
        val source: String,
        val category: String,
        val amount: String,
        val comment: String
    )

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}