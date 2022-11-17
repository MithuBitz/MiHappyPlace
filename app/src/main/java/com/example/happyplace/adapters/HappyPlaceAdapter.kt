package com.example.happyplace.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplace.databinding.ItemHappyPlaceBinding
import com.example.happyplace.model.HappyPlaceModel


class HappyPlaceAdapter(val happyPlacelist: ArrayList<HappyPlaceModel>): RecyclerView.Adapter<HappyPlaceAdapter.MainViewHolder>(){

    private var onClickListener: OnClickListener? = null

    inner class MainViewHolder(val itemHappyPlaceBinding: ItemHappyPlaceBinding): RecyclerView.ViewHolder(itemHappyPlaceBinding.root){
        fun bindItem(list: HappyPlaceModel){
            itemHappyPlaceBinding.titleTV.text = list.title
            itemHappyPlaceBinding.placeImageIV.setImageURI(Uri.parse(list.image))
            itemHappyPlaceBinding.descriptionTV.text = list.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(ItemHappyPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val list = happyPlacelist[position]
        holder.bindItem(list)

        holder.itemView.setOnClickListener{
            if (onClickListener != null){
                onClickListener!!.onClick(position, list)
            }
        }
    }

    override fun getItemCount(): Int {
        return happyPlacelist.size
    }

    interface OnClickListener {
        fun onClick(position: Int, model: HappyPlaceModel)
    }

}
