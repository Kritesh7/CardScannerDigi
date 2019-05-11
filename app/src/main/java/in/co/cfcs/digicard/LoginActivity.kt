package `in`.co.cfcs.digicard

import `in`.co.cfcs.digicard.Helper.*
import `in`.co.cfcs.digicard.MainActivity.MainActivity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.VolleyLog.*
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONException
import java.util.HashMap

class LoginActivity : AppCompatActivity() {

    lateinit var btn_login: Button
    lateinit var txt_user_name: EditText
    lateinit var txt_password: EditText

    lateinit var conn: ConnectionDetector

    var loginUrl = SettingConstant.BASEURL_FOR_LOGIN + "loginservice.asmx/AppUserLogin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txt_user_name = findViewById(R.id.txt_user_name)
        txt_password = findViewById(R.id.txt_password)
        btn_login = findViewById(R.id.btn_login)

        conn = ConnectionDetector(this@LoginActivity)

        btn_login.setOnClickListener(View.OnClickListener {

            var AuthCode = ""
            // Device model
            val PhoneModel = android.os.Build.MODEL

            // Android version
            val AndroidVersion = android.os.Build.VERSION.RELEASE

            val randomNumber = (Math.random() * 9000000 + 1000000).toLong()
            AuthCode = randomNumber.toString()

            if (txt_user_name.getText().toString().equals("", ignoreCase = true)) {
                txt_user_name.setError("Please Enter Valid User Name")
            } else if (txt_password.getText().toString().equals("", ignoreCase = true)) {
                txt_password.setError("Please Enter Valid Password")
            } else {

                if (conn.getConnectivityStatus() > 0) {
                    Login_Api(
                        txt_user_name.getText().toString(), txt_password.getText().toString(),
                        AuthCode, PhoneModel, AndroidVersion
                    )
                } else {
                    conn.showNoInternetAlret()
                }
            }


        })

    }

    fun Login_Api(
        UserName: String, Password: String, AuthCode: String,
        ClientName: String, ClientVersion: String
    ) {
        val pDialog = ProgressDialog(this@LoginActivity, R.style.AppCompatAlertDialogStyle)
        pDialog.setMessage("Loading...")
        pDialog.show()

        val historyInquiry = object : StringRequest(
            Request.Method.POST, loginUrl, object : Response.Listener<String> {
                override fun onResponse(response: String) {

                    try {
                        Log.e("Login", response)
                        val jsonArray =
                            JSONArray(response.substring(response.indexOf("["), response.lastIndexOf("]") + 1))

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            // String status = jsonObject.getString("status");
                            if (jsonObject.has("MsgNotification")) {
                                val MsgNotification = jsonObject.getString("MsgNotification")
                                Toast.makeText(this@LoginActivity, MsgNotification, Toast.LENGTH_SHORT).show()

                            } else {

                                val UserID = jsonObject.getString("UserID")
                                val Name = jsonObject.getString("Name")
                                val RoleName = jsonObject.getString("RoleName")
                                val EmailID = jsonObject.getString("EmailID")
                                val MobileNo = jsonObject.getString("MobileNo")
                                val ZoneID = jsonObject.getString("ZoneID")
                                val ZoneName = jsonObject.getString("ZoneName")
                                val AuthCode = jsonObject.getString("AuthCode")


                                val ik = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(ik)
                                finish()


                                UtilsMethods.getBlankIfStringNull(SharedPrefs.setStatus(this@LoginActivity, "1").toString())
                                UtilsMethods.getBlankIfStringNull(SharedPrefs.setUserId(this@LoginActivity, UserID).toString())
                                UtilsMethods.getBlankIfStringNull(SharedPrefs.setAuthCode(this@LoginActivity, AuthCode).toString())
                                UtilsMethods.getBlankIfStringNull(SharedPrefs.setZoneId(this@LoginActivity, ZoneID).toString())

                            }


                        }

                        pDialog.dismiss()

                    } catch (e: JSONException) {
                        Log.e("checking json excption", e.message)
                        e.printStackTrace()
                    }

                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    d("Login", "Error: " + error.message)
                    // Log.e("checking now ",error.getMessage());

                    Toast.makeText(this@LoginActivity, error.message, Toast.LENGTH_SHORT).show()
                    pDialog.dismiss()


                }
            }) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                    params["UserName"] = UserName
                    params["Password"] = Password
                    params["AuthCode"] = AuthCode
                    params["ClientName"] = ClientName
                    params["ClientVersion"] = ClientVersion

                    Log.e("Parms", params.toString())

                return params
            }


        }
        historyInquiry.setRetryPolicy(DefaultRetryPolicy(SettingConstant.Retry_Time, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        AppController.getInstance().addToRequestQueue(historyInquiry, "Login")

    }




}
