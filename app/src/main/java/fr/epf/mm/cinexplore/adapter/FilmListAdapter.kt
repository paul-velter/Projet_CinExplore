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

class FilmViewHolder(view: View) : RecyclerView.ViewHolder(view)

class FilmListAdapter (val context: Context, val films: List<Film>) : RecyclerView.Adapter<FilmViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.viewholder_film_list, parent, false)
        return FilmViewHolder(view)
    }

    override fun getItemCount() = films.size

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = films[position]
        val view = holder.itemView
        val film_title = view.findViewById<TextView>(R.id.view_film_title_textView)
        film_title.text="${film.title}"
        val film_poster = view.findViewById<ImageView>(R.id.view_film_poster_imageView)
        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w500/${film.posterPath}?api_key=79bc1a7b946265b5f9dd2e89b2a118b2")
            .into(film_poster)

        val cardView = view.findViewById<CardView>(R.id.view_film_cardview)
        cardView.setOnClickListener{
            val intent = Intent(context, DetailsFilmActivity::class.java)
            intent.putExtra("film", film)
            context.startActivity(intent)
        }
    }

}