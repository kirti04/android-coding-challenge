package com.syftapp.codetest.posts

import com.syftapp.codetest.data.model.domain.Post
import com.syftapp.codetest.postdetail.PostDetailScreenState
import com.syftapp.codetest.rules.RxSchedulerRule
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Single
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostsPresenterTest {

    @get:Rule
    val rxRule = RxSchedulerRule()

    @MockK
    lateinit var getPostsUseCase: GetPostsUseCase

    @RelaxedMockK
    lateinit var view: PostsView

    private val anyPost = Post(1, 1, "title", "body")

    private val sut by lazy {
        PostsPresenter(getPostsUseCase)
    }

    @Before
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun `binding loads posts`() {
        every { getPostsUseCase.execute(page = 1) } returns Single.just(listOf(anyPost))
        val slot = slot<PostScreenState.DataAvailable>()

        sut.bind(view, 1)

        verifyOrder {
            view.render(any<PostScreenState.Loading>())
            view.render(capture(slot))
            view.render(any<PostScreenState.FinishedLoading>())
        }
        assertEquals(listOf(anyPost), slot.captured.posts)
    }

    @Test
    fun `error on binding shows error state after loading`() {
        every { getPostsUseCase.execute(page = 1) } returns Single.error(Throwable())

        sut.bind(view, 1)

        verifyOrder {
            view.render(any<PostScreenState.Loading>())
            view.render(any<PostScreenState.Error>())
            view.render(any<PostScreenState.FinishedLoading>())
        }
    }

    @Test
    fun `binding show details`() {
        every { getPostsUseCase.execute(page = 1) } returns Single.just(listOf(anyPost))
        sut.bind(view, 1)

        sut.showDetails(anyPost)

        verifyOrder {
            view.render(any<PostScreenState.Loading>())
            view.render(any<PostScreenState.DataAvailable>())
            view.render(any<PostScreenState.FinishedLoading>())
            view.render(any<PostScreenState.PostSelected>())
        }
    }
}
