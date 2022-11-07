package com.example.happyplace

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.happyplace.databinding.ActivityAddHappyPlaceBinding
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    var binding: ActivityAddHappyPlaceBinding? = null

    private var cal = Calendar.getInstance()
    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.addPlaceTB)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.addPlaceTB?.setNavigationOnClickListener {
            //onBackPressed()
            onBackPressedDispatcher.onBackPressed()
        }
        
        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDateInView()
        }

        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
    }



    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.et_date -> {
             DatePickerDialog(this@AddHappyPlaceActivity,
                 dateSetListener,
                 cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            R.id.tv_add_image -> {
                //TODO Create a variable to hold the AlertDialog Builder
                val pictureDialog = AlertDialog.Builder(this)
                //set the title for the builder
                pictureDialog.setTitle("Select Action")
                //Create a variable of array item for the alert dialog items
                val pictureDialogItems = arrayOf("Select Photo from Gallery", "Select Photo from Camera")
                //Set the items for the dialog and give function for each items when it is clicked
                pictureDialog.setItems(pictureDialogItems){
                    _, which ->
                    when(which){
                        0 ->  {Toast.makeText(this@AddHappyPlaceActivity, "You are useing the Gallery", Toast.LENGTH_LONG).show()
                            //choosePhotoFromGallery()
                        }
                        1 -> Toast.makeText(this@AddHappyPlaceActivity, "You are useing the camera", Toast.LENGTH_LONG).show()
                    }
                }
                pictureDialog.show()
            }
        }
    }

    private fun updateDateInView(){
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding?.etDate?.setText(sdf.format(cal.time).toString())
    }

    private fun choosePhotoFromGallery(){

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}