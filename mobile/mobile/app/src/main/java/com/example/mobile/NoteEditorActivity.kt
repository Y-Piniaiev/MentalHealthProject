package com.example.mobile

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mobile.databinding.ActivityNoteEditorBinding
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.net.URL


@SuppressLint("CheckResult")
class NoteEditorActivity : AppCompatActivity() {


    private lateinit var binding: ActivityNoteEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = intent
        val token = intent.getStringExtra("token")
        val noteId = intent.getStringExtra("id")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_editor)

        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(noteId != ""){
            if (noteId != null) {
                if (token != null) {
                    getNote(noteId, token)
                }
            }
        }


        val titleStream = RxTextView.textChanges(binding.etTitle)
            .skipInitialValue()
            .map { title -> title.isEmpty()}
        titleStream.subscribe{
            showTextMinimalAlert(it, "Title")
        }

        val contentStream = RxTextView.textChanges(binding.etContent)
            .skipInitialValue()
            .map{ content -> content.isEmpty()}
        contentStream.subscribe{
            showTextMinimalAlert(it, "Content")
        }

        val invalidFieldsStream = Observable.combineLatest(
            titleStream, contentStream,
            {titleInvalid: Boolean, contentInvalid: Boolean ->
                !titleInvalid && !contentInvalid
            })
        invalidFieldsStream.subscribe {isValid ->
            if (isValid){
                binding.btnSave.isEnabled = true
                binding.btnSave.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary_color)
            } else {
                binding.btnSave.isEnabled = false
                binding.btnSave.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }

        binding.btnSave.setOnClickListener {
            if(noteId == "") {
                noteCreate(
                    binding.etTitle.text.toString(),
                    binding.etContent.text.toString(),
                    token!!
                )
            } else {
                noteUpdate(binding.etTitle.text.toString(),
                    binding.etContent.text.toString(),
                    token!!, noteId!!)

            }
        }

    }

    private fun prevPage(token: String){
        val nextPage = Intent(this, NoteActivity::class.java)
        nextPage.putExtra("token", token)
        startActivity(nextPage)
    }

    private fun alert(){
        Handler(Looper.getMainLooper()).post {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Error")
            alertDialogBuilder.setMessage("Something went wrong. Try again!")
            alertDialogBuilder.setPositiveButton("Ok", DialogInterface.OnClickListener{ dialog, id ->
                dialog.cancel()
            });
            alertDialogBuilder.show()
        }
    }

    private fun noteCreate(title: String, content: String, token: String) {

        val client = OkHttpClient()
        val url = URL("http://10.0.2.2:5000/note")
        var jsonString = "{\"title\": \"$title\", \"text\": \"$content\"}"

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
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
                        prevPage(token)
                    }
                }
            }
        })
    }

    private fun noteUpdate(title: String, content: String, token: String, id: String) {

        val client = OkHttpClient()
        val url = URL("http://10.0.2.2:5000/note/$id")
        var jsonString = "{\"title\": \"$title\", \"text\": \"$content\"}"

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .patch(body)
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
                        prevPage(token)
                    }
                }
            }
        })
    }

    private fun getNote(id: String, token: String) {
        val client = OkHttpClient()
        val url = URL("http://10.0.2.2:5000/note/$id")

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .get()
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
                        val responseBody = response.body!!.string()
                        val mapperAll = ObjectMapper()
                        val objData = mapperAll.readTree(responseBody)
                        runOnUiThread {
                            binding.etTitle.setText(objData.get("title").toString().replace("\"", ""))
                            binding.etContent.setText(objData.get("text").toString().replace("\"", ""))
                        }
                    }
                }
            }
        })
    }


    private fun showTextMinimalAlert(isNotValid: Boolean, text: String){
        if (text == "Title")
            binding.etTitle.error = if (isNotValid) "$text text cannot be empty" else null
        else if (text == "Content")
            binding.etContent.error = if (isNotValid) "$text text cannot be empty" else null
    }
}