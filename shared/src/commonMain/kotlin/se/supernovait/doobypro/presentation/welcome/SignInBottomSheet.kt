package se.supernovait.doobypro.presentation.welcome

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.sheet_SignIn_action_sign_in
import doobypro.shared.generated.resources.sheet_SignIn_field_username
import doobypro.shared.generated.resources.sheet_SignIn_subtitle
import doobypro.shared.generated.resources.sheet_SignIn_title
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.action.SupernovaButton
import se.supernovait.app.core.ui.component.input.SupernovaTextField
import se.supernovait.app.core.ui.component.modal.LocalBottomSheetState
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.text.SupernovaSubtitle
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.spacing

@Composable
fun SignInBottomSheet(
    showSignInForm: Boolean,
    isSigningIn: Boolean,
    signInError: String?,
    onSignIn: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val bottomSheetState = LocalBottomSheetState.current

    // Use rememberUpdatedState to ensure the content lambda captures the LATEST values
    // and correctly subscribes to state changes inside the bottom sheet's composition scope.
    val isSigningInState = rememberUpdatedState(isSigningIn)
    val signInErrorState = rememberUpdatedState(signInError)
    val onSignInState = rememberUpdatedState(onSignIn)

    LaunchedEffect(showSignInForm) {
        if (showSignInForm) {
            bottomSheetState.show {
                SignInForm(
                    isSigningIn = isSigningInState.value,
                    signInError = signInErrorState.value,
                    onSignIn = onSignInState.value
                )
            }
        } else {
            bottomSheetState.hide()
        }
    }

    LaunchedEffect(bottomSheetState.isVisible) {
        if (!bottomSheetState.isVisible && showSignInForm) {
            onDismiss()
        }
    }
}

@Composable
private fun SignInForm(
    isSigningIn: Boolean,
    signInError: String?,
    onSignIn: (String) -> Unit,
) {
    var username by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.large)
            .padding(bottom = MaterialTheme.spacing.x3Large)
    ) {
        SupernovaTitle(text = stringResource(Res.string.sheet_SignIn_title))
        SupernovaSubtitle(
            text = stringResource(Res.string.sheet_SignIn_subtitle),
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

        SupernovaTextField(
            label = stringResource(Res.string.sheet_SignIn_field_username),
            initialValue = username,
            onValueChange = { value, _ -> username = value },
            modifier = Modifier.fillMaxWidth()
        )

        if (signInError != null) {
            SupernovaLabel(
                text = signInError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = MaterialTheme.spacing.small, start = MaterialTheme.spacing.extraSmall)
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

        SupernovaButton(
            label = stringResource(Res.string.sheet_SignIn_action_sign_in),
            shape = MaterialTheme.shapes.extraSmall,
            onClick = { onSignIn(username) },
            loading = isSigningIn,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
