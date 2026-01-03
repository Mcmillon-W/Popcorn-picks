
package com.popcornpicks.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class ViewRenderer<T, VH : RecyclerView.ViewHolder> {

    abstract fun getLayoutRes(): Int

    abstract fun createViewHolder(parent: ViewGroup): VH

    abstract fun bindView(holder: VH, item: T, position: Int)

    open fun onItemClick(holder: VH, item: T, position: Int) {}

    protected fun inflate(parent: ViewGroup, layoutRes: Int) =
        LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
}
