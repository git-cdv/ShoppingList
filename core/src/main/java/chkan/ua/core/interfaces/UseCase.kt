package chkan.ua.core.interfaces

interface UseCase {
    fun getErrorReason() : String
}

interface SuspendUseCase<T> : UseCase {
    suspend fun run(config: T) : Any
    override fun getErrorReason() : String
}

interface FlowUseCase : UseCase {
    fun run() : Any
    override fun getErrorReason() : String
}