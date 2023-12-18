package com.micudasoftware.api.user

/**
 * Checks whether the provided email address is valid according to the [EMAIL_REGEX] regular expression.
 *
 * @param email The email address to be validated.
 * @return `true` if the email address is valid, `false` otherwise.
 */
fun isEmailValid(email: String) = Regex(EMAIL_REGEX).matches(email)

/**
 * Checks whether the provided password is valid according to the [PASSWORD_REGEX] regular expression.
 *
 * @param password The password to be validated.
 * @return `true` if the password is valid, `false` otherwise.
 */
fun isPasswordValid(password: String) = Regex(PASSWORD_REGEX).matches(password)

const val EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"
const val PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}\$"