package fr.epf.mm.cinexplore.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Film(
    val film_id: Int,
    val posterPath: String?,
    val title: String,
    val release_date: String,
    val synopsis: String,
    val rating: Float
) : Parcelable {

}