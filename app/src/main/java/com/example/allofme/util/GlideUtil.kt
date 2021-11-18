package com.example.allofme.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory

private val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

fun ImageView.clear() = Glide.with(context).clear(this)

/*
* ImageView 위젯에 함수를 추가
* diskCacheStrategy 내장함수를 통해 불러온 이미지를 메모리에 캐싱할 수 있다.
* */

fun ImageView.load(url: String, radius: Float = 0f, scaleType: Transformation<Bitmap> = CenterInside()) {
    Glide.with(this)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(factory))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply {
            if ( radius >0 ) transform(scaleType, RoundedCorners(radius.fromDpToPx()))
         }
        .into(this)
}

/*
* xml에서는 단위로 dp를 사용하기 때문에 코드상에서 동적으로 이미지의 크기를 조정하기 위해서 px로 바꿔주는 함수.
* */

fun Float.fromDpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}