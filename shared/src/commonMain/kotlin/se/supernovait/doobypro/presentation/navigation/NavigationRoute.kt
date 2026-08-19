package se.supernovait.doobypro.presentation.navigation

import org.jetbrains.compose.resources.StringResource

interface NavigationRoute {
    val name: String
        get() = this::class.qualifiedName ?: ""
    val label: StringResource?
        get() = null
    val param: String?
        get() = null
    val isTopLevel: Boolean
        get() = true
    val showTopBar: Boolean
        get() = true
    val showBottomBar: Boolean
        get() = true
}
