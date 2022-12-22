package com.moshe.bagtravel.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Bag(
    var id: Int,
    var weight:Double

): Parcelable
