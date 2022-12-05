package com.example.mobile

import android.graphics.Bitmap
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Article(
    var _id: String,
    var title: String,
    var text: String,
    var viewsCount: Int,
    var imageUrl: String,
    var image: Bitmap?
)

