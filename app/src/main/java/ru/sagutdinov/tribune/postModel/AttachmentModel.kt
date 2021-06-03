package ru.sagutdinov.tribune.postModel

class AttachmentModel(val id: String) {
    val url
        get() = "$BASE_URL/api/v1/static/$id"
}