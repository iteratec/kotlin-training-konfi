package client.model

sealed class Response<T> {
    class Success<T>(val value: T) : Response<T>()
    class Error<T>(val error: Throwable) : Response<T>()

    companion object {
        suspend operator fun <T> invoke(block: suspend () -> T): Response<T> {
            return try {
                Success(block())
            } catch (ex: Throwable) {
                Error(ex)
            }
        }
    }
}