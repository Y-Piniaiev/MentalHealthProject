package com.example.mobile

import Note
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.databinding.ActivityLoginBinding
import com.example.mobile.databinding.ActivityNoteBinding
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.*
import okio.IOException
import java.net.URL


class NoteActivity : AppCompatActivity() {
    var notes: ArrayList<Note> = ArrayList()

    private lateinit var binding: ActivityNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        val intent = intent
        val token = intent.getStringExtra("token")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (token != null) {
            allNote(token)
        }

        binding.addButton.setOnClickListener{
            var id = ""
            val nextPage = Intent(this, NoteEditorActivity::class.java)
            nextPage.putExtra("token", token)
            nextPage.putExtra("id", id)
            startActivity(nextPage)
        }

        binding.btnArticle.setOnClickListener{
            val nextPage = Intent(this, ArticleActivity::class.java)
            nextPage.putExtra("token", token)
            startActivity(nextPage)
        }

        binding.btnTraining.setOnClickListener{
            val nextPage = Intent(this, TrainingActivity::class.java)
            nextPage.putExtra("token", token)
            startActivity(nextPage)
        }

    }

    private fun editing(token: String, id: String){
        val nextPage = Intent(this, NoteEditorActivity::class.java)
        nextPage.putExtra("token", token)
        nextPage.putExtra("id", id)
        startActivity(nextPage)
    }


    private fun showNotes(token: String){
        runOnUiThread {
            if (!notes.isEmpty()) {
                val recyclerview = findViewById<RecyclerView>(R.id.recycler_view)
                recyclerview.layoutManager = LinearLayoutManager(this)
                val adapter = CustomAdapter(notes)
                recyclerview.adapter = adapter

                recyclerview.addOnItemTouchListener(
                    RecyclerItemClickListenr(
                        this,
                        recyclerview,
                        object : RecyclerItemClickListenr.OnItemClickListener {

                            override fun onItemClick(view: View, position: Int) {
                                val chosenNote = adapter.getNoteAt(position)
                                val id = chosenNote._id
                                editing(token, id)
                            }

                            override fun onItemLongClick(view: View?, position: Int) {
                                val chosenNote = adapter.getNoteAt(position)
                                val id = chosenNote._id
                                deleteNote(token, id)
                            }
                        })
                )
            }
        }
    }

    private fun deleteNote(token: String, idNote: String){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to Delete?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->


                val client = OkHttpClient()
                val url = URL("http://10.0.2.2:5000/note/$idNote")
                val mapper = ObjectMapper().registerKotlinModule()

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $token")
                    .delete()
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
                                allNote(token)
                            }
                        }
                    }
                })

            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
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

    private fun allNote(token: String) {
        val client = OkHttpClient()
        val url = URL("http://10.0.2.2:5000/note")
        val mapper = ObjectMapper().registerKotlinModule()

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
                        notes = mapper.readValue(responseBody)
                        showNotes(token)
                    }
                }
            }
        })
    }


}