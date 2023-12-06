package com.micudasoftware.security.hashing

/**
 * Represents a salted hash, which is a combination of a hash value and the salt used to generate it.
 *
 * Salted hashing is a security technique that enhances the security of hash values by adding a random string of characters,
 * known as the salt, to the input before hashing. This helps to prevent attackers from pre-computing hash values
 * for common passwords or using rainbow table attacks.
 *
 * @param hash The hash value of the input data.
 * @param salt The salt used to generate the hash value.
 */
data class SaltedHash(
    val hash: String,
    val salt: String,
)
