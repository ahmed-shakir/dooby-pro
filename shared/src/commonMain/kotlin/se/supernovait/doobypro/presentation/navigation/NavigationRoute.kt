package se.supernovait.doobypro.presentation.navigation

interface NavigationRoute {
    val name: String
        get() = this::class.qualifiedName ?: ""
    val param: String?
        get() = null
    val isTopLevel: Boolean
        get() = true
    val showTopBar: Boolean
        get() = true
    val showBottomBar: Boolean
        get() = true
}
