
package com.popcornpicks.home.adapter.renderers

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.popcornpicks.home.R
import com.popcornpicks.home.adapter.ViewRenderer
import com.popcornpicks.home.models.LoaderItem

/**
 * Renderer for loader item in pagination
 */
class LoaderItemRenderer : ViewRenderer<LoaderItem, LoaderItemRenderer.LoaderViewHolder>() {

    override fun getLayoutRes(): Int = R.layout.item_loader

    override fun createViewHolder(parent: ViewGroup): LoaderViewHolder {
        return LoaderViewHolder(inflate(parent, getLayoutRes()))
    }

    override fun bindView(holder: LoaderViewHolder, item: LoaderItem, position: Int) {
        // Nothing to bind, just showing the loader
    }

    class LoaderViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView)
}
