package com.example.happyplace.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happyplace.adapters.HappyPlaceAdapter
import com.example.happyplace.database.DatabaseHandler
import com.example.happyplace.databinding.ActivityMainBinding
import com.example.happyplace.model.HappyPlaceModel

class MainActivity : AppCompatActivity() {

    companion object {
        const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        const val EXTRA_PLACE_DETAILS = "extra_place_details"
    }

    var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.fabAddHappyPlace?.setOnClickListener {
            var intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }

        getHappyPlaceListFromLocalDb()
    }

    private fun setUpHappyPlaceRV(happyPlaceList: ArrayList<HappyPlaceModel>){
        binding?.happyPlaceListRV?.layoutManager = LinearLayoutManager(this)
        binding?.happyPlaceListRV?.setHasFixedSize(true)

        val placeAdapter = HappyPlaceAdapter(happyPlaceList)

        binding?.happyPlaceListRV?.adapter = placeAdapter

        placeAdapter.setOnClickListener(object : HappyPlaceAdapter.OnClickListener{
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity, HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }

        })
    }

    private fun getHappyPlaceListFromLocalDb(){
        val dbHandler = DatabaseHandler(this)
        val getHappyPlaceList: ArrayList<HappyPlaceModel> = dbHandler.getHappyPlaceList()

        if (getHappyPlaceList.size > 0){
            binding?.happyPlaceListRV?.visibility = View.VISIBLE
            binding?.notFoundHappyPlaceTV?.visibility = View.GONE

            setUpHappyPlaceRV(getHappyPlaceList)
        } else {
            binding?.happyPlaceListRV?.visibility = View.GONE
            binding?.notFoundHappyPlaceTV?.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                getHappyPlaceListFromLocalDb()
            } else {
                Log.e("Activity: ", "Cancelled or backPress")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}