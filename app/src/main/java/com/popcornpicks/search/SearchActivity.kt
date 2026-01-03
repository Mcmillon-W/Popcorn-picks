
package com.popcornpicks.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.popcornpicks.home.R
import com.popcornpicks.home.adapter.MultiViewAdapter
import com.popcornpicks.home.adapter.SpacingItemDecoration
import com.popcornpicks.home.adapter.ViewItem
import com.popcornpicks.home.adapter.ViewTypes
import com.popcornpicks.home.adapter.renderers.CardItemRenderer
import com.popcornpicks.home.adapter.renderers.TextItemRenderer
import com.popcornpicks.home.databinding.ActivitySearchBinding
import com.popcornpicks.home.models.CardItem
import com.popcornpicks.home.models.TextItem
import com.popcornpicks.search.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val adapter = MultiViewAdapter()
    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        setupViews()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupViews() {
        // Handle back button click
        binding.ivBack.setOnClickListener {
            finish()
        }

        // Setup search text watcher
        // Debounce logic is handled in ViewModel using coroutines
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                
                // Clear results if query is empty
                if (query.isEmpty()) {
                    viewModel.clearSearch()
                    adapter.clearItems()
                    showEmptyState()
                    return
                }

                // ViewModel handles debounce automatically
                viewModel.searchMovies(query)
            }
        })
    }

    private fun setupRecyclerView() {
        // Define card click listener
        val onCardClick: (CardItem) -> Unit = { cardItem ->
            // Handle card click - open DetailsActivity with movie ID
            val intent = android.content.Intent(this, com.popcornpicks.details.DetailsActivity::class.java).apply {
                putExtra(com.popcornpicks.details.DetailsActivity.EXTRA_MOVIE_ID, cardItem.id)
            }
            startActivity(intent)
        }
        
        // Register renderers
        adapter.registerRenderer(ViewTypes.TEXT_ITEM, TextItemRenderer())
        adapter.registerRenderer(ViewTypes.CARD_ITEM, CardItemRenderer(onCardClick))

        // Setup RecyclerView
        binding.rvSearch.layoutManager = LinearLayoutManager(this)
        binding.rvSearch.adapter = adapter
        
        // Add item decoration for spacing between cards
        val spacingInPixels = dpToPx(16)
        binding.rvSearch.addItemDecoration(SpacingItemDecoration(spacingInPixels))
    }

    private fun observeViewModel() {
        // Observe search results
        viewModel.searchResults.observe(this) { movies ->
            if (movies.isNotEmpty()) {
                // Create items list
                val items = mutableListOf<ViewItem<*>>()

                // Add search results header
                items.add(
                    ViewItem(
                        ViewTypes.TEXT_ITEM,
                        TextItem("Search Results", "${movies.size} movies found")
                    )
                )

                // Add card items directly (like HomeActivity)
                val cardItems = movies.map { movie ->
                    ViewItem(
                        ViewTypes.CARD_ITEM,
                        CardItem(
                            id = movie.id,
                            title = movie.title,
                            imageUrl = movie.getPosterUrl()
                        )
                    )
                }
                items.addAll(cardItems)

                adapter.setItems(items)
                showResults()
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                hideAllStates()
            }
        }

        // Observe empty state
        viewModel.isEmpty.observe(this) { isEmpty ->
            if (isEmpty) {
                showNoResultsState()
            }
        }

        // Observe errors
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
    
    private fun showEmptyState() {
        binding.emptyState.visibility = View.VISIBLE
        binding.noResultsState.visibility = View.GONE
        binding.rvSearch.visibility = View.GONE
    }
    
    private fun showNoResultsState() {
        binding.emptyState.visibility = View.GONE
        binding.noResultsState.visibility = View.VISIBLE
        binding.rvSearch.visibility = View.GONE
    }
    
    private fun showResults() {
        binding.emptyState.visibility = View.GONE
        binding.noResultsState.visibility = View.GONE
        binding.rvSearch.visibility = View.VISIBLE
    }
    
    private fun hideAllStates() {
        binding.emptyState.visibility = View.GONE
        binding.noResultsState.visibility = View.GONE
        binding.rvSearch.visibility = View.GONE
    }
    
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
