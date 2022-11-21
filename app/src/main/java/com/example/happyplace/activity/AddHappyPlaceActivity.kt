package com.example.happyplace.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.happyplace.R
import com.example.happyplace.database.DatabaseHandler

import com.example.happyplace.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplace.model.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityAddHappyPlaceBinding? = null

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlaceImages"
    }

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    private var saveImageIntoIntenalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private var mHappyPlaceDetail: HappyPlaceModel? = null

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

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            mHappyPlaceDetail = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel?
        }
        
        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDateInView()
        }
        updateDateInView()

        if (mHappyPlaceDetail != null){
            supportActionBar?.title = "Edit Happy Place"

            binding?.titleET?.setText(mHappyPlaceDetail!!.title)
            binding?.etDescription?.setText(mHappyPlaceDetail!!.description)
            binding?.etDate?.setText(mHappyPlaceDetail!!.date)
            binding?.etLocation?.setText(mHappyPlaceDetail!!.location)
            mLongitude = mHappyPlaceDetail!!.longitude
            mLatitude = mHappyPlaceDetail!!.latitude

            saveImageIntoIntenalStorage = Uri.parse(mHappyPlaceDetail!!.image)
            binding?.ivPlaceImage?.setImageURI(saveImageIntoIntenalStorage)

            binding?.btnSave?.text = "UPDATE"
        }

        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)
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
                // Create a variable to hold the AlertDialog Builder
                val pictureDialog = AlertDialog.Builder(this)
                //set the title for the builder
                pictureDialog.setTitle("Select Action")
                //Create a variable of array item for the alert dialog items
                val pictureDialogItems = arrayOf("Select Photo from Gallery", "Select Photo from Camera")
                //Set the items for the dialog and give function for each items when it is clicked
                pictureDialog.setItems(pictureDialogItems){
                    _, which ->
                    when(which){
                        0 -> choosePhotoFromGallery()

                        1 -> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }

            R.id.btn_save -> {
                when{
                    binding?.titleET?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter Title", Toast.LENGTH_LONG).show()
                    }
                    binding?.etDescription?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a Description", Toast.LENGTH_LONG).show()
                    }

                    binding?.etLocation?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a Location", Toast.LENGTH_LONG).show()
                    }

                    saveImageIntoIntenalStorage == null -> {
                        Toast.makeText(this, "Please select an Image", Toast.LENGTH_LONG).show()
                    } else -> {
                        val happyPlaceModel = HappyPlaceModel(
                            if (mHappyPlaceDetail == null) 0 else mHappyPlaceDetail!!.id, // it will check whether the update or save function will work
                            binding?.titleET?.text.toString(),
                            saveImageIntoIntenalStorage.toString(),
                            binding?.etDescription?.text.toString(),
                            binding?.etDate?.text.toString(),
                            binding?.etLocation?.text.toString(),
                            mLatitude,
                            mLongitude
                        )
                        val dbHandler = DatabaseHandler(this)

                        if (mHappyPlaceDetail == null){
                            val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)

                            if (addHappyPlace > 0){
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        } else {
                            val updateHappyPlace = dbHandler.updateHappyPlace(happyPlaceModel)

                            if (updateHappyPlace > 0){
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }


                    }
                }
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

    private fun takePhotoFromCamera(){
        Dexter.withContext(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {
                override  fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()){ //If all the permission is granted by user
                        val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(galleryIntent, CAMERA)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                    //If the permission is not granted by the user
                    showRationalDialogforPermission()
                }
            }).onSameThread().check()
    }

    private fun choosePhotoFromGallery(){
        Dexter.withContext(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override  fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()){ //If all the permission is granted by user
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, GALLERY)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                    //If the permission is not granted by the user
                    showRationalDialogforPermission()
                }
            }).onSameThread().check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            if (requestCode == GALLERY) {
                if (data != null){
                    val contentUri = data.data
                    try {
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
                        // use saveImageIntoIntenalStorage function to store the selected image
                         saveImageIntoIntenalStorage = saveImageIntoIntenalStorage(selectedImageBitmap)
                        //Log.i("Image Loc: ", "Path: $saveImageIntoIntenalStorage")
                        binding?.ivPlaceImage?.setImageBitmap(selectedImageBitmap)
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this, "There is an error to select the image from gallery", Toast.LENGTH_LONG).show()
                    }
                }
            } else if (requestCode == CAMERA){
                        val cameraImage: Bitmap = data!!.extras!!.get("data") as Bitmap
                        //use saveImageIntoIntenalStorage function to store the captured image
                        saveImageIntoIntenalStorage = saveImageIntoIntenalStorage(cameraImage)
                        //Log.i("Image Loc: ", "Path: $saveImageIntoIntenalStorage")
                        binding?.ivPlaceImage?.setImageBitmap(cameraImage)
            }
        }
    }

    //Create a function to save the image into internal storage with bitmap as a argument and it return a Uri
    private fun saveImageIntoIntenalStorage(bitmap: Bitmap): Uri {
        //create a wrapper variable for ContextWrapper with application Context
        val wrapper = ContextWrapper(applicationContext)
        //Create a variable for file with the wrapper.getDir method useing directory and also private mode so that no other app use that image
        var file = wrapper.getDir("IMAGE_DIRECTORY", Context.MODE_PRIVATE)
        //create that file with File method which has above file variable and a UUID as a argument
        file = File(file, "${UUID.randomUUID()}.jpg")

        //To store the file now we use File Output Stream with help of try and catch block
        try {
            //create a OutputStream useing FileOutputStrame and takeing above file as a argument
            var stream: OutputStream = FileOutputStream(file)
            //Now compress the bitmap with Bitmap compress format, quality and stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            //flush the stream and than close the stream
            stream.flush()
            stream.close()


        }catch (e:IOException){
            e.printStackTrace()
        }
        //Return a Uri with parse and absolute path
        return Uri.parse(file.absolutePath)

    }



    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}