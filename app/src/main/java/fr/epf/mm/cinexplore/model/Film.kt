package fr.epf.mm.cinexplore.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Film(
    val film_id: Int,
    val posterPath: String?,
    val title: String,
    val release_date: String,
    val runtime: Int,
    val synopsis: String,
    val vote_average: Float,
    val vote_count: Int
) : Parcelable {

}