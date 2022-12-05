package com.example.mobile

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import com.example.mobile.databinding.ActivityOneArticleBinding
import com.example.mobile.databinding.ActivityOneTrainingBinding
import com.fasterxml.jackson.databind.ObjectMapper
import io.noties.markwon.Markwon
import okhttp3.*
import okio.IOException
import java.net.URL

class OneTrainingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOneTrainingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = intent
        val token = intent.getStringExtra("token")
        val trainingId = intent.getStringExtra("id")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_training)


        binding = ActivityOneTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val markwon = Markwon.create(this)

        if (token != null) {
            if (trainingId != null) {
                oneTraining(trainingId, token, markwon)
            }
        }

        binding.prevButton.setOnClickListener{
            val nextPage = Intent(this, TrainingActivity::class.java)
            nextPage.putExtra("token", token)
            startActivity(nextPage)
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

    private fun oneTraining(id: String, token: String, markwon: Markwon) {
        val client = OkHttpClient()
        val url = URL("http://10.0.2.2:5000/training/$id")

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
                        val imageUrl = objData.get("imageUrl").toString().replace("\"", "")

                        var image = if (imageUrl.isEmpty()) {
                            var bitmap =
                                BitmapFactory.decodeResource(resources, R.drawable.no_image)
                            Bitmap.createScaledBitmap(bitmap, 395, 300, true);
                        } else {
                            val url = "http://10.0.2.2:5000${imageUrl}"
                            var bitmap = BitmapFactory.decodeStream(
                                URL(url).openConnection().getInputStream()
                            )
                            Bitmap.createScaledBitmap(bitmap, 395, 300, true);
                        }

                        runOnUiThread {
                            binding.imageTraining.setImageBitmap(image)
                            binding.titleTraining.text =
                                objData.get("title").toString().replace("\"", "")
                            val node1 = markwon.parse(objData.get("text").textValue().replace("\"", ""))
                            val markdown1 = markwon.render(node1)
                            markwon.setParsedMarkdown( binding.textTraining, markdown1)
                            val node2 = markwon.parse(objData.get("task").textValue().replace("\"", ""))
                            val markdown2 = markwon.render(node2)
                            markwon.setParsedMarkdown( binding.taskTraining, markdown2)
                            binding.timeTraining.text =
                                objData.get("time").toString()
                        }
                    }
                }
            }
        })
    }
}