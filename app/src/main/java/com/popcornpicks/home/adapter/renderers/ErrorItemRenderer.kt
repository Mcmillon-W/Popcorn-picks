
package com.popcornpicks.home.adapter.renderers

import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.popcornpicks.home.R
import com.popcornpicks.home.adapter.ViewRenderer
import com.popcornpicks.home.models.ErrorItem

/**
 * Renderer for error item in pagination
 */
class ErrorItemRenderer : ViewRenderer<ErrorItem, ErrorItemRenderer.ErrorViewHolder>() {

    override fun getLayoutRes(): Int = R.layout.item_error

    override fun createViewHolder(parent: ViewGroup): ErrorViewHolder {
        return ErrorViewHolder(inflate(parent, getLayoutRes()))
    }

    override fun bindView(holder: ErrorViewHolder, item: ErrorItem, position: Int) {
        holder.bind(item)
    }

    class ErrorViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val tvErrorMessage: TextView = itemView.findViewById(R.id.tv_error_message)
        private val btnRetry: Button = itemView.findViewById(R.id.btn_retry)

        fun bind(item: ErrorItem) {
            tvErrorMessage.text = item.message
            btnRetry.setOnClickListener {
                item.onRetry?.invoke()
            }
        }
    }
}
