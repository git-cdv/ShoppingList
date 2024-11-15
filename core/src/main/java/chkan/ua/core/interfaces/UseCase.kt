package chkan.ua.core.interfaces

interface UseCase<T> {
    fun getErrorReason(config: T? = null): String
}

interface SuspendUseCase<T> : UseCase<T> {
    suspend fun run(config: T) : Any
    override fun getErrorReason(config: T?) : String
}

interface FlowUseCase<T> : UseCase<T> {
    fun run(config: T) : Any
    override fun getErrorReason(config: T?) : String
}