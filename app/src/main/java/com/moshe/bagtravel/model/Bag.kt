package com.moshe.bagtravel.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Bag(
    var id: Int,
    var weight:Double,
    var taken:Boolean=false

): Parcelable{
    override fun toString(): String {
        return "Bag(id=$id,weight=$weight)"
    }
}
