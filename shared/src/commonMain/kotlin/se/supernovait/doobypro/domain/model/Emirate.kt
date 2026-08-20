package se.supernovait.doobypro.domain.model

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.emirate_abu_dhabi
import doobypro.shared.generated.resources.emirate_ajman
import doobypro.shared.generated.resources.emirate_dubai
import doobypro.shared.generated.resources.emirate_fujairah
import doobypro.shared.generated.resources.emirate_ras_al_khaimah
import doobypro.shared.generated.resources.emirate_sharjah
import doobypro.shared.generated.resources.emirate_umm_al_quwain
import org.jetbrains.compose.resources.StringResource

enum class Emirate(val value: String, val label: StringResource) {
    ABU_DHABI("Abu Dhabi", Res.string.emirate_abu_dhabi),
    DUBAI("Dubai", Res.string.emirate_dubai),
    SHARJAH("Sharjah", Res.string.emirate_sharjah),
    AJMAN("Ajman", Res.string.emirate_ajman),
    UMM_AL_QUWAIN("Umm Al Quwain", Res.string.emirate_umm_al_quwain),
    RAS_AL_KHAIMAH("Ras Al Khaimah", Res.string.emirate_ras_al_khaimah),
    FUJAIRAH("Fujairah", Res.string.emirate_fujairah);

    companion object {
        fun fromName(name: String): Emirate? = entries.find { it.name == name }
        fun fromValue(value: String): Emirate? = entries.find { it.value == value }
    }
}
