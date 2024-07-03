package com.micudasoftware.plugins

import com.micudasoftware.api.image.image
import com.micudasoftware.api.recipe.latestRecipes
import com.micudasoftware.api.recipe.rateRecipe
import com.micudasoftware.api.recipe.recipe
import com.micudasoftware.api.user.authenticate
import com.micudasoftware.api.user.login
import com.micudasoftware.api.user.signUp
import com.micudasoftware.api.userprofile.userProfile
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        signUp()
        login()
        authenticate()

        userProfile()
        image()
//        staticFiles("/image", File("data"))

        recipe()
        latestRecipes()
        rateRecipe()
    }
}
