package fr.epf.mm.cinexplore.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.mm.cinexplore.model.Film
import fr.epf.mm.cinexplore.R
import fr.epf.mm.cinexplore.activity.DetailsFilmActivity
import java.text.DecimalFormat

class FilmViewHolder(view: View) : RecyclerView.ViewHolder(view)

class FilmListAdapter (val context: Context, val films: List<Film>) : RecyclerView.Adapter<FilmViewHolder>(){

    private lateinit var filmPoster: ImageView
    private lateinit var filmTitle: TextView
    private lateinit var filmRating: TextView
    private lateinit var filmTime: TextView
    private lateinit var filmVoteCount: TextView
    private lateinit var filmGenre: TextView
    private lateinit var cardView: CardView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.viewholder_film_list, parent, false)
        return FilmViewHolder(view)
    }

    override fun getItemCount() = films.size

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = films[position]
        val view = holder.itemView

        filmPoster = view.findViewById(R.id.view_film_poster_imageView)
        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w500/${film.posterPath}?api_key=79bc1a7b946265b5f9dd2e89b2a118b2")
            .into(filmPoster)

        filmTitle = view.findViewById(R.id.view_film_title_textView)
        filmTitle.text = film.title

        filmRating = view.findViewById(R.id.view_film_voteAverage_textView)
        filmRating.text = DecimalFormat("#.#").format(film.vote_average)

        filmTime = view.findViewById(R.id.view_film_time_textView)
        filmTime.text = film.runtime.toString() + " min."

        filmVoteCount = view.findViewById(R.id.view_film_voteCount_textView)
        filmVoteCount.text = film.vote_count.toString()

        filmGenre = view.findViewById(R.id.view_film_genres_textView)
        filmGenre.text = film.genre.map { it }.joinToString(" / ")


        cardView = view.findViewById(R.id.view_film_cardview)
        cardView.setOnClickListener{
            val intent = Intent(context, DetailsFilmActivity::class.java)
            intent.putExtra("film", film)
            context.startActivity(intent)
        }
    }

}