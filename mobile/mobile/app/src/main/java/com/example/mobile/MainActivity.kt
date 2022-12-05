package com.example.mobile

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var spinner: Spinner
    lateinit var locale: Locale
    private var currentLanguage = "en"
    private var currentLang: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentLanguage = intent.getStringExtra(currentLang).toString()
        spinner = findViewById(R.id.spinner_lang)
        val list = ArrayList<String>()
        list.add("Select Language")
        list.add("English")
        list.add("Українська")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> {
                    }
                    1 -> setLocale("en")
                    2 -> setLocale("uk")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        binding.btnLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.btnRegister.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

//    fun setLocalLanguage(localcode: String){
//        var locale: Locale = Locale(localcode)
//        Locale.setDefault(locale)
//
//        var config: Configuration = Configuration()
//        config.setLocale(locale)
//
//        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
//        val nextPage = Intent(this, MainActivity::class.java)
//        nextPage.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        startActivity(nextPage)
//    }

    private fun setLocale(localeName: String) {
        if (localeName != currentLanguage) {
            locale = Locale(localeName)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration
            conf.locale = locale
            res.updateConfiguration(conf, dm)
            val refresh = Intent(
                this,
                MainActivity::class.java
            )
            refresh.putExtra(currentLang, localeName)
            startActivity(refresh)
        } else {
            Toast.makeText(
                this@MainActivity, "Language, , already, , selected)!", Toast.LENGTH_SHORT).show();
        }
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
        exitProcess(0)
    }


}