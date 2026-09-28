package se.supernovait.doobypro.domain.model.support

data class SupportRequest(
    val accountId: String? = null,
    val userId: String? = null,
    val userEmail: String,
    val userName: String,
    val userPhone: String? = null,
    val requestType: SupportRequestType,
    val subject: String,
    val message: String,
    val wantsCallback: Boolean = false,
    val appVersion: String = "",
    val buildNumber: String = "",
    val deviceDetails: String = ""
)
