package com.example.mobile

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile.databinding.ActivityOneArticleBinding
import com.fasterxml.jackson.databind.ObjectMapper
import io.noties.markwon.Markwon
import okhttp3.*
import okio.IOException
import java.net.URL


class OneArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOneArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = intent
        val token = intent.getStringExtra("token")
        val articleId = intent.getStringExtra("id")


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_article)

        binding = ActivityOneArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val markwon = Markwon.create(this)

        if (token != null) {
            if (articleId != null) {
                oneArticle(articleId, token, markwon)
            }
        }

        binding.prevButton.setOnClickListener{
            val nextPage = Intent(this, ArticleActivity::class.java)
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

    private fun oneArticle(id: String, token: String, markwon: Markwon) {
        val client = OkHttpClient()
        val url = URL("http://10.0.2.2:5000/posts/$id")

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
                            binding.imageArticle.setImageBitmap(image)
                            binding.titleArticle.text =
                                objData.get("title").toString().replace("\"", "")
                            val node = markwon.parse(objData.get("text").textValue().replace("\"", ""))
                            val markdown = markwon.render(node)
                            markwon.setParsedMarkdown( binding.textArticle, markdown)
                        }
                    }
                }
            }
        })
    }
}