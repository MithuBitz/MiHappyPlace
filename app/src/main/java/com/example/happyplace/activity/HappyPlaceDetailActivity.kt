package com.example.happyplace.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplace.R
import com.example.happyplace.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplace.databinding.ActivityHappyPlaceDetailBinding
import com.example.happyplace.model.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {

    private var binding: ActivityHappyPlaceDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        var detailHappyPlaceModel: HappyPlaceModel? = null

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            detailHappyPlaceModel = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel?
        }

        if (detailHappyPlaceModel != null){
            setSupportActionBar(binding?.happyPlaceDetailTB)
            if (supportActionBar != null){
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.title = detailHappyPlaceModel.title
            }
            binding?.happyPlaceDetailTB?.setNavigationOnClickListener {
                //onBackPressed()
                onBackPressedDispatcher.onBackPressed()
            }

            binding?.ivPlaceImage?.setImageURI(Uri.parse(detailHappyPlaceModel.image))
            binding?.tvDescription?.text = detailHappyPlaceModel.description
            binding?.tvLocation?.text = detailHappyPlaceModel.location
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}