package com.micudasoftware.common

/**
 * Interface representing base HTTP request.
 */
interface BaseRequest {

    /**
     * Function to validate request.
     *
     * @return The [Result] of the validation.
     */
    fun validate(): Result<Unit>
}