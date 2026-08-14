package se.supernovait.doobypro.domain.model

import se.supernovait.app.core.domain.model.location.Address

/**
 * Domain model representing a company profile.
 *
 * @property id The unique identifier for the company. Null for unsaved companies.
 * @property legalName The official registered name of the company.
 * @property displayName The name used for public display or branding.
 * @property licenseNumber The regulatory or trade license number.
 * @property phoneNumber The primary contact phone number.
 * @property email The primary contact email address.
 * @property address The physical address of the company.
 * @property logoUrl The URL or path to the company's logo image.
 */
data class Company(
    val id: String? = null,
    val legalName: String,
    val displayName: String,
    val licenseNumber: String,
    val phoneNumber: String,
    val email: String,
    val address: Address? = null,
    val logoUrl: String? = null
)
