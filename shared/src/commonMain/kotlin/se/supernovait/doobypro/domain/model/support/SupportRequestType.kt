package se.supernovait.doobypro.domain.model.support

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.support_request_type_bug_report
import doobypro.shared.generated.resources.support_request_type_other
import doobypro.shared.generated.resources.support_request_type_question
import doobypro.shared.generated.resources.support_request_type_suggestion
import org.jetbrains.compose.resources.StringResource

enum class SupportRequestType(val label: StringResource) {
    QUESTION(Res.string.support_request_type_question),
    SUGGESTION(Res.string.support_request_type_suggestion),
    BUG_REPORT(Res.string.support_request_type_bug_report),
    OTHER(Res.string.support_request_type_other)
}
