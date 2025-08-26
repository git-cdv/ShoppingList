package chkan.ua.domain

interface Logger {
    fun d(tag: String, message: String)
    fun e(e: Exception, message: String? = null)
}
