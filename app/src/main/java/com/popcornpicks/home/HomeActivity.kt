package com.popcornpicks.home

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.popcornpicks.home.adapter.MultiViewAdapter
import com.popcornpicks.home.adapter.SpacingItemDecoration
import com.popcornpicks.home.adapter.ViewItem
import com.popcornpicks.home.adapter.ViewTypes
import com.popcornpicks.home.adapter.renderers.CardItemRenderer
import com.popcornpicks.home.adapter.renderers.ErrorItemRenderer
import com.popcornpicks.home.adapter.renderers.HorizontalListRenderer
import com.popcornpicks.home.adapter.renderers.LoaderItemRenderer
import com.popcornpicks.home.adapter.renderers.TextItemRenderer
import com.popcornpicks.home.databinding.ActivityHomeBinding
import com.popcornpicks.home.models.CardItem
import com.popcornpicks.home.models.ErrorItem
import com.popcornpicks.home.models.HorizontalListSection
import com.popcornpicks.home.models.LoaderItem
import com.popcornpicks.home.models.TextItem
import com.popcornpicks.home.viewmodel.MOVIE_CATEGORY
import com.popcornpicks.home.viewmodel.MoviesViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val adapter = MultiViewAdapter()
    private lateinit var viewModel: MoviesViewModel
    private var currentTab = 0 // 0 for Now Playing, 1 for Trending

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize ViewModel with application context
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MoviesViewModel::class.java]

        setupTopContainer()
        setupTabs()
        setupRecyclerView()
        observeViewModel()

        // Fetch now playing movies initially with reset
        viewModel.fetchMovies(category = MOVIE_CATEGORY.NOW_PLAYING)
        viewModel.category = MOVIE_CATEGORY.NOW_PLAYING
    }

    private fun setupTopContainer() {
        // Create search icon ImageView
        val searchIcon = ImageView(this).apply {
            setImageDrawable(ContextCompat.getDrawable(this@HomeActivity, R.drawable.search_ic))
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(40),
                dpToPx(40)
            ).apply {
                marginEnd = dpToPx(16)
            }
            scaleType = ImageView.ScaleType.FIT_CENTER
            setOnClickListener {
                // Navigate to SearchActivity
                startActivity(
                    android.content.Intent(
                        this@HomeActivity,
                        com.popcornpicks.search.SearchActivity::class.java
                    )
                )
            }
        }

        // Create profile ImageView
        val profileImage = ImageView(this).apply {
            setImageDrawable(ContextCompat.getDrawable(this@HomeActivity, R.drawable.profile_home))
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(40),
                dpToPx(40)
            )
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        // Add ImageViews to top_container
        binding.topContainer.addView(searchIcon)
        binding.topContainer.addView(profileImage)
        binding.greet.setText("Hi User!")
    }

    private fun setupTabs() {
        // Add tabs
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Now Playing"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Trending"))

        // Set tab listener
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                when (currentTab) {
                    0 -> {
                        viewModel.fetchMovies(category = MOVIE_CATEGORY.NOW_PLAYING)
                        viewModel.category = MOVIE_CATEGORY.NOW_PLAYING
                    }

                    1 -> {
                        viewModel.fetchMovies(category = MOVIE_CATEGORY.TRENDING)
                        viewModel.category = MOVIE_CATEGORY.TRENDING
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        // Define card click listener to reuse across renderers
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
        adapter.registerRenderer(ViewTypes.HORIZONTAL_LIST, HorizontalListRenderer(onCardClick))
        adapter.registerRenderer(ViewTypes.LOADER_ITEM, LoaderItemRenderer())
        adapter.registerRenderer(ViewTypes.ERROR_ITEM, ErrorItemRenderer())

        // Setup RecyclerView
        val layoutManager = LinearLayoutManager(this)
        binding.rv.layoutManager = layoutManager
        binding.rv.adapter = adapter

        // Add item decoration for spacing between cards
        val spacingInPixels = dpToPx(16)
        binding.rv.addItemDecoration(SpacingItemDecoration(spacingInPixels))

        // Add scroll listener for pagination
        binding.rv.addOnScrollListener(object :
            androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: androidx.recyclerview.widget.RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)

                // Check if scrolling down
                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Load more when reaching the last 5 items
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5
                        && firstVisibleItemPosition >= 0
                    ) {
                        loadMoreMovies()
                    }
                }
            }
        })
    }

    private fun loadMoreMovies() {
        when (currentTab) {
            0 -> viewModel.loadMoreMovies(category = MOVIE_CATEGORY.NOW_PLAYING)
            1 -> viewModel.loadMoreMovies(category = MOVIE_CATEGORY.TRENDING)
        }
    }

    private fun observeViewModel() {
        // Observe now playing movies
        viewModel.result.observe(this) { movies ->
            if (movies.isNotEmpty()) {
                val items = getRvMovieData(movies).toMutableList()
                
                // Add loader item if there are more pages
                if (viewModel.hasMorePages()) {
                    items.add(ViewItem(ViewTypes.LOADER_ITEM, LoaderItem()))
                }
                
                adapter.setItems(items)
                hideError()
            }
        }

        viewModel.appendResult.observe(this) { movies ->
            if (movies.isNotEmpty()) {
                // Remove loader/error item before adding new movies
                val lastItem = adapter.getLastItem()
                if (lastItem?.viewType == ViewTypes.LOADER_ITEM || lastItem?.viewType == ViewTypes.ERROR_ITEM) {
                    adapter.removeLastItem()
                }
                
                val newItems = getRvMovieData(movies).toMutableList()
                
                // Add loader item if there are more pages
                if (viewModel.hasMorePages()) {
                    newItems.add(ViewItem(ViewTypes.LOADER_ITEM, LoaderItem()))
                }
                
                adapter.addItems(newItems)
            }
        }

        // Observe pagination loading state
        viewModel.isPaginationLoading.observe(this) { isLoading ->
            if (isLoading) {
                // Show loader at the end if not already present
                val lastItem = adapter.getLastItem()
                if (lastItem?.viewType != ViewTypes.LOADER_ITEM) {
                    adapter.addItem(ViewItem(ViewTypes.LOADER_ITEM, LoaderItem()))
                }
            }
        }

        // Observe pagination errors
        viewModel.paginationError.observe(this) { error ->
            error?.let {
                // Replace loader with error item
                val lastItem = adapter.getLastItem()
                if (lastItem?.viewType == ViewTypes.LOADER_ITEM) {
                    adapter.updateLastItem(
                        ViewItem(
                            ViewTypes.ERROR_ITEM,
                            ErrorItem(
                                message = it,
                                onRetry = { loadMoreMovies() }
                            )
                        )
                    )
                }
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.rv.visibility = View.GONE
                binding.errorView.root.visibility = View.GONE
                binding.loader.visibility = View.VISIBLE
            } else {
                binding.loader.visibility = View.GONE
            }
        }

        // Observe errors
        viewModel.error.observe(this) { error ->
            error?.let {
                showError(it)
            }
        }

        // Setup retry button
        binding.errorView.retryButton.setOnClickListener {
            retryFetch()
        }
    }

    private fun showError(message: String) {
        binding.rv.visibility = View.GONE
        binding.errorView.root.visibility = View.VISIBLE
        binding.errorView.errorMessage.text = message
    }

    private fun hideError() {
        binding.errorView.root.visibility = View.GONE
        binding.rv.visibility = View.VISIBLE
    }

    private fun retryFetch() {
        when (currentTab) {
            0 -> viewModel.fetchMovies(category = MOVIE_CATEGORY.NOW_PLAYING)
            1 -> viewModel.fetchMovies(category = MOVIE_CATEGORY.TRENDING)
        }
    }

    private fun getRvMovieData(movies: List<com.popcornpicks.home.data.model.Movie>): List<ViewItem<*>> {
        // Convert movies to CardItems
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
        return cardItems
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
