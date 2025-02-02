package com.syftapp.codetest.posts

import com.syftapp.codetest.data.model.domain.Post
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent

class PostsPresenter(private val getPostsUseCase: GetPostsUseCase) : KoinComponent {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var view: PostsView

    fun bind(view: PostsView, page: Int) {
        this.view = view
        compositeDisposable.add(loadPosts(page))
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed)
            compositeDisposable.dispose()
    }

    fun showDetails(post: Post) {
        view.render(PostScreenState.PostSelected(post))
    }

    private fun loadPosts(page: Int) = getPostsUseCase.execute(page)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { view.render(PostScreenState.Loading) }
        .doAfterTerminate { view.render(PostScreenState.FinishedLoading) }
        .subscribe({
            view.render(PostScreenState.DataAvailable(it))
        }, {
            view.render(PostScreenState.Error(it))
        })

    fun onRefresh() {
        //when user swipes to refresh will always be at the top of the screen hence loading the page 1 in on refresh.
        // This will automatically load all the cached data.
        loadPosts(page = 1)
    }
}
