package fr.epf.mm.cinexplore.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epf.mm.cinexplore.adapter.FilmListAdapter
import fr.epf.mm.cinexplore.model.Film
import fr.epf.mm.cinexplore.R
import fr.epf.mm.cinexplore.TmdbApiService
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListSearchFilmActivity : AppCompatActivity() {

    private lateinit var recyclerView : RecyclerView
    private lateinit var searchView : SearchView
    private lateinit var qrCodeButton : Button

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val data = it.data?.getStringExtra(ScanQrCodeActivity.QR_CODE_VALUE)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_search_film)

        qrCodeButton = findViewById<Button>(R.id.list_film_QrCode_button)
        initButtonClickListener()

        recyclerView = findViewById<RecyclerView>(R.id.list_film_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val tmdbService = retrofit.create(TmdbApiService::class.java)

        var currentQuery = ""

       fun searchFilmsByTitle(query: String) {
            runBlocking {
                val films = tmdbService.searchMoviesByTitle("79bc1a7b946265b5f9dd2e89b2a118b2", currentQuery, 1).results.map {
                    Film(
                        it.id,
                        it.poster_path,
                        it.title,
                        it.release_date,
                        it.runtime,
                        it.overview,
                        it.vote_average,
                        it.vote_count
                    )
                }
                Log.d("API_RESPONSE", films.toString())
                recyclerView.adapter = FilmListAdapter(this@ListSearchFilmActivity, films)
            }
       }

        fun searchFilmsById(id: Int) {
            runBlocking {
                val films = tmdbService.searchMoviesById(id,"79bc1a7b946265b5f9dd2e89b2a118b2").results.map {

                }

            }
        }

        searchView = findViewById<SearchView>(R.id.list_film_searchBar_searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                currentQuery = newText
                searchFilmsByTitle(currentQuery)
                return true
            }
        })

    }

    private fun initButtonClickListener(){
        qrCodeButton.setOnClickListener {
            val intent = Intent(this, ScanQrCodeActivity::class.java)
            resultLauncher.launch(intent)
        }

    }
}
