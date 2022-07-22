package com.example.wsacare

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PatternMatcher
import android.util.JsonToken
import android.view.View
import androidx.core.util.PatternsCompat
import com.example.wsacare.core.Constants
import com.example.wsacare.core.networkInfo
import com.example.wsacare.data.rest.Rest
import com.example.wsacare.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

    clicks()
    }

    private fun clicks() {
        binding.btnSignIn.setOnClickListener { validateData() }
    }

    private fun validateData() {

        if (!networkInfo(this )){
            alertMessage("No internet connection")
            return
        }

        val results = arrayOf(validateEmail(),validatePassword())
        if (false in results)
            return

        sendPost()

    }

    private fun hideProgress() {
        binding.progress.visibility = View.GONE
        binding.btnSignIn.visibility = View.VISIBLE
    }

    private fun sendPost() {
        binding.progress.visibility = View.VISIBLE
        binding.btnSignIn.visibility = View.GONE
        Constants.okHttp.newCall(Rest().post("signin/",Rest().signIn(binding.edtEmail.text.toString(),binding.edtPassword.text.toString()))).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                alertMessage("Error from server!")
                runOnUiThread {
                    hideProgress()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                runOnUiThread {
                    hideProgress()
                }
                if (json.getBoolean("success")){
                    val data = json.getJSONObject("data")
                    val sharedPreferences = getSharedPreferences(Constants.USER,Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()){
                        putString("id",data.getString("id"))
                        putString("name",data.getString("name"))
                        apply()
                    }
                    val i = Intent(this@MainActivity,HomeActivity::class.java)
                    startActivity(i)
                }else{
                    alertMessage("The credentials are wrong")
                }
            }
        })

    }

    private fun validatePassword(): Boolean {
        return if (binding.edtPassword.text.toString().isNullOrEmpty()){
            alertMessage("Any field can be empty")
            false
        }else{
            true
        }
    }

    private fun validateEmail(): Boolean {
        return if (binding.edtEmail.text.toString().isNullOrEmpty()){
            alertMessage("Any field can be empty")
            false
        }else if (!PatternsCompat.EMAIL_ADDRESS.matcher(binding.edtEmail.text.toString()).matches()){
            alertMessage("The email field must have a email body")
            false
        }else{
            true
        }
    }

    private fun alertMessage(s: String) {
        binding.txtAlert.text = s
        binding.btnSignIn.animate().translationY(300f).setDuration(200).withEndAction{
            binding.bgAlert.animate().alpha(1f).setDuration(200).withEndAction {
                binding.bgAlert.animate().alpha(1f).setDuration(800).withEndAction {
                    binding.bgAlert.animate().alpha(0f).setDuration(200)
                    binding.btnSignIn.animate().translationY(0f).setDuration(200)
                }
            }
        }
    }


}