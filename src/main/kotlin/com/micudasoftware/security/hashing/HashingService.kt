package com.micudasoftware.security.hashing

/**
 * Defines an interface for generating salted hashes and verifying passwords against stored salted hashes.
 */
interface HashingService {

    /**
     * Generates a salted hash for the provided value, using a random salt of the specified length.
     *
     * @param value The input data to be hashed.
     * @param saltLength The length of the random salt to be used.
     * @return The generated `SaltedHash` object.
     */
    fun generateSaltedHash(value: String, saltLength: Int = 32): SaltedHash

    /**
     * Verifies whether the provided value matches the stored salted hash.
     *
     * @param value The input data to be verified.
     * @param saltedHash The stored `SaltedHash` object containing the hash value and salt.
     * @return `true` if the value matches the stored salted hash, `false` otherwise.
     */
    fun verify(value: String, saltedHash: SaltedHash): Boolean
}
