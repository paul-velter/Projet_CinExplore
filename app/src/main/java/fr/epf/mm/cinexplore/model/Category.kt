package fr.epf.mm.cinexplore.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val category_id: Int,
    val category_name: String,
    val category_icon: String
) : Parcelable {

}