
package io.github.hrdthinkr.devhelper.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.hrdthinkr.devhelper.presentation.screen.home.HomeScreen
import io.github.hrdthinkr.devhelper.presentation.screen.json.JsonToKotlinScreen

object Routes {
    const val HOME = "home"
    const val JSON_TO_KOTLIN = "json_to_kotlin"
}

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onToolClick = { toolId ->
                    when (toolId) {
                        "json_to_kotlin" -> {
                            navController.navigate(
                                Routes.JSON_TO_KOTLIN
                            )
                        }
                    }
                }
            )
        }

        composable(Routes.JSON_TO_KOTLIN) {
            JsonToKotlinScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
