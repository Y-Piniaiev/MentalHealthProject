package com.example.mobile

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mobile.databinding.ActivityLoginBinding
import com.fasterxml.jackson.databind.ObjectMapper
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.net.URL


@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val emailStream = RxTextView.textChanges(binding.etEmail)
            .skipInitialValue()
            .map { email -> email.isEmpty()}
        emailStream.subscribe{
            showTextMinimalAlert(it, "Email")
        }

        val passwordStream = RxTextView.textChanges(binding.etPassword)
            .skipInitialValue()
            .map{ password -> password.isEmpty()}
        passwordStream.subscribe{
            showTextMinimalAlert(it, "Password")
        }

        val invalidFieldsStream = Observable.combineLatest(
            emailStream, passwordStream,
            {emailInvalid: Boolean, passwordInvalid: Boolean ->
                !emailInvalid && !passwordInvalid
            })
        invalidFieldsStream.subscribe {isValid ->
            if (isValid){
                binding.btnLogin.isEnabled = true
                binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary_color)
            } else {
                binding.btnLogin.isEnabled = false
                binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }

        binding.btnLogin.setOnClickListener {
            login(binding.etEmail.text.toString(), binding.etPassword.text.toString())
        }
        binding.tvHaventAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }


    private fun nextPage(){
        val nextPage = Intent(this, NoteActivity::class.java)
        nextPage.putExtra("token", token)
        startActivity(nextPage)
    }

    private fun alert(){
        Handler(Looper.getMainLooper()).post {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Login error")
            alertDialogBuilder.setMessage("No such user. Try again!")
            alertDialogBuilder.setPositiveButton("Ok", DialogInterface.OnClickListener{ dialog, id ->
                dialog.cancel()
            });
            alertDialogBuilder.show()
        }
    }

    private fun login(email: String, password: String) {
        val client = OkHttpClient()
        val url = URL("http://10.0.2.2:5000/auth/login/")
        val mapperAll = ObjectMapper()
        var jsonString = "{\"email\": \"$email\", \"password\": \"$password\"}"

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        alert()
                    }
                    else if(response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val objData = mapperAll.readTree(responseBody)
                        token = objData.get("token").textValue()
                        nextPage()
                    }
                }
            }
        })
    }

    var token = ""

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String){
        if (text == "Email")
            binding.etEmail.error = if (isNotValid) "$text text cannot be empty" else null
        else if (text == "Password")
            binding.etPassword.error = if (isNotValid) "$text text cannot be empty" else null
    }

}

private fun Call.enqueue(responseCallback: Any) {

}
