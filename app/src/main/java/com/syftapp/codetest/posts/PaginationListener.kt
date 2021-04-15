package com.syftapp.codetest.posts

import androidx.core.widget.NestedScrollView

/**
 * [PaginationListener]
 * PaginationListener is implemented with OnScrollChangeListener to listen to scroll changes and perform pagination  when
 * list reaches the last item in the recycler view
 */

abstract class PaginationListener : NestedScrollView.OnScrollChangeListener {

    override fun onScrollChange(
        view: NestedScrollView?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {

        val lastItemPosition = view?.getChildAt(0)?.measuredHeight?.minus(view.measuredHeight)
        if (scrollY == lastItemPosition)
            loadMore()
    }

    protected abstract fun loadMore()
}