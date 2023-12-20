package com.micudasoftware.security.di

import com.micudasoftware.security.hashing.HashingService
import com.micudasoftware.security.hashing.SHA256HashingService
import com.micudasoftware.security.token.JwtTokenService
import com.micudasoftware.security.token.TokenService
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val securityModule = module {
    singleOf(::JwtTokenService) { bind<TokenService>() }
    singleOf(::SHA256HashingService) { bind<HashingService>() }
}