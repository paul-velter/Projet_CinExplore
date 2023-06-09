package fr.epf.mm.cinexplore.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Film(
    val film_id: Int,
    val posterPath: String?,
    val title: String,
    val genre: List<String>,
    val genre_id: List<Int>,
    val release_date: String,
    val runtime: Int,
    val overview: String,
    val vote_average: Float,
    val vote_count: Int,
    val trailer_link: String?

) : Parcelable {

}