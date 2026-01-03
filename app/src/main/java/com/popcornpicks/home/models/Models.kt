package com.popcornpicks.home.models

data class TextItem(
    val title: String,
    val subtitle: String
)

data class CardItem(
    val id: Int,
    val title: String,
    val imageUrl: String? = null
)

data class HorizontalListSection(
    val sectionTitle: String,
    val items: List<CardItem>
)

data class LoaderItem(
    val isLoading: Boolean = true
)

data class ErrorItem(
    val message: String,
    val onRetry: (() -> Unit)? = null
)
