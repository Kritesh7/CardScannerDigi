package `in`.co.cfcs.digicard.MainActivity

import `in`.co.cfcs.digicard.R
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.*
import androidx.core.content.FileProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.card_list_layout.*
import java.io.File
import java.util.*
import kotlin.Array as Array1
import kotlin.collections.ArrayList as ArrayList1


class ScannedActivity : AppCompatActivity() {

    var imageUri = ""
    var realPath = ""

    var bitmap: Bitmap? = null
    var path1: Uri? = null
    var filename = ""

    private lateinit var txt_website: EditText
    private lateinit var txt_name: EditText
    private lateinit var txt_address: EditText
    private lateinit var txt_email: EditText
    private lateinit var txt_company: EditText
    private  lateinit var txt_mobile: EditText
    private lateinit var name_chip_group: ChipGroup
    private lateinit var txt_degination: EditText

    val mobileArrayList = ArrayList<String>()
    val addressArrayList = ArrayList<String>()
    val nameArrayList = ArrayList<String>()
    val companyArrayList = ArrayList<String>()
    val designationArrayList = ArrayList<String>()

    lateinit var lines : kotlin.Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanned)

        FirebaseApp.initializeApp(this);

        val imageScanned: ImageView = findViewById(R.id.scanned_image)
        txt_name = findViewById(R.id.txt_name)
        txt_website = findViewById(R.id.txt_website)
        txt_address = findViewById(R.id.txt_address)
        txt_email = findViewById(R.id.txt_email)
        txt_company = findViewById(R.id.txt_company)
        txt_mobile = findViewById(R.id.txt_mobile)
        name_chip_group = findViewById(R.id.name_chip_group)
        txt_degination = findViewById(R.id.txt_degination)


        val intent = intent
        if (intent != null) {
            imageUri = intent.getStringExtra("imageUri")

            val myUri = Uri.parse(imageUri)

            val file =  File(imageUri)

            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, myUri)
              //  contentResolver.delete(myUri!!, null, null)
                imageScanned!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            runTextRecognition()
        }



      name_chip_group.setOnCheckedChangeListener(ChipGroup.OnCheckedChangeListener { name_chip_group, i ->

          Log.i("Kriteh", "ID: " + name_chip_group.getCheckedChipId())
          val checkedChip = name_chip_group?.findViewById(name_chip_group.checkedChipId) as? Chip
          checkedChip?.let {

              if (checkedChip.isChecked)
                 // Toast.makeText(applicationContext, "RadioGroup: ${name_chip_group?.contentDescription} RadioButton: ${checkedChip?.text}", Toast.LENGTH_LONG).show()
              txt_name.setText(checkedChip?.text)
          }

      })

    }


    private fun runTextRecognition() {
        val image = FirebaseVisionImage.fromBitmap(this.bitmap!!)
        val recognizer = FirebaseVision.getInstance()
            .onDeviceTextRecognizer
        recognizer.processImage(image)
            .addOnSuccessListener { texts ->

                processTextRecognitionResult(texts)
                nameSuggesstion()
//                if txt_company name Empty
                companyName()
            }
            .addOnFailureListener(
                object : OnFailureListener {
                    override fun onFailure(@NonNull e: Exception) {
                        // Task failed with an exception
                        e.printStackTrace()
                    }
                })
    }


    private fun processTextRecognitionResult(texts: FirebaseVisionText) {
        val blocks = texts.textBlocks
        if (blocks.size == 0) {
            return
        }



        val MlText = texts.text.toString()
         lines = MlText.split(regex = System.getProperty("line.separator").toRegex()).dropLastWhile({ it.isEmpty() })
            .toTypedArray()

        for (i in lines.indices) {
            val firstLine = lines[i]
            val pa = Pattern.compile("\\+?\\(?\\d{2,4}\\)?[\\d\\s-]{3,}")
            val ma = pa.matcher(firstLine)
            if (ma.find()) {

                if (!Pattern.matches("[a-zA-Z]+", firstLine)) {

                    val ch = firstLine.toCharArray()
                    var letter = 0
                    var num = 0
                    for (id in 0 until firstLine.length) {
                        if (Character.isLetter(ch[id])) {
                            letter++
                        } else if (Character.isDigit(ch[id])) {
                            num++
                        }
                    }
                    if (num >= 10) {

                        if (num > letter) {
                            ExtractMobileNumber(firstLine)
                        } else {
                            ExtractAddress(firstLine)
                        }

                    } else if (num >= 1 && num <= 12 && letter >= 5) {
                        if (!lines[i - 1].contains("mobile") || !lines[i - 1].contains("Phone")
                            || !lines[i - 1].contains("Phone No.") || !lines[i - 1].contains("Mobile")
                        ) {
                            ExtractAddress(firstLine)
                        }
                    }else {

                        Toast.makeText(this@ScannedActivity,firstLine,Toast.LENGTH_LONG).show()
                    }

                }else {

                    Toast.makeText(this@ScannedActivity,firstLine,Toast.LENGTH_LONG).show()
                }

            }else {

                Toast.makeText(this@ScannedActivity,firstLine,Toast.LENGTH_LONG).show()
            }


            val url = Patterns.WEB_URL.matcher(firstLine)
            while (url.find()) {
                txt_website.setText(url.group())

            }

            val emailAddress = Patterns.EMAIL_ADDRESS.matcher(firstLine)
            while (emailAddress.find()) {

                txt_email.setText(emailAddress.group())
            }

            var count = 0
          //  val pattern = Pattern.compile("^(([A-za-z]+[\\s]{1}[A-za-z]+)|([A-Za-z]+))\$")
            val pattern = Pattern.compile("\\s")
            val matcher = pattern.matcher(firstLine)
            val found = matcher.find()
            if (found) {
                if (Pattern.matches(".*[a-zA-Z]+.*[a-zA-Z]", firstLine)) {
                    if (!CommunityCheck(firstLine)) {
                        if (count == 0) {

                            val ch1 = firstLine.toCharArray()
                            var letter1 = 0
                            var num1 = 0
                            for (id in 0 until firstLine.length) {
                                if (Character.isLetter(ch1[id])) {
                                    letter1++
                                } else if (Character.isDigit(ch1[id])) {
                                    num1++
                                }
                            }
                            if (letter1 <= 20 && num1 == 0) {
                                val pa = Pattern.compile("([a-zA-Z]*(\\s)*[.,]*)*")
                                val ma = pa.matcher(firstLine)
                                if (ma.find()) {

                                   // txt_name.setText(firstLine)
                                    ExtractName(firstLine)
                                    count++
                                }

                            }else if (DesignationCheck(firstLine)){
                                ExtractCompanyName(firstLine)
                            }else if(CommunityCheck(firstLine)){
                                ExtractCompanyName(firstLine)
                            }


//                            val p = Pattern.compile(".*\\d+.*")
//                            var m: Matcher? = null
//                            try {
//                                m = p.matcher(lines[i + 1])
//                                val b = m!!.find()
//                                if (!b) {
//                                    if (Pattern.matches(".*[a-zA-Z]+.*[a-zA-Z]", firstLine)) {
//
//                                      txt_company.setText(lines[i + 1])
//                                    }
//                                }
//                            } catch (e: ArrayIndexOutOfBoundsException) {
//                                e.printStackTrace()
//                            }

                        }
                    }else{

                        ExtractCompanyName(firstLine)
                    }
                }else if (CommunityCheck(firstLine)){
                    if (!Pattern.matches(".*\\d.*", firstLine)){
                        ExtractCompanyName(firstLine)
                    }

                }else if (DesignationCheck(firstLine)){
                    ExtractCompanyName(firstLine)
                }
            }

        }

    }

    private fun CommunityCheck(firstLine: String): Boolean {

        if (firstLine.contains("PVT.") || firstLine.contains("LTD.") || firstLine.contains("LTD") ||
            firstLine.contains("S.r.l.") || firstLine.contains("eurl") || firstLine.contains("GbR") ||
            firstLine.contains("GmbH") || firstLine.contains("inc") || firstLine.contains("LLC") ||
            firstLine.contains("LLP") || firstLine.contains("Ltd") || firstLine.contains("Srl") ||
            firstLine.contains("Corp") || firstLine.contains("Ltd.") || firstLine.contains("Inc.") ||
            firstLine.contains("Co,LTD.") || firstLine.contains("Pvt.") || firstLine.contains("PRIVATE") || firstLine.contains("LIMITED"))
        {
            return true
        }

        return false
    }

    private fun DesignationCheck(firstLine: String): Boolean {

        if (firstLine.contains("Chief Executive Officer") || firstLine.contains("Chief Operating Officer") || firstLine.contains("Chief Financial Officer") ||
            firstLine.contains("Chief Technology Officer") || firstLine.contains("Chief Marketing Officer") || firstLine.contains("Chief Legal Officer") ||
            firstLine.contains("Accounts Manager") || firstLine.contains("Recruitment Manager") || firstLine.contains("Technology Manager") ||
            firstLine.contains("Store Manager") || firstLine.contains("Regional Manager") || firstLine.contains("Functional Manager") ||
            firstLine.contains("Departmental Managers") || firstLine.contains("General Managers") || firstLine.contains("CEO") ||
            firstLine.contains("COO") || firstLine.contains("Departmental Heads") || firstLine.contains("Director") || firstLine.contains("Chairman and Managing Director") || firstLine.contains("CMD")
            || firstLine.contains("Departmental Deputy Heads or General Managers") || firstLine.contains("Manager") || firstLine.contains("Sub-ordinates") || firstLine.contains("Team Manager")
            || firstLine.contains("Sales") || firstLine.contains("Engineer") ||  firstLine.contains("MANAGER") ||  firstLine.contains("SALE") || firstLine.contains("ACCOUNTS")
            || firstLine.contains("SENIOR") || firstLine.contains("Senior") || firstLine.contains("Junior") || firstLine.contains("JUNIOR") || firstLine.contains("President")
            || firstLine.contains("President") || firstLine.contains("Head") || firstLine.contains("VP"))
        {
            return true
        }

        return false
    }

