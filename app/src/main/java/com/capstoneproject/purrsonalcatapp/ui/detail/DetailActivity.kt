package com.capstoneproject.purrsonalcatapp.ui.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.capstoneproject.purrsonalcatapp.R

class DetailActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_ARTIKEL = "extra_artikel"
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

//        val dataArtikel = if (Build.VERSION.SDK_INT >= 33) {
//            intent.getParcelableExtra<Artikel>("key_artikel", Artikel::class.java)
//        } else {
//            @Suppress("DEPRECATION")
//            intent.getParcelableExtra<Artikel>("key_artikel")
//        }
        val dataArtikel = intent.getParcelableExtra<Artikel>(EXTRA_ARTIKEL)

        val tvDetailName: TextView = findViewById(R.id.text_judul)
        val tvDetailDescription: TextView = findViewById(R.id.text_des)
        val ivDetailPhoto: ImageView = findViewById(R.id.img_photo)

        if (dataArtikel != null) {
            tvDetailName.text = dataArtikel.judul
            tvDetailDescription.text = dataArtikel.description
            ivDetailPhoto.setImageResource(dataArtikel.photo)
        }
    }
}