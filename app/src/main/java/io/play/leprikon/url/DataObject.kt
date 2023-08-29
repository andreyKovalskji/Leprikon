package io.play.leprikon.url


data class DataObject (
    val let_in: Boolean,
    val url: String?
) {
    fun contains(text: String): Boolean {
        return url?.contains(text) ?: false
    }
}