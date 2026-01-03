
package com.popcornpicks.home.adapter.renderers

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.popcornpicks.home.R
import com.popcornpicks.home.adapter.ViewRenderer
import com.popcornpicks.home.models.TextItem

/**
 * Renderer for text items
 */
class TextItemRenderer : ViewRenderer<TextItem, TextItemRenderer.TextViewHolder>() {

    override fun getLayoutRes(): Int = R.layout.item_text

    override fun createViewHolder(parent: ViewGroup): TextViewHolder {
        return TextViewHolder(inflate(parent, getLayoutRes()))
    }

    override fun bindView(holder: TextViewHolder, item: TextItem, position: Int) {
        holder.bind(item)
    }

    class TextViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvSubtitle: TextView = itemView.findViewById(R.id.tv_subtitle)

        fun bind(item: TextItem) {
            tvTitle.text = item.title
            tvSubtitle.text = item.subtitle
        }
    }
}
