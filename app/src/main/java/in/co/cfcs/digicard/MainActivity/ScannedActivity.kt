package `in`.co.cfcs.digicard.MainActivity

import `in`.co.cfcs.digicard.Adapter.DemoCustomeAdapter
import `in`.co.cfcs.digicard.Alogo.ExtractDataCheck.Companion.AddressCheck
import `in`.co.cfcs.digicard.Alogo.ExtractDataCheck.Companion.CommunityCheck
import `in`.co.cfcs.digicard.Alogo.ExtractDataCheck.Companion.DesignationCheck
import `in`.co.cfcs.digicard.Helper.*
import `in`.co.cfcs.digicard.Interface.*
import `in`.co.cfcs.digicard.Model.ContactTypeListModel
import `in`.co.cfcs.digicard.Model.CustomerDetailsModel
import `in`.co.cfcs.digicard.Model.ManagmentTypeListModel
import `in`.co.cfcs.digicard.R
import android.app.ProgressDialog
import android.graphics.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.io.IOException
import java.util.regex.Pattern
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.exifinterface.media.ExifInterface
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.scanlibrary.Utils
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.Array as Array1
import kotlin.collections.ArrayList as ArrayList1


class ScannedActivity : AppCompatActivity(), CustomerNameInterface, BusinessVerticalInterface, IndustrySegmentInterface,
    IndustryTypeInterface, PrincipleTypeInterface {

    var getDdlListUrl = SettingConstant.BASEURL_FOR_LOGIN + "DigiCardScannerService.asmx/AppddlList"

    private var imageUri = ""
    var bitmap: Bitmap? = null
    lateinit var original: Bitmap

    private lateinit var txt_website: EditText
    private lateinit var txt_name: EditText
    private lateinit var txt_address: EditText
    private lateinit var txt_company: EditText
    private lateinit var name_chip_group: ChipGroup
    private lateinit var txt_degination: EditText

    private lateinit var ll_tel_numbers: LinearLayout
    private lateinit var ll_header_mobile: LinearLayout
    private lateinit var ll_header_email: LinearLayout
    private lateinit var ll_emails: LinearLayout
    private lateinit var spinner_contact_type: Spinner
    private lateinit var spinner_management_type: Spinner
    private lateinit var btn_business_vertical: ImageView
    private lateinit var Chip_group_business_vertical: ChipGroup

    lateinit var searchData: EditText

    val mobileArrayList = ArrayList<String>()
    val addressArrayList = ArrayList<String>()
    val nameArrayList = ArrayList<String>()
    val companyArrayList = ArrayList<String>()
    val designationArrayList = ArrayList<String>()
    val emailArrayList = ArrayList<String>()

    lateinit var lines: kotlin.Array<String>
    lateinit var selectedPath: String
    lateinit var exifObject: ExifInterface


    lateinit var mobileAraayListID: ArrayList<String>
    lateinit var mobileAraayListName: ArrayList<String>
    lateinit var emailAraayListID: ArrayList<String>
    lateinit var emailAraayListName: ArrayList<String>
    lateinit var emailEditList: ArrayList<EditText>


    var contactTypeList = ArrayList<ContactTypeListModel>()
    var managementTypeList = ArrayList<ManagmentTypeListModel>()
    var businessVerticalList = ArrayList<CustomerDetailsModel>()


    var bussinessverticalidList = ArrayList<String>()
    var bussinessverticalnameList = ArrayList<String>()
    var industrySegmentIdList = ArrayList<String>()
    var industrySegmentNameList = ArrayList<String>()
    var industryTypeIdList = ArrayList<String>()
    var industryTypeNameList = ArrayList<String>()
    var principleTypeIdList = ArrayList<String>()
    var principleTypeNameList = ArrayList<String>()

    var businessVerticalCheckID = ArrayList<String>()
    var businessVerticalCheckName = ArrayList<String>()

    lateinit var popupWindow: PopupWindow
    var businessVerticalTypeString = ""
    lateinit var serchListData: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanned)

        FirebaseApp.initializeApp(this)


        val imageScanned: ImageView = findViewById(R.id.scanned_image)
        txt_name = findViewById(R.id.txt_name)
        txt_website = findViewById(R.id.txt_website)
        txt_address = findViewById(R.id.txt_address)
        txt_company = findViewById(R.id.txt_company)
        name_chip_group = findViewById(R.id.name_chip_group)
        txt_degination = findViewById(R.id.txt_degination)
        ll_tel_numbers = findViewById(R.id.ll_tel_numbers)
        ll_header_mobile = findViewById(R.id.ll_header_mobile)
        ll_header_email = findViewById(R.id.ll_header_email)
        ll_emails = findViewById(R.id.ll_emails)
        spinner_contact_type = findViewById(R.id.spinner_contact_type)
        spinner_management_type = findViewById(R.id.spinner_management_type)
        btn_business_vertical = findViewById(R.id.btn_business_vertical)
        Chip_group_business_vertical = findViewById(R.id.Chip_group_business_vertical)

        mobileAraayListID = arrayListOf("0", "1", "2", "3", "4", "5", "6", "7")
        mobileAraayListName =
            arrayListOf("Home", "Mobile", "Work", "Work Fax", "Home Fax", "Other Fax", "Pager", "Other")

        emailAraayListID = arrayListOf("0", "1", "2")
        emailAraayListName = arrayListOf("Personal", "Work", "Other")


        val intent = intent
        if (intent != null) {
            imageUri = intent.getStringExtra("imageUri")

            selectedPath = intent.getStringExtra("selectedPath")
            val myUri = Uri.parse(imageUri)

            val bitmapcheck = getBitmapcheck()

            imageScanned.setImageBitmap(bitmapcheck)

            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, myUri)
                //  contentResolver.delete(myUri!!, null, null)
                //  imageScanned!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            runTextRecognition()
        }


        name_chip_group.setOnCheckedChangeListener(ChipGroup.OnCheckedChangeListener { name_chip_group, i ->

            val checkedChip = name_chip_group?.findViewById(name_chip_group.checkedChipId) as? Chip
            checkedChip?.let {

                if (checkedChip.isChecked)
                    txt_name.setText(checkedChip.text)
            }

        })


        val conn = ConnectionDetector(this@ScannedActivity)

        val userId = UtilsMethods.getBlankIfStringNull(SharedPrefs.getUserId(this@ScannedActivity).toString())
        val authCode = UtilsMethods.getBlankIfStringNull(SharedPrefs.getAuthCode(this@ScannedActivity).toString())
        val zoneIdShared = UtilsMethods.getBlankIfStringNull(SharedPrefs.getZoneId(this@ScannedActivity).toString())


        if (conn.getConnectivityStatus() > 0) {

            getAppDdlList(authCode, userId)
        } else {
            conn.showNoInternetAlret()
        }


        //contactType Spinner
        contactTypeList.add(ContactTypeListModel("", "Please select contact Type"))
        val contactTypeAdapter = ArrayAdapter(
            this@ScannedActivity, android.R.layout.simple_spinner_item, contactTypeList
        )
        contactTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_contact_type.setAdapter(contactTypeAdapter)

        //Mangement Type Spinner
        managementTypeList.add(ManagmentTypeListModel("", "Please Select Management Type"))
        val managementTypeAdapter = ArrayAdapter(
            this@ScannedActivity, android.R.layout.simple_spinner_item,
            managementTypeList
        )
        managementTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_management_type.setAdapter(managementTypeAdapter)


        btn_business_vertical.setOnClickListener(View.OnClickListener {

            callPopup(businessVerticalList, "1")
        })


    }


    override fun getCustomerName(name: String) {

    }

    override fun getCustomerId(cusId: String) {

    }

    override fun getBusinessVerticalId(cusId: String, name: String) {
        if (bussinessverticalidList.size > 0) {
            for (item in bussinessverticalidList.indices) {
                val getId = bussinessverticalidList.get(item)
                if (cusId.compareTo(getId) == 0) {
                    bussinessverticalidList.remove(cusId)
                    bussinessverticalnameList.remove(name)
                } else {
                    bussinessverticalidList.add(cusId)
                    bussinessverticalnameList.add(name)
                }
            }
        } else {
            bussinessverticalidList.add(cusId)
            bussinessverticalnameList.add(name)
        }


    }

    override fun getIndustrySegmentId(cusId: String, name: String) {

        industrySegmentIdList.add(cusId)
        industrySegmentNameList.add(name)
    }

    override fun getIndustryTypeId(cusId: String, name: String) {
        industryTypeIdList.add(cusId)
        industryTypeNameList.add(name)
    }

    override fun getPrincipleTypeId(cusId: String, name: String) {
        principleTypeIdList.add(cusId)
        principleTypeNameList.add(name)
    }

    private fun getBitmapcheck(): Bitmap? {
        val uri = getUri()
        try {
            original = Utils.getBitmap(getApplicationContext(), uri)
            val width = original.width
            val height = original.height
            if (height > width) {
                try {
                    exifObject = ExifInterface(selectedPath)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val orientation = exifObject.getAttributeInt(
                    android.media.ExifInterface.TAG_ORIENTATION,
                    android.media.ExifInterface.ORIENTATION_UNDEFINED
                )
                val imageRotate = rotateBitmap(original, orientation)
                // capturedPhoto.setImageBitmap(imageRotate);
                original = imageRotate!!
            }
            getContentResolver().delete(uri!!, null, null)
            return original
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun getUri(): Uri? {
        val uri = Uri.parse(imageUri)
        return uri
    }

    fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
        val matrix = Matrix()
        when (orientation) {
            android.media.ExifInterface.ORIENTATION_NORMAL -> return bitmap
            android.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            android.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(90f)
            android.media.ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            android.media.ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            android.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            android.media.ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            android.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            else -> return bitmap
        }
        try {
            //  bitmap.recycle()
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return null
        }

    }

    private fun runTextRecognition() {
        val image = FirebaseVisionImage.fromBitmap(this.original)
        val recognizer = FirebaseVision.getInstance()
            .onDeviceTextRecognizer
        recognizer.processImage(image)
            .addOnSuccessListener { texts ->

                processTextRecognitionResult(texts)
                nameSuggesstion()

                fillAllDataMobile()
                fillAllDataEmail()
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
                    } else {

                        //   Toast.makeText(this@ScannedActivity,firstLine,Toast.LENGTH_LONG).show()
                    }

                } else {

                    //     Toast.makeText(this@ScannedActivity,firstLine,Toast.LENGTH_LONG).show()
                }

            } else {

                if (AddressCheck(firstLine)) {

                    ExtractAddress(firstLine)
                }

                //  Toast.makeText(this@ScannedActivity,firstLine,Toast.LENGTH_LONG).show()
            }


            val url = Patterns.WEB_URL.matcher(firstLine)
            while (url.find()) {
                txt_website.setText(url.group())

            }

            val emailAddress = Patterns.EMAIL_ADDRESS.matcher(firstLine)
            while (emailAddress.find()) {

                emailArrayList.add(emailAddress.group())

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

                            } else if (DesignationCheck(firstLine)) {
                                ExtractCompanyName(firstLine)
                            } else if (CommunityCheck(firstLine)) {
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
                    } else {

                        ExtractCompanyName(firstLine)
                    }
                } else if (CommunityCheck(firstLine)) {
                    if (!Pattern.matches(".*\\d.*", firstLine)) {
                        ExtractCompanyName(firstLine)
                    }

                } else if (DesignationCheck(firstLine)) {
                    ExtractCompanyName(firstLine)
                }
            }

        }

    }

    //    Extract Names From String
    private fun ExtractName(firstLine: String) {

        for (item in lines) {
            // body of loop
            val emailAddress = Patterns.EMAIL_ADDRESS.matcher(item)
            while (emailAddress.find()) {

                val emailCheck = emailAddress.group()

                val split = emailCheck.split("@")
                val secondEmailString = split[1]
                val FirstEmailString = split[0]
                val index = secondEmailString.indexOf('.')
                val email = secondEmailString.substring(0, index)

                if (email.compareTo(firstLine) != 0) {
                    var firstNameString = ""
                    if (firstLine.contains(" ")) {
                        val splitName = firstLine.split(" ")
                        firstNameString = splitName[0]
                    }


                    if (FirstEmailString.contains(".")) {

                        val str = FirstEmailString.replace(".", " ")
                        if (str.compareTo(firstLine.toLowerCase()) == 0) {
                            txt_name.setText(firstLine)
                        } else if (DesignationCheck(firstLine)) {
                            designationArrayList.add(firstLine)
                            txt_degination.setText(designationArrayList.toString())
                        }
                    } else if (FirstEmailString.contains("_")) {
                        val str = FirstEmailString.replace("_", " ")
                        if (str.compareTo(firstLine.toLowerCase()) == 0) {
                            txt_name.setText(firstLine)
                        } else if (DesignationCheck(firstLine)) {
                            designationArrayList.add(firstLine)
                            txt_degination.setText(designationArrayList.toString())
                        }
                    } else if (FirstEmailString.compareTo(firstNameString.toLowerCase()) == 0) {

                        txt_name.setText(firstLine)

                    } else if (DesignationCheck(firstLine)) {
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

        //      txt_mobile.setText(mobileArrayList.toString())

//       Toast.makeText(this@ScannedActivity,mobileArrayList.toString(),Toast.LENGTH_LONG).show()

    }

    private fun nameSuggesstion() {
        val nameCheck = txt_name.getText().toString()
        if (nameArrayList.size > 0) {
            for (index in nameArrayList.indices) {
                val chip = Chip(name_chip_group.context)
                val chipValue = nameArrayList.get(index)
                chip.text = chipValue

                // necessary to get single selection working
                chip.isClickable = true
                chip.isCheckable = true
                if (chipValue.compareTo(nameCheck) == 0) {
                    chip.isSelected = true
                }
                name_chip_group.addView(chip)
            }
        }
    }

    private fun ExtractCompanyName(firstLine: String) {

        if (DesignationCheck(firstLine)) {
            designationArrayList.add(firstLine)
            txt_degination.setText(designationArrayList.toString())
        } else {
            companyArrayList.add(firstLine)
            txt_company.setText(companyArrayList.toString())
        }

    }

    private fun companyName() {


        if (emailEditList.isNotEmpty()) {

            val comName = txt_company.getText().toString()
            val edtQytArray = arrayOfNulls<String>(emailEditList.size)

            if (emailEditList != null) {
                if (emailEditList.size > 0) {

                    for (i in emailEditList.indices) {
                        edtQytArray[i] = emailEditList.get(i).getText().toString()
                        if (comName.isEmpty()) {
                            if (edtQytArray[i]?.isNotEmpty()!!) {
                                val emailAddress = Patterns.EMAIL_ADDRESS.matcher(edtQytArray[i])
                                while (emailAddress.find()) {

                                    val emailCheck = emailAddress.group()

                                    val split = emailCheck.split("@")
                                    val secondEmailString = split[1]
                                    val index = secondEmailString.indexOf('.')
                                    val email = secondEmailString.substring(0, index)

                                    txt_company.setText(email)
                                }
                            }
                        }
                    }
                }
            }

        }

    }

    private fun fillAllDataMobile() {


        val linearHeaderparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )
        linearHeaderparams.setMargins(0, 0, 0, 0)

        val Textparams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 8f
        )
        Textparams.gravity = Gravity.CENTER
        Textparams.setMargins(0, 5, 0, 0)

        val Addparams = LinearLayout.LayoutParams(
            0, 50, 2f
        )
        Addparams.gravity = Gravity.CENTER
        Addparams.setMargins(0, 5, 0, 0)

        val linearLayoutHorizontalHeader = LinearLayout(this@ScannedActivity)
        linearLayoutHorizontalHeader.orientation = LinearLayout.HORIZONTAL
        linearLayoutHorizontalHeader.layoutParams = linearHeaderparams

        val TextView = TextView(this@ScannedActivity)
        TextView.layoutParams = Textparams
        TextView.setTextSize(12.0F)
        TextView.setText("Tel")
        TextView.setPadding(0, 0, 0, 0)
        TextView.setTextColor(Color.parseColor("#008577"))
        TextView.setTypeface(null, Typeface.BOLD)

        val AdddButton = ImageView(this@ScannedActivity)
        AdddButton.layoutParams = Addparams
        AdddButton.setImageResource(R.drawable.ic_add_circle)
        AdddButton.setPadding(80, 0, 0, 0)

        linearLayoutHorizontalHeader.addView(TextView)
        linearLayoutHorizontalHeader.addView(AdddButton)
        ll_header_mobile.addView(linearLayoutHorizontalHeader)

        AdddButton.setOnClickListener(View.OnClickListener {

            AddBlankMobileFiled("")

        })


        if (mobileArrayList.size > 0) {

            for (mobile in mobileArrayList) {

                AddBlankMobileFiled(mobile)

            }

        }


    }

    private fun fillAllDataEmail() {

        val linearHeaderparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )
        linearHeaderparams.setMargins(0, 0, 0, 0)

        val Textparams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 8f
        )
        Textparams.gravity = Gravity.CENTER
        Textparams.setMargins(0, 5, 0, 0)

        val Addparams = LinearLayout.LayoutParams(
            0, 50, 2f
        )
        Addparams.gravity = Gravity.CENTER
        Addparams.setMargins(0, 5, 0, 0)

        val linearLayoutHorizontalHeader = LinearLayout(this@ScannedActivity)
        linearLayoutHorizontalHeader.orientation = LinearLayout.HORIZONTAL
        linearLayoutHorizontalHeader.layoutParams = linearHeaderparams

        val TextView = TextView(this@ScannedActivity)
        TextView.layoutParams = Textparams
        TextView.setTextSize(12.0F)
        TextView.setText("Email")
        TextView.setPadding(0, 0, 0, 0)
        TextView.setTextColor(Color.parseColor("#008577"))
        TextView.setTypeface(null, Typeface.BOLD)

        val AdddButton = ImageView(this@ScannedActivity)
        AdddButton.layoutParams = Addparams
        AdddButton.setImageResource(R.drawable.ic_add_circle)
        AdddButton.setPadding(80, 0, 0, 0)

        linearLayoutHorizontalHeader.addView(TextView)
        linearLayoutHorizontalHeader.addView(AdddButton)
        ll_header_email.addView(linearLayoutHorizontalHeader)

        AdddButton.setOnClickListener(View.OnClickListener {

            AddBlankEmailFiled("")

        })


        if (emailArrayList.size > 0) {

            for (email in emailArrayList) {

                AddBlankEmailFiled(email)

            }

        }


    }

    private fun AddBlankMobileFiled(mobile: String) {

        val linearparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )
        linearparams.setMargins(0, 10, 0, 0)

        val Spinnerparams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f
        )
        Spinnerparams.gravity = Gravity.CENTER
        Spinnerparams.setMargins(0, 5, 0, 0)

        val Textparams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 6f
        )
        Textparams.gravity = Gravity.CENTER
        Textparams.setMargins(0, 5, 0, 0)

        val Deleteparams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )
        Deleteparams.gravity = Gravity.CENTER
        Deleteparams.setMargins(0, 5, 0, 0)


        val linearLayoutHorizontal = LinearLayout(this@ScannedActivity)
        linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
        linearLayoutHorizontal.layoutParams = linearparams


        val Spinner = Spinner(this@ScannedActivity)
        Spinner.layoutParams = Spinnerparams


        val spinneradapterMobile = ArrayAdapter<String>(
            this@ScannedActivity,
            android.R.layout.simple_spinner_item, mobileAraayListName
        )


        spinneradapterMobile.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        Spinner.setAdapter(spinneradapterMobile)

        if (!mobile.contains("-")) {
            Spinner.setSelection(1)
        }


        val EditName = EditText(this@ScannedActivity)
        EditName.layoutParams = Textparams
        EditName.setTextSize(12.0F)
        EditName.setText(mobile)
        EditName.setPadding(10, 5, 0, 10)
        EditName.setTypeface(null, Typeface.NORMAL)
        EditName.inputType = InputType.TYPE_CLASS_PHONE


        val deteleButton = ImageView(this@ScannedActivity)
        deteleButton.layoutParams = Deleteparams
        deteleButton.setImageResource(R.drawable.ic_delete)


        linearLayoutHorizontal.addView(Spinner)
        linearLayoutHorizontal.addView(EditName)
        linearLayoutHorizontal.addView(deteleButton)
        ll_tel_numbers.addView(linearLayoutHorizontal)


        deteleButton.setOnClickListener(View.OnClickListener {

            linearLayoutHorizontal.setVisibility(View.GONE)


        })


    }

    private fun AddBlankEmailFiled(email: String) {

        emailEditList = ArrayList()
        val linearparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )
        linearparams.setMargins(0, 10, 0, 0)

        val Spinnerparams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f
        )
        Spinnerparams.gravity = Gravity.CENTER
        Spinnerparams.setMargins(0, 5, 0, 0)

        val Textparams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 6f
        )
        Textparams.gravity = Gravity.CENTER
        Textparams.setMargins(0, 5, 0, 0)

        val Deleteparams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )
        Deleteparams.gravity = Gravity.CENTER
        Deleteparams.setMargins(0, 5, 0, 0)


        val linearLayoutHorizontal = LinearLayout(this@ScannedActivity)
        linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
        linearLayoutHorizontal.layoutParams = linearparams


        val Spinner = Spinner(this@ScannedActivity)
        Spinner.layoutParams = Spinnerparams

        val spinneradapterEmail = ArrayAdapter<String>(
            this@ScannedActivity,
            android.R.layout.simple_spinner_item, emailAraayListName
        )


        spinneradapterEmail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        Spinner.setAdapter(spinneradapterEmail)

        Spinner.setSelection(1)

        val EditName = EditText(this@ScannedActivity)
        EditName.layoutParams = Textparams
        EditName.setTextSize(12.0F)
        EditName.setText(email)
        EditName.setPadding(10, 5, 0, 10)
        EditName.setTypeface(null, Typeface.NORMAL)
        EditName.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS


        val deteleButton = ImageView(this@ScannedActivity)
        deteleButton.layoutParams = Deleteparams
        deteleButton.setImageResource(R.drawable.ic_delete)

        emailEditList.add(EditName)


        linearLayoutHorizontal.addView(Spinner)
        linearLayoutHorizontal.addView(EditName)
        linearLayoutHorizontal.addView(deteleButton)
        ll_emails.addView(linearLayoutHorizontal)


        deteleButton.setOnClickListener(View.OnClickListener {

            linearLayoutHorizontal.setVisibility(View.GONE)


        })


    }

    //Api Work
    fun getAppDdlList(authCode: String, userId: String) {
        val pDialog = ProgressDialog(this@ScannedActivity, R.style.AppCompatAlertDialogStyle)
        pDialog.setMessage("Loading...")
        pDialog.show()

        val historyInquiry = object : StringRequest(
            Request.Method.POST, getDdlListUrl, Response.Listener { response ->
                try {
                    Log.e("AppDdlList", response)
                    val jsonObject =
                        JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1))
                    var status = ""
                    if (jsonObject.has("status")) {
                        status = jsonObject.getString("status")
                    }

                    if (status.equals("failed", ignoreCase = true)) {

                    } else {

                        //Contact Type
                        val contactTypeArray = jsonObject.getJSONArray("ContactTypeMaster")
                        if (contactTypeList.size > 0) {
                            contactTypeList.clear()
                        }
                        contactTypeList.add(ContactTypeListModel("0", "Please Select Contact Type"))
                        for (p in 0 until contactTypeArray.length()) {
                            val `object` = contactTypeArray.getJSONObject(p)
                            val ContactTypeID = `object`.getString("ContactTypeID")
                            val ContactType = `object`.getString("ContactType")

                            contactTypeList.add(ContactTypeListModel(ContactTypeID, ContactType))

                        }

                        // Management Type
                        val managementTypeArray = jsonObject.getJSONArray("ManagementTypeMaster")
                        if (managementTypeList.size > 0) {
                            managementTypeList.clear()
                        }
                        managementTypeList.add(ManagmentTypeListModel("0", "Please Select Management Type"))
                        for (o in 0 until managementTypeArray.length()) {
                            val `object` = managementTypeArray.getJSONObject(o)

                            val ManagementTypeID = `object`.getString("ManagementTypeID")
                            val ManagementType = `object`.getString("ManagementType")

                            managementTypeList.add(ManagmentTypeListModel(ManagementTypeID, ManagementType))

                        }
//                        Business Vertical
                        val businessVerticalMasterArray = jsonObject.getJSONArray("BusinessVerticalMaster")
                        if (businessVerticalList.size > 0) {
                            businessVerticalList.clear()
                        }
                        for (k in 0 until businessVerticalMasterArray.length()) {
                            val businessVerticalObject = businessVerticalMasterArray.getJSONObject(k)
                            val BusinessVerticalID = businessVerticalObject.getString("BusinessVerticalID")
                            val BusinessVertical = businessVerticalObject.getString("BusinessVertical")


                            businessVerticalList.add(CustomerDetailsModel(BusinessVertical, BusinessVerticalID))
                        }


                    }

                    if (jsonObject.has("MsgNotification")) {
                        val MsgNotification = jsonObject.getString("MsgNotification")
                        Toast.makeText(this@ScannedActivity, MsgNotification, Toast.LENGTH_SHORT).show()

                    }
                    pDialog.dismiss()

                } catch (e: JSONException) {
                    Log.e("checking json excption", e.message)
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                VolleyLog.d("Login", "Error: " + error.message)
                // Log.e("checking now ",error.getMessage());

                Toast.makeText(this@ScannedActivity, error.message, Toast.LENGTH_SHORT).show()
                pDialog.dismiss()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["AuthCode"] = authCode
                params["UserID"] = userId
                return params
            }

        }
        historyInquiry.retryPolicy = DefaultRetryPolicy(
            SettingConstant.Retry_Time,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        AppController.getInstance().addToRequestQueue(historyInquiry, "AddDdlList")

    }


    //call popup
    private fun callPopup(list: ArrayList<CustomerDetailsModel>, checkingType: String) {

        val layoutInflater = this@ScannedActivity.getBaseContext()
            .getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = layoutInflater.inflate(R.layout.popup_window_layout, null)
        val cancel: Button
        val save: Button
        popupWindow = PopupWindow(
            popupView,
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.setTouchable(true)
        popupWindow.setFocusable(true)
        popupWindow.setAnimationStyle(R.style.animationName)
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
        save = popupView.findViewById(R.id.saveBtn)
        cancel = popupView.findViewById(R.id.cancelbtutton)

        save.setOnClickListener {
            if (checkingType.equals("1", ignoreCase = true)) {
                val idList = bussinessverticalidList.toString()
                val nameList = bussinessverticalnameList.toString()

                businessVerticalTypeString = idList.substring(1, idList.length - 1).replace(", ", ",")

                //  selectEditTxt.setText(nameList.substring(1, nameList.length - 1).replace(", ", ","))
                Log.e("Now this is final", businessVerticalTypeString)


                if (bussinessverticalidList.size > 0) {

                    if(businessVerticalCheckID.size > 0){
                        for (item in businessVerticalCheckID.indices) {
                            val getCheckValue = businessVerticalCheckID.get(item)
                            val getCheckValueName = businessVerticalCheckName.get(item)

                            bussinessverticalidList.indices.forEach { item1 ->
                                if (getCheckValue.compareTo(bussinessverticalidList.get(item1)) == 0) {
                                    bussinessverticalidList.remove(getCheckValue)
                                    bussinessverticalnameList.remove(getCheckValueName)
                                }
                            }

                        }
                    }


                    val hashSet = HashSet <String>()
                    hashSet.addAll(bussinessverticalidList)
                    bussinessverticalidList.clear()
                    bussinessverticalidList.addAll(hashSet)

                    val hashSetName = HashSet <String>()
                    hashSetName.addAll(bussinessverticalnameList)
                    bussinessverticalnameList.clear()
                    bussinessverticalnameList.addAll(hashSetName)

                    for (index in bussinessverticalidList.indices) {
                        val chip = Chip(Chip_group_business_vertical.context)
                        val chipValue = bussinessverticalnameList.get(index)
                        chip.text = chipValue

                        // necessary to get single selection working
                        chip.isClickable = true
                        chip.isCheckable = true
                        Chip_group_business_vertical.addView(chip)
                        businessVerticalCheckID.add(bussinessverticalidList.get(index))
                        businessVerticalCheckName.add(bussinessverticalnameList.get(index))
                    }
                }

            }

            popupWindow.dismiss()

            bussinessverticalidList.clear()
            bussinessverticalnameList.clear()
        }

        cancel.setOnClickListener { popupWindow.dismiss() }

        searchData = popupView.findViewById(R.id.editTextsearching) as EditText
        serchListData = popupView.findViewById(R.id.listview_customer_list) as ListView

        val demoCustomeAdapter = DemoCustomeAdapter(
            this@ScannedActivity, list, this, checkingType, this, this, this,
            this
        )
        serchListData.setAdapter(demoCustomeAdapter)


        searchData.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                demoCustomeAdapter.getFilter().filter(s.toString())
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                demoCustomeAdapter.getFilter().filter(s)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}


