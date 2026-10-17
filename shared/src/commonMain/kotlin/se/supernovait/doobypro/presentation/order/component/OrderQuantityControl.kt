package se.supernovait.doobypro.presentation.order.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.Style
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.order_quantity_control_a11y_decrease_quantity
import doobypro.shared.generated.resources.order_quantity_control_a11y_increase_quantity
import doobypro.shared.generated.resources.order_quantity_control_a11y_quantity
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.action.SupernovaTextAction
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing

/**
 * Quantity control component with an outline container and increment/decrement actions.
 *
 * @param quantity The current quantity value.
 * @param onQuantityChange Callback invoked when the quantity changes.
 * @param modifier Optional [Modifier] for layout adjustment.
 */
@Composable
fun OrderQuantityControl(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.labelSmallEmphasized
) {
    val decreaseLabel = stringResource(Res.string.order_quantity_control_a11y_decrease_quantity)
    val increaseLabel = stringResource(Res.string.order_quantity_control_a11y_increase_quantity)
    val quantityDescription = stringResource(Res.string.order_quantity_control_a11y_quantity, quantity)

    Box(
        modifier = modifier.semantics {
            contentDescription = quantityDescription
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium, Alignment.CenterHorizontally),
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.extraSmall)
                .padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.extraSmall)
        ) {
            SupernovaTextAction(
                label = "-",
                contentDescription = decreaseLabel,
                onClick = {
                    if (quantity > 1) {
                        onQuantityChange(quantity - 1)
                    }
                },
                textStyle = style
            )
            SupernovaLabel(
                text = quantity.toString(),
                style = style,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            SupernovaTextAction(
                label = "+",
                contentDescription = increaseLabel,
                onClick = {
                    onQuantityChange(quantity + 1)
                },
                textStyle = style
            )
        }
    }
}
