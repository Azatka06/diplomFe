package ru.sagutdinov.tribune.postModel

import android.graphics.Bitmap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.sagutdinov.tribune.BuildConfig
import ru.sagutdinov.tribune.api.API
import ru.sagutdinov.tribune.api.AuthRequestParams
import ru.sagutdinov.tribune.api.InjectAuthTokenInterceptor
import ru.sagutdinov.tribune.api.RegistrationRequestParams
import java.io.ByteArrayOutputStream


const val BASE_URL = "https://sagutdinov-tribune.herokuapp.com/"


object Repository {

    private var retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    private var api: API = retrofit.create(API::class.java)


    suspend fun authenticate(username: String, password: String) =
        api.authenticate(AuthRequestParams(username, password))

    suspend fun register(username: String, password: String) =
        api.register(RegistrationRequestParams(username, password))

    suspend fun getPostsBefore(idPost: Long) = api.getPostsBefore(idPost)

    suspend fun getRecent() = api.getRecent()

    suspend fun pressPostUp(idPost: Long) = api.pressPostUp(idPost)

    suspend fun pressPostDown(idPost: Long) = api.pressPostDown(idPost)

    suspend fun getReactionByUsers(idPost: Long) = api.getReactionByUsers(idPost)

    suspend fun getPostsOfMe() = api.getPostsOfMe()

    suspend fun getPostsOfUser(username: String) = api.getPostsOfUser(username)

    suspend fun upload(bitmap: Bitmap): Response<AttachmentModel> {
        // Создаем поток байтов
        val bos = ByteArrayOutputStream()
        // Помещаем Bitmap в качестве JPEG в этот поток
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val reqFIle =
            // Создаем тип медиа и передаем массив байтов с потока
            RequestBody.create("image/jpeg".toMediaTypeOrNull(), bos.toByteArray())
        val body =
        // Создаем multipart объект, где указываем поле, в котором
            // содержатся посылаемые данные, имя файла и медиафайл
            MultipartBody.Part.createFormData("file", "image.jpg", reqFIle)
        return api.uploadImage(body)
    }

    suspend fun createPost(
        namePost: String,
        textPost: String,
        link: String,
        attachmentImage: String,
    ): Response<Void> {
        var attachmentLink: String? = link
        when {
            attachmentLink!!.isEmpty() -> {
                attachmentLink = null
            }
            !attachmentLink.contains("http") -> {
                attachmentLink = "https://$attachmentLink"
            }
        }
        val postRequestDto = PostRequestDto(
            postName = namePost,
            postText = textPost,
            link = attachmentLink,
            attachmentImage = attachmentImage
        )
        return api.createPost(postRequestDto)
    }

    fun createRetrofitWithAuth(authToken: String) {
        val httpLoggerInterceptor = HttpLoggingInterceptor()
        httpLoggerInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
        val client = OkHttpClient.Builder()
            .addInterceptor(InjectAuthTokenInterceptor(authToken))
            .addInterceptor(httpLoggerInterceptor)
            .addInterceptor(interceptor)
            .build()
        retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(API::class.java)
    }

}