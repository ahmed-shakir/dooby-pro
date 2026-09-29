package se.supernovait.doobypro.presentation.support.tab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_support_faq_no_match
import doobypro.shared.generated.resources.screen_support_faq_search_placeholder
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.domain.model.faq.StandardFAQCategory
import se.supernovait.app.core.ui.component.SupernovaEmptyState
import se.supernovait.app.core.ui.component.input.SupernovaSearchField
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.support.faq.DoobyFAQCategory
import se.supernovait.doobypro.domain.model.support.faq.FAQData
import se.supernovait.doobypro.presentation.support.SupportEvent
import se.supernovait.doobypro.presentation.support.SupportState

@Composable
fun FAQTab(
    uiState: SupportState,
    onEvent: (SupportEvent) -> Unit
) {
    var searchQuery by remember { mutableStateOf(uiState.searchQuery) }
    val filteredFaqs = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            FAQData.faqs
        } else {
            FAQData.faqs.filter {
                it.question.contains(searchQuery, ignoreCase = true) || it.answer.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val groupedFaqs = filteredFaqs.groupBy { it.category }

    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        SupernovaSearchField(
            value = searchQuery,
            onValueChange = { value ->
                searchQuery = value
                onEvent(SupportEvent.UpdateSearchQuery(value))
            },
            onSearch = {},
            placeholder = stringResource(Res.string.screen_support_faq_search_placeholder),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

        if (groupedFaqs.isEmpty()) {
            SupernovaEmptyState(titleRes = Res.string.screen_support_faq_no_match)
        } else {
            groupedFaqs.forEach { (category, faqs) ->
                val categoryName = when (category) {
                    is StandardFAQCategory -> category.name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }
                    is DoobyFAQCategory -> category.name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }
                    else -> category.toString()
                }

                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                    SupernovaLabel(
                        text = category.label?.let { stringResource(it) } ?: categoryName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = MaterialTheme.spacing.extraSmall)
                    )

                    faqs.forEach { faq ->
                        var expanded by remember { mutableStateOf(false) }
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = !expanded }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                            ) {
                                SupernovaLabel(
                                    text = faq.question,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                AnimatedVisibility(
                                    visible = expanded,
                                    enter = fadeIn() + expandVertically(),
                                    exit = fadeOut() + shrinkVertically()
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                                        HorizontalDivider()
                                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
                                        SupernovaLabel(
                                            text = faq.answer,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
