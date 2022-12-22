package com.moshe.bagtravel.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moshe.bagtravel.R
import com.moshe.bagtravel.databinding.BagItemBinding
import com.moshe.bagtravel.model.Bag
import javax.inject.Inject

class BagsAdapter(private val listener: OnBagClickListener) :
    RecyclerView.Adapter<BagsAdapter.ViewHolder>() {
    private val list = mutableListOf<Bag>()

    interface OnBagClickListener {
        fun callback(position:Int)

    }

    inner class ViewHolder(binding: BagItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val bagIdTv: TextView = binding.bugIdTv
        val bagWeightTv: TextView = binding.bugWeightTv

        init {
            itemView.setOnClickListener {
                val currentBag = getItem(adapterPosition)
                val currentBagIndex = adapterPosition
                list.remove(currentBag)
                listener.callback(currentBagIndex)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            BagItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bagIdTv.text = "${list[position].id}"
        holder.bagWeightTv.text = "${list[position].weight}"
    }

    fun getItem(position: Int): Bag = list[position]

    override fun getItemCount(): Int = list.size

    fun setBag(bag: Bag) = list.add(bag)
    fun getBagsList() = list

}