package `in`.co.cfcs.digicard.MainActivity

import `in`.co.cfcs.digicard.Fragment.CardFragment
import `in`.co.cfcs.digicard.Fragment.Profile
import `in`.co.cfcs.digicard.R
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants
import kotlinx.android.synthetic.main.activity_main.*
import android.provider.MediaStore
import android.R.attr.data
import java.io.File
import android.content.Context
import android.graphics.Path
import android.media.Image
import android.util.Log
import androidx.exifinterface.media.ExifInterface





class MainActivity : AppCompatActivity(), CardFragment.OnFragmentInteractionListener, Profile.OnFragmentInteractionListener  {


//    private val REQUEST_CAMERA = 1
    private val REQUEST_WRITE_PERMISSION = 20
//    private var imageUri: Uri? = null
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100

    private val REQUEST_CODE = 99
    private var scanButton: Button? = null
    private var cameraButton: Button? = null
    private var mediaButton: Button? = null
    private var scannedImageView: ImageView? = null

    private var filePath: String = ""


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {

                val textFragment = CardFragment()

                // Get the support fragment manager instance
                val manager = supportFragmentManager

                // Begin the fragment transition using support fragment manager
                val transaction = manager.beginTransaction()

                // Replace the fragment on container
                transaction.replace(R.id.frame_main_container,textFragment)
                transaction.addToBackStack(null)

                // Finishing the transition
                transaction.commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                        REQUEST_WRITE_PERMISSION
                    )
                }else{
                    var preference: Int = 4
                    startScan(preference)
                }


                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {

                val textFragment = Profile()

                // Get the support fragment manager instance
                val manager = supportFragmentManager

                // Begin the fragment transition using support fragment manager
                val transaction = manager.beginTransaction()

                // Replace the fragment on container
                transaction.replace(R.id.frame_main_container,textFragment)
                transaction.addToBackStack(null)

                // Finishing the transition
                transaction.commit()

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

      //  val farmlayout_container: FrameLayout = findViewById(R.id.frame_main_container)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        val textFragment = CardFragment()

        // Get the support fragment manager instance
        val manager = supportFragmentManager

        // Begin the fragment transition using support fragment manager
        val transaction = manager.beginTransaction()

        // Replace the fragment on container
        transaction.replace(R.id.frame_main_container,textFragment)
        transaction.addToBackStack(null)

        // Finishing the transition
        transaction.commit()

    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: kotlin.Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            REQUEST_WRITE_PERMISSION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                val filename = System.currentTimeMillis().toString() + ".jpg"
//
//                val values = ContentValues()
//                values.put(MediaStore.Images.Media.TITLE, filename)
//                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//
//
//                val intent = Intent()
//                intent.action = MediaStore.ACTION_IMAGE_CAPTURE
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
//                startActivityForResult(intent, REQUEST_CAMERA)

                var preference: Int = 4
                startScan(preference)


                // cardImg.setImageURI(imageUri);
            } else if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                  //  checkedCheckBox = "1"

                } else {
                    Toast.makeText(this@MainActivity, "Permission Denied!", Toast.LENGTH_SHORT).show()
                }


            }
        }
    }

    protected fun startScan(preference: Int) {
        val intent = Intent(this, ScanActivity::class.java)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data!!.extras!!.getParcelable<Uri>(ScanConstants.SCANNED_RESULT)
            val selectedpath = data.extras.getString("selectedPath")
            var bitmap: Bitmap? = null
            val photo: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

            val tempUri = getPath().getImageUri(getApplicationContext(), photo)
            val finalFile =  File(getPath().getRealPathFromURI(this@MainActivity,tempUri))


            var i = Intent(this, ScannedActivity::class.java)
            i.putExtra("imageUri", uri.toString())
            i.putExtra("selectedPath", selectedpath)
            startActivity(i)

        }
    }


    fun getCameraPhotoOrientation(context: Context, imageUri: Uri, imagePath: String): Int {
        var rotate = 0
        try {
            context.getContentResolver().notifyChange(imageUri, null)
            val imageFile = File(imagePath)

            val exif = ExifInterface(imageFile.absolutePath)
           // val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION)
            val orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
            val orientation =
                if (orientString != null) Integer.parseInt(orientString) else ExifInterface.ORIENTATION_NORMAL

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }

            Log.i("RotateImage", "Exif orientation: $orientation")
            Log.i("RotateImage", "Rotate value: $rotate")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return rotate
    }

    //    Bitmap photo = (Bitmap) data.getExtras().get("data");
//    imageView.setImageBitmap(photo);
//    knop.setVisibility(Button.VISIBLE);
//
//
//    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
//    Uri tempUri = getImageUri(getApplicationContext(), photo);
//
//    // CALL THIS METHOD TO GET THE ACTUAL PATH
//    File finalFile = new File(getRealPathFromURI(tempUri));
//
//    System.out.println(mImageCaptureUri);
//}
//}
//


}