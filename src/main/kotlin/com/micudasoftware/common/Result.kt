package com.micudasoftware.common

import com.micudasoftware.common.Result.Error
import com.micudasoftware.common.Result.Success
import io.ktor.http.*
import kotlin.String
import kotlin.Unit

/**
 * [Result] is a sealed class that represents a value of one of two possible types
 * (a disjoint union). Instances of [Result] are either an instance of [Success] or [Error].
 *
 * @param T The type of the value.
 */
sealed class Result<T> {

    /**
     * Represents a successful operation.
     *
     * @param data The result of the successful operation.
     */
    data class Success<T>(
        val data: T,
    ) : Result<T>()

    /**
     * Represents a failed operation.
     *
     * @param code The HTTP status code of the error.
     * @param message The error message.
     */
    data class Error<T>(
        val code: HttpStatusCode = HttpStatusCode.InternalServerError,
        val message: String,
    ) : Result<T>()

    /**
     * Executes the given [block] function if this is a [Success].
     *
     * @param block The function to execute.
     * @return This result, to facilitate chaining.
     */
    suspend fun onSuccess(block: suspend (T) -> Unit): Result<T> {
        if (this is Success) {
            block.invoke(this.data)
        }
        return this
    }

    /**
     * Executes the given [block] function if this is an [Error].
     *
     * @param block The function to execute.
     * @return This result, to facilitate chaining.
     */
    suspend fun onError(block: suspend (HttpStatusCode, ErrorMessage) -> Unit): Result<T> {
        if (this is Error) {
            block.invoke(this.code, this.message)
        }
        return this
    }

    /**
     * Returns the encapsulated value if this is a [Success] or `null` otherwise.
     *
     * @return The encapsulated value if this is a [Success] or `null` otherwise.
     */
    fun getOrNull() : T? = (this as? Success)?.data
}

/**
 * [ErrorMessage] is a typealias for [String] used to represent error messages.
 */
typealias ErrorMessage = String