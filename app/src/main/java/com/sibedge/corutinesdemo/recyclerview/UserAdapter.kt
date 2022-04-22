package com.sibedge.corutinesdemo.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.calculateDiff
import androidx.recyclerview.widget.RecyclerView
import com.sibedge.corutinesdemo.User
import com.sibedge.corutinesdemo.databinding.ItemUserBinding

class UserAdapter() : RecyclerView.Adapter<UserAdapter.UserHolder>() {

    private val items: MutableList<User> = mutableListOf()

    class UserHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: User) {
            binding.email.text = data.email
            binding.name.text = "${data.name} id = ${data.id}"

            if (data.myCompany != "") {
                binding.company.text = data.myCompany
                binding.company.isVisible = true
            } else {
                binding.company.isVisible = false
            }
            if (data.postCounts > 0) {
                binding.postCounts.text = "posts => ${data.postCounts}"
                binding.postCounts.isVisible = true
            } else {
                binding.postCounts.isVisible = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val binding = ItemUserBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return UserHolder(binding)
    }


    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun update(list: List<User>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
//
//        with(object : DiffUtil.Callback() {
//            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
//                items[oldItemPosition] === list[newItemPosition]
//
//            override fun getOldListSize(): Int = items.size
//            override fun getNewListSize(): Int = list.size
//
//            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
//                items[oldItemPosition].myCompany == list[newItemPosition].myCompany &&
//                        items[oldItemPosition].postCounts == list[newItemPosition].postCounts &&
//                        items[oldItemPosition].id == list[newItemPosition].id
//
//        }) {
//            calculateDiff(this)
//        }.run {
//            items.clear()
//            items.addAll(list)
//            dispatchUpdatesTo(this@UserAdapter)
//        }
    }


}