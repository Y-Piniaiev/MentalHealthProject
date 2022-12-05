package com.example.mobile

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mobile.databinding.ActivityRegisterBinding
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("CheckResult")
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nameStream = RxTextView.textChanges(binding.etFullname)
            .skipInitialValue()
            .map { name -> name.isEmpty()}
        nameStream.subscribe{
            showNameExistAlert(it)
        }

        val emailStream = RxTextView.textChanges(binding.etEmail)
            .skipInitialValue()
            .map { email -> !Patterns.EMAIL_ADDRESS.matcher(email).matches()}
        emailStream.subscribe{
            showEmailValidAlert(it)
        }

        val passwordStream = RxTextView.textChanges(binding.etPassword)
            .skipInitialValue()
            .map{ password -> password.length < 8}
        passwordStream.subscribe{
            showTextMinimalAlert(it, "Password")
        }

        val invalidFieldsStream = Observable.combineLatest(
            nameStream, emailStream, passwordStream,
            { nameInvalid: Boolean, emailInvalid: Boolean, passwordInvalid: Boolean ->
                !nameInvalid && !emailInvalid && !passwordInvalid
            })
        invalidFieldsStream.subscribe {isValid ->
            if (isValid){
                binding.btnRegister.isEnabled = true
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary_color)
            } else {
                binding.btnRegister.isEnabled = false
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }


        binding.btnRegister.setOnClickListener{
            register(binding.etEmail.text.toString(), binding.etPassword.text.toString(), binding.etFullname.text.toString())
        }
        binding.tvHaveAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun date(): String {
        val year: Int = binding.etDate.getYear()
        val month: Int = binding.etDate.getMonth()
        val day: Int = binding.etDate.getDayOfMonth()

        val calendar: Calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        val format = SimpleDateFormat("yyyy-MM-dd")
        val strDate: String = format.format(calendar.getTime())
        return strDate;
    }

    private fun nextPage(){
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun alert(){
        Handler(Looper.getMainLooper()).post {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Register error")
            alertDialogBuilder.setMessage("Problem with registration. Check if the input is correct. Try again!")
            alertDialogBuilder.setPositiveButton("Ok", DialogInterface.OnClickListener{ dialog, id ->
                dialog.cancel()
            });
            alertDialogBuilder.show()
        }
    }

    private fun register(email: String, password: String, name: String) {

        val client = OkHttpClient()
        val url = URL("http://10.0.2.2:5000/auth/register/")
        var jsonString = "{\"email\": \"$email\", \"password\": \"$password\", \"role\": \"user\",\"name\": \"$name\",\"birth\": \"${date()}\"}"

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
                        nextPage()
                    }
                }
            }
        })
    }

    private fun showNameExistAlert(isNotValid: Boolean){
        binding.etFullname.error = if (isNotValid) "Name cannot be empty" else null
    }

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String){
        if (text == "Password")
            binding.etPassword.error = if (isNotValid) "$text text must be longer than 8 characters" else null
    }

    private fun showEmailValidAlert(isNotValid: Boolean){
        binding.etEmail.error = if (isNotValid) "Invalid email" else null
    }
}