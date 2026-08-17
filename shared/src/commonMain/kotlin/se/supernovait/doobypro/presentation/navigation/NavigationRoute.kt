package se.supernovait.doobypro.presentation.navigation

interface NavigationRoute {
    val showTopBar: Boolean
        get() = true
    val showBottomBar: Boolean
        get() = true
    val param: String?
        get() = null
}
