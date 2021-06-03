package ru.sagutdinov.tribune.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.sagutdinov.tribune.postModel.*

interface API {
    @POST("api/v1/authentication")
    suspend fun authenticate(@Body authRequestParams: AuthRequestParams): Response<Token>

    @POST("api/v1/registration")
    suspend fun register(@Body registrationRequestParams: RegistrationRequestParams): Response<Token>

    @POST("api/v1/posts")
    suspend fun createPost(@Body postRequestDto: PostRequestDto): Response<Void>

    @GET("api/v1/posts/me")
    suspend fun getPostsOfMe(): Response<List<Post>>

    @GET("api/v1/posts/username/{username}")
    suspend fun getPostsOfUser(@Path("username") username: String): Response<List<Post>>

    @GET("api/v1/posts/recent")
    suspend fun getRecent(): Response<List<Post>>

    @GET("api/v1/posts/{idPost}/get-posts-before")
    suspend fun getPostsBefore(@Path("idPost") idPost: Long): Response<List<Post>>

    @POST("api/v1/posts/{idPost}/up")
    suspend fun pressPostUp(@Path("idPost") idPost: Long): Response<Post>

    @POST("api/v1/posts/{idPost}/down")
    suspend fun pressPostDown(@Path("idPost") idPost: Long): Response<Post>

    @GET("api/v1/posts/{idPost}/reaction-by-users")
    suspend fun getReactionByUsers(@Path("idPost") idPost: Long): Response<List<UsersReactionModel>>

    @Multipart
    @POST("api/v1/media")
    suspend fun uploadImage(@Part file: MultipartBody.Part):
            Response<AttachmentModel>
}



