package com.sdzshn3.android.websocketpractice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sdzshn3.android.websocketpractice.databinding.MyMessageLayoutBinding
import com.sdzshn3.android.websocketpractice.databinding.OthersMessageLayoutBinding

const val OTHERS_MESSAGE_TYPE = 2
const val MY_MESSAGE_TYPE = 1

class Adapter(private val username: String) : ListAdapter<Message, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    class MyMessageViewHolder(val binding: MyMessageLayoutBinding): RecyclerView.ViewHolder(binding.root)
    class OthersMessageViewHolder(val binding: OthersMessageLayoutBinding): RecyclerView.ViewHolder(binding.root)

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Message> = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.time == newItem.time
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(currentList[position].sender == username) {
            MY_MESSAGE_TYPE
        } else {
            OTHERS_MESSAGE_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MY_MESSAGE_TYPE) {
            MyMessageViewHolder(
                MyMessageLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            OthersMessageViewHolder(
                OthersMessageLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyMessageViewHolder -> {
                holder.binding.apply {
                    textView.text = currentList[position].message
                }
            }
            is OthersMessageViewHolder -> {
                holder.binding.apply {
                    sender.text = currentList[position].sender
                    textView.text = currentList[position].message
                }
            }
        }
    }
}