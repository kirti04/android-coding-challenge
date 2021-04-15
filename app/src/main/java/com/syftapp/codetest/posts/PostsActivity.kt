package com.syftapp.codetest.posts

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.syftapp.codetest.Navigation
import com.syftapp.codetest.R
import com.syftapp.codetest.data.model.domain.Post
import kotlinx.android.synthetic.main.activity_posts.*
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent

class PostsActivity : AppCompatActivity(), PostsView, KoinComponent {

    private val presenter: PostsPresenter by inject()
    private lateinit var navigation: Navigation
    private lateinit var adapter: PostsAdapter
    private var currentPage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
        navigation = Navigation(this)

        initView()
        initListener()
    }

    private fun initView() {
        swipeToRefresh.setOnRefreshListener { presenter.onRefresh() }
        listOfPosts.layoutManager = LinearLayoutManager(this)
        val separator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        listOfPosts.addItemDecoration(separator)
        bindView()
    }

    private fun initListener() {
        nestedScrollView.setOnScrollChangeListener(object : PaginationListener() {
            override fun loadMore() {
                bindView()
            }
        })
    }

    private fun bindView() {
        presenter.bind(this, ++currentPage)
    }

    override fun onDestroy() {
        presenter.unbind()
        super.onDestroy()
    }

    override fun render(state: PostScreenState) {
        when (state) {
            is PostScreenState.Loading -> showLoading()
            is PostScreenState.DataAvailable -> showPosts(state.posts)
            is PostScreenState.Error -> showError(getString(R.string.load_posts_error_message))
            is PostScreenState.FinishedLoading -> hideLoading()
            is PostScreenState.PostSelected -> navigation.navigateToPostDetail(state.post.id)
        }
    }

    private fun showLoading() {
        error.visibility = View.GONE
        listOfPosts.visibility = View.GONE
        if (!swipeToRefresh.isRefreshing)
            loading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loading.visibility = View.GONE
    }

    private fun showPosts(posts: List<Post>) {
        // this is a fairly crude implementation, if it was Flowable, it would
        // be better to use DiffUtil and consider notifyRangeChanged, notifyItemInserted, etc
        // to preserve animations on the RecyclerView
        adapter = PostsAdapter(presenter)
        adapter.update(posts)
        listOfPosts.adapter = adapter
        listOfPosts.visibility = View.VISIBLE
        swipeToRefresh.isRefreshing = false
    }

    private fun showError(message: String) {
        swipeToRefresh.isRefreshing = false
        error.visibility = View.VISIBLE
        error.text = message
    }
}
