package com.example.happyplace.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplace.activity.AddHappyPlaceActivity
import com.example.happyplace.activity.MainActivity
import com.example.happyplace.databinding.ItemHappyPlaceBinding
import com.example.happyplace.model.HappyPlaceModel
import kotlinx.coroutines.NonDisposableHandle.parent


class HappyPlaceAdapter(val happyPlacelist: ArrayList<HappyPlaceModel>,
                        private val context: Context
                        ): RecyclerView.Adapter<HappyPlaceAdapter.MainViewHolder>(){

    private var onClickListener: OnClickListener? = null

    inner class MainViewHolder(val itemHappyPlaceBinding: ItemHappyPlaceBinding): RecyclerView.ViewHolder(itemHappyPlaceBinding.root){
        fun bindItem(list: HappyPlaceModel){
            itemHappyPlaceBinding.titleTV.text = list.title
            itemHappyPlaceBinding.placeImageIV.setImageURI(Uri.parse(list.image))
            itemHappyPlaceBinding.descriptionTV.text = list.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(ItemHappyPlaceBinding.inflate(LayoutInflater.from(context), parent, false))
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

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int){
        val intent = Intent(context, AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, happyPlacelist[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return happyPlacelist.size
    }

    interface OnClickListener {
        fun onClick(position: Int, model: HappyPlaceModel)
    }

}
