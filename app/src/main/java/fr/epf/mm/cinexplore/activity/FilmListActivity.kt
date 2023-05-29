package fr.epf.mm.cinexplore.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epf.mm.cinexplore.adapter.FilmListAdapter
import fr.epf.mm.cinexplore.model.Film
import fr.epf.mm.cinexplore.R
import fr.epf.mm.cinexplore.TmdbApiService
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FilmListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.list_film_recyclerview)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val tmdbService = retrofit.create(TmdbApiService::class.java)

        val films = listOf(
            Film("Film 1", "Description 1", 2.3),
            Film("Film 2", "Description 2", 3.5),
            Film("Film 3", "Description 3", 4.2),
            // Ajoutez d'autres films si nécessaire
        )

        val adapter = FilmListAdapter(this, films)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val testButton = findViewById<Button>(R.id.button_test)

        testButton.setOnClickListener {
            runBlocking {
                val response = tmdbService.getPopularMovies("79bc1a7b946265b5f9dd2e89b2a118b2", 1)
                Log.d("API_RESPONSE", response.toString())
            }

    }}

}