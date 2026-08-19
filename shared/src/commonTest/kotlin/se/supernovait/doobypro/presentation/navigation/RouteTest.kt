package se.supernovait.doobypro.presentation.navigation

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.navigation_item_dashboard_label
import doobypro.shared.generated.resources.navigation_item_orders_label
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RouteTest {

    @Test
    fun `parse should return correct route object for simple names`() {
        assertEquals(Route.Welcome, Route.parse(Route.Welcome.name))
        assertEquals(Route.Dashboard, Route.parse(Route.Dashboard.name))
        assertEquals(Route.Orders, Route.parse(Route.Orders.name))
        assertEquals(Route.Account, Route.parse(Route.Account.name))
        assertEquals(Route.Settings, Route.parse(Route.Settings.name))
    }

    @Test
    fun `parse should return correct route object for routes with parameters`() {
        // Compose Navigation format: qualified.name/id
        val orderDetailsRoute = "${Route.OrderDetails("").name}/order-123"
        val parsed = Route.parse(orderDetailsRoute)
        
        assertIs<Route.OrderDetails>(parsed)
    }

    @Test
    fun `parse should return default route for unknown strings`() {
        assertEquals(Route.Welcome, Route.parse("unknown.route"))
        assertEquals(Route.Dashboard, Route.parse("unknown.route", defaultRoute = Route.Dashboard))
    }

    @Test
    fun `name property should return qualified class name`() {
        assertEquals(Route.Welcome::class.qualifiedName, Route.Welcome.name)
        assertEquals(Route.OrderDetails::class.qualifiedName, Route.OrderDetails("1").name)
    }

    @Test
    fun `route labels should be correct`() {
        assertEquals(Res.string.navigation_item_dashboard_label, Route.Dashboard.label)
        assertEquals(Res.string.navigation_item_orders_label, Route.Orders.label)
    }

    @Test
    fun `isTopLevel property should be correct for main routes`() {
        assertEquals(true, Route.Dashboard.isTopLevel)
        assertEquals(true, Route.Orders.isTopLevel)
        assertEquals(true, Route.Services.isTopLevel)
    }

    @Test
    fun `isTopLevel property should be false for detail routes`() {
        assertEquals(false, Route.OrderDetails("").isTopLevel)
        assertEquals(false, Route.ServiceDetails("").isTopLevel)
    }
}
