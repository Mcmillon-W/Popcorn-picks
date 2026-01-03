
package com.popcornpicks.home.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * ItemDecoration to add spacing between RecyclerView items
 */
class SpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
    
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        
        val position = parent.getChildAdapterPosition(view)
        
        // Add spacing to all items
        outRect.left = spacing
        outRect.right = spacing
        outRect.bottom = spacing
        
        // Add top spacing to first item
        if (position == 0) {
            outRect.top = spacing
        }
    }
}
