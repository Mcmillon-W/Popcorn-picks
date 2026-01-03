
package com.popcornpicks.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MultiViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val items = mutableListOf<ViewItem<*>>()
    private val renderers = mutableMapOf<Int, ViewRenderer<*, *>>()

    fun <T> registerRenderer(viewType: Int, renderer: ViewRenderer<T, *>) {
        renderers[viewType] = renderer
    }

    fun setItems(newItems: List<ViewItem<*>>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItems(newItems: List<ViewItem<*>>) {
        val startPosition = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }

    fun addItem(item: ViewItem<*>) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    fun removeLastItem() {
        if (items.isNotEmpty()) {
            val lastPosition = items.size - 1
            items.removeAt(lastPosition)
            notifyItemRemoved(lastPosition)
        }
    }

    fun updateLastItem(item: ViewItem<*>) {
        if (items.isNotEmpty()) {
            val lastPosition = items.size - 1
            items[lastPosition] = item
            notifyItemChanged(lastPosition)
        }
    }

    fun getLastItem(): ViewItem<*>? {
        return if (items.isNotEmpty()) items.last() else null
    }
    
    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val renderer = renderers[viewType]
            ?: throw IllegalArgumentException("No renderer registered for viewType: $viewType")
        return renderer.createViewHolder(parent)
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        @Suppress("UNCHECKED_CAST")
        val renderer = renderers[item.viewType] as? ViewRenderer<Any, RecyclerView.ViewHolder>
            ?: throw IllegalArgumentException("No renderer registered for viewType: ${item.viewType}")
        item.data?.let { renderer.bindView(holder, it, position) }
    }
    
    override fun getItemCount(): Int = items.size
}

data class ViewItem<T>(
    val viewType: Int,
    val data: T
)
