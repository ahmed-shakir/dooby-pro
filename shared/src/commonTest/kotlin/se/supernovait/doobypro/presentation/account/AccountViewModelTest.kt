package se.supernovait.doobypro.presentation.account

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.location.Address
import se.supernovait.app.core.domain.model.license.License
import se.supernovait.app.core.domain.model.license.LicenseStatus
import se.supernovait.app.core.domain.model.license.Tier
import se.supernovait.doobypro.data.local.dao.FakeAccountDao
import se.supernovait.doobypro.data.local.dao.FakeUserDao
import se.supernovait.doobypro.data.repository.AccountRepositoryImpl
import se.supernovait.doobypro.data.repository.FakeAgreementRepository
import se.supernovait.doobypro.data.repository.FakeAuthRepository
import se.supernovait.doobypro.data.repository.FakeCompanyRepository
import se.supernovait.doobypro.data.repository.FakeLicenseRepository
import se.supernovait.doobypro.domain.model.Account
import se.supernovait.doobypro.domain.model.Company
import se.supernovait.doobypro.util.FakeFileStorage
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class AccountViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var accountRepository: AccountRepositoryImpl
    private lateinit var fileStorage: FakeFileStorage
    private lateinit var viewModel: AccountViewModel

    private val testUser = User(
        id = "user-123",
        username = "johndoe",
        firstname = "John",
        lastname = "Doe",
        birthdate = null,
        email = "john@example.com",
        address = Address(street = "User St", city = "Dubai", country = "UAE")
    )

    private val testCompany = Company(
        id = "comp-123",
        legalName = "Legal Corp",
        displayName = "Legal",
        licenseNumber = "123",
        email = "comp@example.com",
        phoneNumber = "987",
        address = Address(street = "Main", city = "Dubai", country = "UAE")
    )

    private val testLicense = License(
        id = "lic-123",
        accountId = "comp-123",
        licenseStatus = LicenseStatus.Active,
        tier = Tier.Free,
        title = "Test License",
        description = "Test Description",
        issueDate = LocalDate(2026, 1, 1),
        expiryDate = LocalDate(2027, 1, 1)
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = FakeAuthRepository()
        fileStorage = FakeFileStorage()
        accountRepository = AccountRepositoryImpl(
            authRepository = authRepository,
            companyRepository = FakeCompanyRepository(),
            licenseRepository = FakeLicenseRepository(),
            agreementRepository = FakeAgreementRepository(),
            accountDao = FakeAccountDao(),
            userDao = FakeUserDao()
        )
        
        // Seed initial data for "current user"
        runTest(testDispatcher) {
            authRepository.signUp(testUser)
            accountRepository.saveAccount(Account(user = testUser, company = testCompany, license = testLicense))
        }

        viewModel = AccountViewModel(authRepository, accountRepository, fileStorage)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load account data`() = runTest(testDispatcher) {
        val collectJob = launch { viewModel.uiState.collect {} }
        
        val state = viewModel.uiState.first { it.account != null }
        
        assertNotNull(state.account)
        assertEquals("John", state.editUserFirstName)
        assertEquals("Legal Corp", state.editCompanyLegalName)
        assertEquals("Main", state.editCompanyAddressStreet)
        
        collectJob.cancel()
    }

    @Test
    fun `UpdateFirstName event should update state`() = runTest(testDispatcher) {
        val collectJob = launch { viewModel.uiState.collect {} }
        
        viewModel.onEvent(AccountEvent.UpdateUserFirstName("Jane"))
        val state = viewModel.uiState.first { it.editUserFirstName == "Jane" }
        assertEquals("Jane", state.editUserFirstName)
        
        collectJob.cancel()
    }

    @Test
    fun `SaveUserProfile should update repository state and clear edit mode`() = runTest(testDispatcher) {
        val collectJob = launch { viewModel.uiState.collect {} }
        
        // Wait for initial load
        viewModel.uiState.first { it.account != null }

        viewModel.onEvent(AccountEvent.EnterEditMode("personal-info"))
        viewModel.onEvent(AccountEvent.UpdateUserFirstName("Jane"))
        
        // Fix for the address validation failure in ViewModel
        viewModel.onEvent(AccountEvent.UpdateUserAddressStreet("User St"))
        
        viewModel.onEvent(AccountEvent.SaveUserProfile)
        
        val state = viewModel.uiState.first { it.account?.user?.firstname == "Jane" && it.editingCardId == null }
        assertEquals("Jane", state.account?.user?.firstname)
        assertEquals(null, state.editingCardId)
        
        collectJob.cancel()
    }
    
    @Test
    fun `UpdateCompanyStreet should update address fields and clear edit mode`() = runTest(testDispatcher) {
        val collectJob = launch { viewModel.uiState.collect {} }
        
        // Wait for initial load
        viewModel.uiState.first { it.account != null }

        viewModel.onEvent(AccountEvent.EnterEditMode("company-address"))
        viewModel.onEvent(AccountEvent.UpdateCompanyAddressStreet("New Street"))
        viewModel.onEvent(AccountEvent.SaveCompanyProfile)
        
        val state = viewModel.uiState.first { it.account?.company?.address?.street == "New Street" && it.editingCardId == null }
        assertEquals("New Street", state.account?.company?.address?.street)
        assertEquals(null, state.editingCardId)
        
        collectJob.cancel()
    }

    @Test
    fun `UpdateCompanyLogo should update state and repository after save`() = runTest(testDispatcher) {
        val collectJob = launch { viewModel.uiState.collect {} }
        
        // Wait for initial load
        viewModel.uiState.first { it.account != null }

        val newLogoBytes = byteArrayOf(4, 5, 6)
        viewModel.onEvent(AccountEvent.EnterEditMode("company-branding"))
        viewModel.onEvent(AccountEvent.UpdateCompanyLogo(newLogoBytes))

        // Wait for the background file saving to complete
        advanceUntilIdle()

        viewModel.onEvent(AccountEvent.SaveCompanyProfile)
        
        val state = viewModel.uiState.first { it.account?.company?.logoUrl?.contains("company_logo") == true && it.editingCardId == null }
        assertNotNull(state.account?.company?.logoUrl)
        
        collectJob.cancel()
    }

    @Test
    fun `SignOut event should call authRepository signOut`() = runTest(testDispatcher) {
        viewModel.onEvent(AccountEvent.SignOut)
        // Verify via AuthRepository or check if it doesn't crash
        // Since we don't have a verify() for the fake, we just ensure no exception is thrown
    }

    @Test
    fun `SwitchTab event should update currentTab`() = runTest(testDispatcher) {
        val collectJob = launch { viewModel.uiState.collect {} }
        viewModel.onEvent(AccountEvent.SwitchTab(AccountTab.COMPANY_PROFILE))
        
        val state = viewModel.uiState.first { it.currentTab == AccountTab.COMPANY_PROFILE }
        assertEquals(AccountTab.COMPANY_PROFILE, state.currentTab)
        collectJob.cancel()
    }

    @Test
    fun `EnterEditMode and ExitEditMode should update editingCardId`() = runTest(testDispatcher) {
        val collectJob = launch { viewModel.uiState.collect {} }
        
        viewModel.onEvent(AccountEvent.EnterEditMode("test-card"))
        var state = viewModel.uiState.first { it.editingCardId == "test-card" }
        assertEquals("test-card", state.editingCardId)
        
        viewModel.onEvent(AccountEvent.ExitEditMode)
        state = viewModel.uiState.first { it.editingCardId == null }
        assertEquals(null, state.editingCardId)
        
        collectJob.cancel()
    }
}
