
package com.popcornpicks.home.adapter.renderers

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.popcornpicks.home.R
import com.popcornpicks.home.adapter.MultiViewAdapter
import com.popcornpicks.home.adapter.ViewItem
import com.popcornpicks.home.adapter.ViewRenderer
import com.popcornpicks.home.adapter.ViewTypes
import com.popcornpicks.home.models.CardItem
import com.popcornpicks.home.models.HorizontalListSection

/**
 * Renderer for horizontal list sections
 * Contains a horizontal RecyclerView with cards
 * @param onItemClick Lambda function to handle card item clicks
 */
class HorizontalListRenderer(
    private val onItemClick: (CardItem) -> Unit
) : ViewRenderer<HorizontalListSection, HorizontalListRenderer.HorizontalListViewHolder>() {

    override fun getLayoutRes(): Int = R.layout.item_horizontal_list

    override fun createViewHolder(parent: ViewGroup): HorizontalListViewHolder {
        return HorizontalListViewHolder(inflate(parent, getLayoutRes()))
    }

    override fun bindView(holder: HorizontalListViewHolder, item: HorizontalListSection, position: Int) {
        holder.bind(item, onItemClick)
    }

    class HorizontalListViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val tvSectionTitle: TextView = itemView.findViewById(R.id.tv_section_title)
        private val rvHorizontal: RecyclerView = itemView.findViewById(R.id.rv_horizontal)
        private val horizontalAdapter = MultiViewAdapter()

        init {
            // Setup horizontal RecyclerView
            rvHorizontal.layoutManager = LinearLayoutManager(
                itemView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            rvHorizontal.adapter = horizontalAdapter
        }

        fun bind(item: HorizontalListSection, onItemClick: (CardItem) -> Unit) {
            tvSectionTitle.text = item.sectionTitle
            
            // Register card renderer with click listener for the horizontal list
            horizontalAdapter.registerRenderer(ViewTypes.CARD_ITEM, CardItemRenderer(onItemClick))
            
            // Convert CardItems to ViewItems and set to adapter
            val viewItems = item.items.map { cardItem ->
                ViewItem(ViewTypes.CARD_ITEM, cardItem)
            }
            horizontalAdapter.setItems(viewItems)
        }
    }
}
