package chkan.ua.core.interfaces

interface UseCase<T> {
    suspend fun run(args: T) : Any
    fun getErrorReason() : String
}