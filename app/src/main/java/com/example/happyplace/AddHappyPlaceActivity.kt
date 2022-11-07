package com.example.happyplace

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.happyplace.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
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
                        0 ->
                            choosePhotoFromGallery()

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

    private fun showRationalDialogforPermission(){
        AlertDialog.Builder(this)
            .setMessage("You dont grant permission which are required by this app")
            .setPositiveButton("Go To Settings"){
                _, _ ->
                try {
                    //If user want to enable the permission then use intent to go to the Settings
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel"){
                dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun choosePhotoFromGallery(){
        Dexter.withContext(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override  fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()){ //If all the permission is granted by user
                        Toast.makeText(this@AddHappyPlaceActivity, "All Permission Granted so you have to select image from Gallery", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                    //If the permission is not granted by the user
                    showRationalDialogforPermission()
                }
            }).onSameThread().check()
    }



    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}