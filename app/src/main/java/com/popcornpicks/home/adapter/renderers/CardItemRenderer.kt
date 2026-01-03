
package com.popcornpicks.home.adapter.renderers

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.popcornpicks.home.R
import com.popcornpicks.home.adapter.ViewRenderer
import com.popcornpicks.home.models.CardItem
import com.popcornpicks.home.utils.ImageUtils

/**
 * Renderer for card items in horizontal list
 * @param onItemClick Lambda function to handle card item clicks
 */
class CardItemRenderer(
    private val onItemClick: (CardItem) -> Unit
) : ViewRenderer<CardItem, CardItemRenderer.CardViewHolder>() {

    override fun getLayoutRes(): Int = R.layout.item_card

    override fun createViewHolder(parent: ViewGroup): CardViewHolder {
        return CardViewHolder(inflate(parent, getLayoutRes()))
    }

    override fun bindView(holder: CardViewHolder, item: CardItem, position: Int) {
        holder.bind(item, onItemClick)
    }

    class CardViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val tvCardTitle: TextView = itemView.findViewById(R.id.tv_card_title)
        private val cardImage: ImageView = itemView.findViewById(R.id.card_image)

        fun bind(item: CardItem, onItemClick: (CardItem) -> Unit) {
            tvCardTitle.text = item.title
            // Load image using the ImageUtils utility function
            ImageUtils.loadImage(cardImage, item.imageUrl)
            
            // Set click listener using the passed lambda
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
