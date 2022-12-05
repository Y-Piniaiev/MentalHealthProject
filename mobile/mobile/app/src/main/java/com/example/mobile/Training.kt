package com.example.mobile

import android.graphics.Bitmap
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Training(
    var _id: String,
    var title: String,
    var task: String,
    var text: String,
    var time: Int,
    var viewsCount: Int,
    var imageUrl: String,
    var image: Bitmap?
)
