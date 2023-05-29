package fr.epf.mm.cinexplore.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import fr.epf.mm.cinexplore.model.Film
import fr.epf.mm.cinexplore.R

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
        val film_title = view.findViewById<TextView>(R.id.view_film_title_textview)
        film_title.text="${film.title}"
        val film_synopsis = view.findViewById<TextView>(R.id.view_film_synopsis_textview)
        film_synopsis.text="${film.synopsis}"

    }

}