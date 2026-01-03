
package com.popcornpicks.details

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.popcornpicks.details.viewmodel.DetailsViewModel
import com.popcornpicks.home.R
import com.popcornpicks.home.databinding.ActivityDetailsBinding
import com.popcornpicks.home.utils.ImageUtils

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var viewModel: DetailsViewModel
    private var movieId: Int = -1

    companion object {
        const val EXTRA_MOVIE_ID = "extra_movie_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get movie ID from intent
        movieId = intent.getIntExtra(EXTRA_MOVIE_ID, -1)
        
        if (movieId == -1) {
            Toast.makeText(this, "Invalid movie ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[DetailsViewModel::class.java]

        setupViews()
        observeViewModel()

        // Fetch movie details
        viewModel.fetchMovieDetails(movieId)
    }

    private fun setupViews() {
        // Handle back button click
        binding.ivBack.setOnClickListener {
            finish()
        }

        // Handle share button click
        binding.ivShare.setOnClickListener {
            shareMovieDeeplink()
        }
    }

    private fun shareMovieDeeplink() {
        // Create deeplink URL for the movie
        val deeplink = "popcornpicks://movie/$movieId"
        
        // Create share intent
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out this movie!")
            putExtra(Intent.EXTRA_TEXT, "Check out this movie on PopcornPicks: $deeplink")
        }
        
        // Show share chooser
        startActivity(Intent.createChooser(shareIntent, "Share movie via"))
    }

    private fun observeViewModel() {
        // Observe movie details
        viewModel.movieDetails.observe(this) { movieDetails ->
            // Show content and hide loading
            binding.contentLayout.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.tvError.visibility = View.GONE

            // Set movie title
            binding.tvTitle.text = movieDetails.title

            // Set tagline
            if (!movieDetails.tagline.isNullOrEmpty()) {
                binding.tvTagline.text = movieDetails.tagline
                binding.tvTagline.visibility = View.VISIBLE
            } else {
                binding.tvTagline.visibility = View.GONE
            }

            // Set rating
            binding.tvRating.text = "⭐ ${String.format("%.1f", movieDetails.voteAverage)}/10"

            // Set runtime
            binding.tvRuntime.text = movieDetails.getFormattedRuntime()

            // Set release year
            val year = if (movieDetails.releaseDate.isNotEmpty()) {
                movieDetails.releaseDate.substring(0, 4)
            } else {
                "N/A"
            }
            binding.tvReleaseDate.text = year

            // Set genres
            binding.tvGenres.text = movieDetails.getGenresString()

            // Set overview
            binding.tvOverview.text = movieDetails.overview

            // Load backdrop image
            if (movieDetails.backdropPath != null) {
                ImageUtils.loadImage(
                    binding.ivBackdrop,
                    movieDetails.getBackdropUrl()
                )
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.contentLayout.visibility = View.GONE
                binding.tvError.visibility = View.GONE
            }
        }

        // Observe errors
        viewModel.error.observe(this) { error ->
            error?.let {
                binding.progressBar.visibility = View.GONE
                binding.contentLayout.visibility = View.GONE
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = it
                
                Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
}
