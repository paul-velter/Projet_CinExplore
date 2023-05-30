package fr.epf.mm.cinexplore.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import fr.epf.mm.cinexplore.R
import fr.epf.mm.cinexplore.model.Film

class DetailsFilmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_film)

        val film = intent.extras?.get("film") as? Film

        val film_poster = findViewById<ImageView>(R.id.detail_film_poster_imageView)
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500/${film?.posterPath}?api_key=79bc1a7b946265b5f9dd2e89b2a118b2")
            .into(film_poster)

        val film_title = findViewById<TextView>(R.id.detail_film_title_textView)
        film_title.text = film?.title

        val film_rating = findViewById<TextView>(R.id.detail_film_rating_textView)
        film_rating.text = film?.rating.toString()

        val film_synopsis = findViewById<TextView>(R.id.detail_film_synopsis_textView)
        film_synopsis.text = film?.synopsis

    }
}