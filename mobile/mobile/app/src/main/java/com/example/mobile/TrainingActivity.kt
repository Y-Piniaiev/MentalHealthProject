package com.example.mobile

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.databinding.ActivityArticleBinding
import com.example.mobile.databinding.ActivityTrainingBinding
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.*
import okio.IOException
import java.net.URL

class TrainingActivity : AppCompatActivity() {
    var trainings: ArrayList<Training> = ArrayList()
    private lateinit var binding: ActivityTrainingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = intent
        val token = intent.getStringExtra("token")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)


        binding = ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (token != null) {
            allTraining(token)
        }

        binding.btnNote.setOnClickListener{
            val nextPage = Intent(this, NoteActivity::class.java)
            nextPage.putExtra("token", token)
            startActivity(nextPage)
        }
        binding.btnArticle.setOnClickListener{
            val nextPage = Intent(this, ArticleActivity::class.java)
            nextPage.putExtra("token", token)
            startActivity(nextPage)
        }
    }

    private fun oneTraining(token: String, id: String){
        val nextPage = Intent(this, OneTrainingActivity::class.java)
        nextPage.putExtra("token", token)
        nextPage.putExtra("id", id)
        startActivity(nextPage)
    }

    private fun showArticles(token: String){
        runOnUiThread {
            if (!trainings.isEmpty()) {
                val recyclerview = findViewById<RecyclerView>(R.id.recycler_view3)
                recyclerview.layoutManager = LinearLayoutManager(this)
                val adapter = CustomAdapterTrainings(trainings)
                recyclerview.adapter = adapter

                recyclerview.addOnItemTouchListener(
                    RecyclerItemClickListenr(
                        this,
                        recyclerview,
                        object : RecyclerItemClickListenr.OnItemClickListener {

                            override fun onItemClick(view: View, position: Int) {
                                val chosenTraining = adapter.getTrainingAt(position)
                                val id = chosenTraining._id
                                oneTraining(token, id)
                            }

                            override fun onItemLongClick(view: View?, position: Int) {

                            }
                        })
                )
            }
        }
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

    private fun imageFromUrl(token: String){
        for(i in trainings){
            if(i.imageUrl.isEmpty()){
                var bitmap = BitmapFactory.decodeResource(resources, R.drawable.no_image)
                i.image = Bitmap.createScaledBitmap(bitmap, 375, 200, true);
            } else {
                val url = "http://10.0.2.2:5000${i.imageUrl}"
                var bitmap = BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
                i.image = Bitmap.createScaledBitmap(bitmap, 375, 200, true);
            }
        }
        showArticles(token)
    }

    private fun allTraining(token: String) {
        val client = OkHttpClient()
        val url = URL("http://10.0.2.2:5000/training")
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
                        trainings = mapper.readValue(responseBody)
                        imageFromUrl(token)

                    }
                }
            }
        })
    }
}