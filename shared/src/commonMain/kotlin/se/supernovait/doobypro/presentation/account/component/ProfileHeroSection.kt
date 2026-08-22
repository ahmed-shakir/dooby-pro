package se.supernovait.doobypro.presentation.account.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.label_role_admin
import doobypro.shared.generated.resources.label_role_user
import doobypro.shared.generated.resources.label_user_default
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.domain.auth.UserRole
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.presentation.account.AccountState

@Composable
fun ProfileHeroSection(state: AccountState) {
    val user = state.account?.user
    val company = state.account?.company

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
            .padding(vertical = 32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            val initials = buildString {
                if (user != null) {
                    append(user.firstname.firstOrNull()?.uppercaseChar() ?: "")
                    append(user.lastname.firstOrNull()?.uppercaseChar() ?: "")
                }
            }.takeIf { it.isNotEmpty() } ?: "U"

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
            ) {
                SupernovaLabel(
                    text = initials,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            // Name
            SupernovaLabel(
                text = user?.name ?: stringResource(Res.string.label_user_default),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall
            )

            // Designation subtitle
            if (user != null && company != null) {
                val roleName = when (user.role) {
                    UserRole.Admin -> stringResource(Res.string.label_role_admin)
                    UserRole.User -> stringResource(Res.string.label_role_user)
                }
                SupernovaLabel(
                    text = "$roleName at ${company.displayName}",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
