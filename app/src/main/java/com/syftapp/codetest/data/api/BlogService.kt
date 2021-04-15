package com.syftapp.codetest.data.api

import com.syftapp.codetest.data.model.api.Comment
import com.syftapp.codetest.data.model.api.Post
import com.syftapp.codetest.data.model.api.User
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface BlogService {

    @GET("/users")
    fun getUsers(): Single<List<User>>

    @GET("/comments")
    fun getComments(): Single<List<Comment>>

    /*Passing page and limit query allows to load less data at one time and hence improved and quicker response.
    This allows us to provide better user experience and we always have something to display when user opens the app in short interval of time.
    Good pagination will help us with both to reduce the weight of the page we serve to the client and the query charge on the database.
    For more flexibility we can pass page limit from presenter/activity as well to allow customize the number of data loaded.*/
    @GET("/posts")
    fun getPosts(
        @Query("_page") page: String,
        @Query("_limit") limit: String = "10"
    ): Single<List<Post>>

    companion object {
        fun createService(retrofit: Retrofit): BlogService =
            retrofit.create(BlogService::class.java)
    }

}