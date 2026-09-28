package com.azg.alf_002_qrscan.widget

import android.net.Uri
import android.os.Build
import android.os.Parcelable
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import androidx.core.net.toUri


private fun NavController.isSameDestination(targetRoute: String): Boolean {
    val currentRoute = currentBackStackEntry?.destination?.route ?: return false
    val currentBase = currentRoute.substringBefore("?")
    val targetBase = targetRoute.substringBefore("?")
    return currentBase == targetBase
}
fun NavController.navigationTo(
    route: String,
    vararg args: Pair<String, Any?>
) {
    if (isSameDestination(route)) return

    val navOptions = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setRestoreState(true)
        .setPopUpTo(
            graph.findStartDestination().id,
            inclusive = false,
            saveState = true
        )
        .build()

    if (args.isEmpty()) {
        navigate(route, navOptions)
    } else {
        val uriBuilder = route.toUri().buildUpon()
        args.forEach { (key, value) ->
            value?.let { uriBuilder.appendQueryParameter(key, it.toString()) }
        }
        val deepLinkUri = uriBuilder.build()

        navigate(deepLinkUri, navOptions, null)
    }
}



//fun NavController.navigateTo(
//    route: String,
//    vararg args: Pair<String, Any?>
//) {
//    if (isSameDestination(route)) return
//
//    val uriBuilder = route.toUri().buildUpon()
//    args.forEach { (key, value) ->
//        value?.let { uriBuilder.appendQueryParameter(key, it.toString()) }
//    }
//    val deepLinkUri = uriBuilder.build()
//
//    val navOptions = NavOptions.Builder()
//        .setLaunchSingleTop(true)
//        .setRestoreState(true)
//        .setPopUpTo(
//            graph.findStartDestination().id,
//            inclusive = false,
//            saveState = true
//        )
//        .build()
//
//    navigate(deepLinkUri, navOptions)
//}


inline fun <reified T> NavBackStackEntry.getResult(key: String): T? {
    return savedStateHandle.get<T>(key)
}

inline fun <reified T> NavBackStackEntry.getData(key: String): T? {
    val args = arguments ?: return null
    if (!args.containsKey(key)) return null

    @Suppress("UNCHECKED_CAST")
    return when (T::class) {
        String::class -> args.getString(key) as T?
        Int::class -> args.getInt(key) as T
        Long::class -> args.getLong(key) as T
        Boolean::class -> args.getBoolean(key) as T
        Float::class -> args.getFloat(key) as T
        Double::class -> args.getDouble(key) as T
        else -> {
            when {
                Parcelable::class.java.isAssignableFrom(T::class.java) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        args.getParcelable(key, T::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        args.getParcelable(key) as? T
                    }
                }
                Serializable::class.java.isAssignableFrom(T::class.java) -> {
                    @Suppress("UNCHECKED_CAST", "DEPRECATION")
                    args.getSerializable(key) as? T
                }
                else -> {
                    @Suppress("DEPRECATION")
                    val raw = args.get(key)
                    raw as? T
                }
            }
        }
    }
}

inline fun <reified T> NavBackStackEntry.requireData(key: String): T =
    getData<T>(key) ?: error("Missing or invalid argument for key '$key'")

fun NavGraphBuilder.animComposable(
    route: String,
    duration: Int = 300,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) = composable(
    arguments = arguments,
    deepLinks = deepLinks,
    route = route,
    enterTransition = {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(durationMillis = duration)
        )
    },
    exitTransition = {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(durationMillis = duration)
        )
    },
    popEnterTransition = {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(durationMillis = duration)
        )
    },
    popExitTransition = {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(durationMillis = duration)
        )
    }
) { backStackEntry ->
    content(backStackEntry)
}
