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

class FilmVerticalViewHolder(view: View) : RecyclerView.ViewHolder(view)

class FilmVerticalAdapter(private val context: Context, private val films: List<Film>) :
    RecyclerView.Adapter<FilmVerticalViewHolder>() {

    private lateinit var filmPoster: ImageView
    private lateinit var filmTitle: TextView
    private lateinit var filmGenre: TextView
    private lateinit var filmRating: TextView
    private lateinit var cardView: CardView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmVerticalViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.viewholder_film_vertical, parent, false)
        return FilmVerticalViewHolder(view)
    }

    override fun getItemCount() = films.size

    override fun onBindViewHolder(holder: FilmVerticalViewHolder, position: Int) {
        val film = films[position]
        val view = holder.itemView

        filmPoster = view.findViewById(R.id.view_film_poster_imageView)
        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w500/${film.posterPath}?api_key=79bc1a7b946265b5f9dd2e89b2a118b2")
            .into(filmPoster)

        filmTitle = view.findViewById(R.id.view_film_title_textView)
        filmTitle.text = film.title

        filmGenre = view.findViewById(R.id.view_film_genres_textView)
        filmGenre.text = film.genre.joinToString(" / ") { it }

        filmRating = view.findViewById(R.id.view_film_rating_textView)
        filmRating.text = DecimalFormat("#.#").format(film.vote_average)
        cardView = view.findViewById(R.id.view_film_cardview)
        cardView.setOnClickListener {
            val intent = Intent(context, DetailsFilmActivity::class.java)
            intent.putExtra("film", film)
            context.startActivity(intent)
        }
    }

}