//    Extract Names From String
    private fun ExtractName(firstLine: String){

    for (item in lines) {
        // body of loop
        val emailAddress = Patterns.EMAIL_ADDRESS.matcher(item)
        while (emailAddress.find()) {

            val emailCheck = emailAddress.group()

              val split = emailCheck.split("@")
              val secondEmailString = split[1]
            val FirstEmailString = split[0]
             val index = secondEmailString.indexOf('.')
              val email = secondEmailString.substring(0,index)

            if(email.compareTo(firstLine) != 0){
                var firstNameString = ""
                if(firstLine.contains(" ") ){
                    val splitName = firstLine.split(" ")
                    firstNameString = splitName[0]
                }


                if(FirstEmailString.contains(".")){

                  val str = FirstEmailString.replace("."," ")
                    if(str.compareTo(firstLine.toLowerCase()) == 0){
                        txt_name.setText(firstLine)
                    }else if(DesignationCheck(firstLine)){
                        designationArrayList.add(firstLine)
                        txt_degination.setText(designationArrayList.toString())
                    }
                }else if(FirstEmailString.contains("_")){
                    val str = FirstEmailString.replace("_"," ")
                    if(str.compareTo(firstLine.toLowerCase()) == 0){
                        txt_name.setText(firstLine)
                    }else if(DesignationCheck(firstLine)){
                        designationArrayList.add(firstLine)
                        txt_degination.setText(designationArrayList.toString())
                    }
                }else if (FirstEmailString.compareTo(firstNameString.toLowerCase()) == 0){

                        txt_name.setText(firstLine)

                }else if(DesignationCheck(firstLine)){
                    designationArrayList.add(firstLine)
                    txt_degination.setText(designationArrayList.toString())
                }
            }

            // txt_email.setText(emailAddress.group())
        }
    }
    nameArrayList.add(firstLine)
   // txt_name.setText(nameArrayList.toString())

    print(nameArrayList.toString())

}

    //Extract Address From String
    private fun ExtractAddress(firstLine: String) {

        addressArrayList.add(firstLine)
        txt_address.setText(addressArrayList.toString())

        //   Toast.makeText(this@ScannedActivity,addressArrayList.toString(),Toast.LENGTH_LONG).show()

    }

    // Extract Mobile No From String
    private fun ExtractMobileNumber(firstLine: String) {


        val splitString = firstLine.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val regex = ("(?<=Tel[:\\s])([+\\d\\s]+\\S)(?=\\s\\D)|" // this captures the tel number

                + "(?<=Fax[:\\s])([+\\d\\s]+\\S)(?=\\s\\D)|" // this captures the fax number

                + "(?<=\\s)(\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b)") // this captures the email string


        // Remember the CASE_INSENSITIVE option
        val re = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)

        val m = re.matcher(firstLine)
        while (m.find()) {
            val splitString2 = m.group(0).split(".".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (splitString2.size > 0) {

                mobileArrayList.add(splitString[0])
            }
        }


        if (splitString.size > 1) {

            for (i in splitString.indices) {
                var str = splitString[i].replace(".*[a-zA-Z]+.*[a-zA-Z].*[:]".toRegex(), "")
                str = str.replace(".*[a-zA-Z].*[.]".toRegex(), "")
                str = str.replace("[A-Za-z]".toRegex(), "")
                str = str.replace("[:]".toRegex(), "")
                str = str.replace("[()]".toRegex(), "")
                str = str.replace("[.]".toRegex(), "")
                str = str.replace("[,]".toRegex(), "")
                mobileArrayList.add(str)
            }


        } else {
            var str = firstLine.replace(".*[a-zA-Z]+.*[a-zA-Z].*[:]".toRegex(), "")
            str = str.replace(".*[a-zA-Z].*[.]".toRegex(), "")
            str = str.replace("[A-Za-z]".toRegex(), "")
            str = str.replace("[:]".toRegex(), "")
            str = str.replace("[()]".toRegex(), "")
            str = str.replace("[.]".toRegex(), "")
            str = str.replace("[,]".toRegex(), "")
            mobileArrayList.add(str)

        }

        txt_mobile.setText(mobileArrayList.toString())

//       Toast.makeText(this@ScannedActivity,mobileArrayList.toString(),Toast.LENGTH_LONG).show()

    }

    private fun nameSuggesstion(){
        val nameCheck = txt_name.getText().toString()
        if(nameArrayList.size > 0){
            for (index in nameArrayList.indices) {
                val chip = Chip(name_chip_group.context)
                val chipValue = nameArrayList.get(index)
                chip.text= chipValue

                // necessary to get single selection working
                chip.isClickable = true
                chip.isCheckable = true
                if (chipValue.compareTo(nameCheck) == 0){
                    chip.isSelected = true
                }
                name_chip_group.addView(chip)
            }
        }
    }

    private fun ExtractCompanyName(firstLine: String){

        if(DesignationCheck(firstLine)){
            designationArrayList.add(firstLine)
            txt_degination.setText(designationArrayList.toString())
        }else{
            companyArrayList.add(firstLine)
            txt_company.setText( companyArrayList.toString())
        }

    }

    private fun companyName(){

        val comName = txt_company.getText().toString()
        val emailtxt = txt_email.getText().toString()

        if(comName.isEmpty()){
            if (emailtxt.isNotEmpty()){
                val emailAddress = Patterns.EMAIL_ADDRESS.matcher(emailtxt)
                while (emailAddress.find()) {

                    val emailCheck = emailAddress.group()

                    val split = emailCheck.split("@")
                    val secondEmailString = split[1]
                    val index = secondEmailString.indexOf('.')
                    val email = secondEmailString.substring(0,index)

                    txt_company.setText(email)
                }
            }
        }

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
//public Uri getImageUri(Context inContext, Bitmap inImage) {
//    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//    String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//    return Uri.parse(path);
//}
//
//public String getRealPathFromURI(Uri uri) {
//    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//    cursor.moveToFirst();
//    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//    return cursor.getString(idx);
//}

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }



}
