package net.hitpromo.hitpromoworkstation.presentation.login

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.hitpromo.hitpromoworkstation.data.local.UserPreferences
import net.hitpromo.hitpromoworkstation.domain.model.AuthResult
import net.hitpromo.hitpromoworkstation.domain.repository.BadgeAuthRepository
import net.hitpromo.hitpromoworkstation.domain.repository.WorkersLoginRepository
import net.hitpromo.hitpromoworkstation.domain.scanner.LogLevel
import net.hitpromo.hitpromoworkstation.domain.scanner.ScannerEventDelegate
import net.hitpromo.hitpromoworkstation.domain.scanner.ScannerSDKManager
import net.hitpromo.hitpromoworkstation.domain.usecase.SignInUseCase
import net.hitpromo.hitpromoworkstation.domain.usecase.SignOutUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the login screen implementing MVVM + MVI architecture.
 *
 * Handles authentication state management, user interactions, and scanner integration
 * using reactive streams and use cases.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val userPreferences: UserPreferences,
    private val scannerSDKManager: ScannerSDKManager,
    private val badgeAuthRepository: BadgeAuthRepository,
    private val workersLoginRepository: WorkersLoginRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Tracks the initialization job to prevent race conditions.
     * signIn() must await completion of this job before proceeding.
     */
    private var initializationJob: Job? = null

    /**
     * Scanner event delegate - receives callbacks from ScannerSDKManager.
     * Posts intents to ViewModel for processing.
     */
    private val scannerEventDelegate = object : ScannerEventDelegate {
        override fun onScannerAppeared(scannerId: Int, name: String) {
            viewModelScope.launch {
                handleIntent(LoginIntent.OnScannerAppeared(scannerId, name))
            }
        }

        override fun onScannerConnected(scannerId: Int, name: String) {
            viewModelScope.launch {
                handleIntent(LoginIntent.OnScannerConnected(scannerId, name))
            }
        }

        override fun onScannerDisconnected(scannerId: Int) {
            viewModelScope.launch {
                handleIntent(LoginIntent.OnScannerDisconnected(scannerId))
            }
        }

        override fun onScannerDisappeared(scannerId: Int) {
            viewModelScope.launch {
                handleIntent(LoginIntent.OnScannerDisconnected(scannerId))
            }
        }

        override fun onBarcodeScanned(barcode: String, type: Int, scannerId: Int) {
            viewModelScope.launch {
                handleIntent(LoginIntent.ScanBadge(barcode))
            }
        }

        override fun onError(message: String) {
            viewModelScope.launch {
                handleIntent(LoginIntent.OnScannerError(message))
            }
        }

        override fun onLog(level: LogLevel, message: String) {
            viewModelScope.launch {
                handleIntent(LoginIntent.OnLog(level, message))
            }
        }
    }

    init {
        // Initialize UI state based on stored preferences
        initializationJob = initializeState()

        // Register scanner delegate
        scannerSDKManager.setDelegate(scannerEventDelegate)
        addLog(LogLevel.INFO, "Scanner SDK delegate registered")
    }

    override fun onCleared() {
        super.onCleared()
        // Remove scanner delegate to prevent memory leaks
        scannerSDKManager.setDelegate(null)
        Log.d(TAG, "Scanner delegate removed")
    }

    /**
     * Handle user intents from the UI.
     */
    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.SignIn -> {
                signIn(intent.username, intent.password)
            }
            is LoginIntent.ScanBadge -> {
                signInWithBadge(intent.badgeId)
            }
            is LoginIntent.ReadyToScan -> {
                setReadyToScan()
            }
            is LoginIntent.SignOut -> {
                signOut()
            }
            is LoginIntent.ToggleRememberMe -> {
                toggleRememberMe(intent.remember)
            }
            is LoginIntent.ForgotPassword -> {
                handleForgotPassword()
            }
            is LoginIntent.ClearError -> {
                clearError()
            }
            is LoginIntent.RefreshSession -> {
                refreshSession()
            }
            is LoginIntent.ClearPasswordChangeState -> {
                clearPasswordChangeState()
            }
            // Form input intents
            is LoginIntent.UpdateBadgeId -> {
                updateBadgeId(intent.badgeId)
            }
            is LoginIntent.UpdateMachineId -> {
                updateMachineId(intent.machineId)
            }
            is LoginIntent.SubmitLoginForm -> {
                submitLoginForm(intent.badgeId, intent.machineId)
            }
            is LoginIntent.RetryWorkersLogin -> {
                retryWorkersLogin()
            }
            // Scanner intents
            is LoginIntent.StartScanning -> {
                startScanning()
            }
            is LoginIntent.StopScanning -> {
                stopScanning()
            }
            is LoginIntent.ToggleDebugLogs -> {
                toggleDebugLogs()
            }
            is LoginIntent.CopyLogs -> {
                copyLogsToClipboard()
            }
            is LoginIntent.ClearLogs -> {
                clearDebugLogs()
            }
            // Internal scanner events
            is LoginIntent.OnScannerAppeared -> {
                handleScannerAppeared(intent.scannerId, intent.name)
            }
            is LoginIntent.OnScannerConnected -> {
                handleScannerConnected(intent.scannerId, intent.name)
            }
            is LoginIntent.OnScannerDisconnected -> {
                handleScannerDisconnected(intent.scannerId)
            }
            is LoginIntent.OnScannerError -> {
                handleScannerError(intent.message)
            }
            is LoginIntent.OnLog -> {
                addLog(intent.level, intent.message)
            }
        }
    }

    /**
     * Sign in with username and password.
     *
     * Waits for initialization to complete before attempting sign-in
     * to prevent race conditions with session validation.
     */
    private fun signIn(username: String, password: String) {
        viewModelScope.launch {
            // Wait for initialization to complete before proceeding
            initializationJob?.join()

            signInUseCase(username, password)
                .catch { exception ->
                    Log.e(TAG, "Sign-in flow error", exception)
                    emit(AuthResult.Error(
                        "Sign-in failed: ${exception.message ?: "Unknown error"}",
                        exception
                    ))
                }
                .collect { result ->
                    when (result) {
                        is AuthResult.Loading -> {
                            _uiState.value = LoginUiState.Loading(_uiState.value)
                        }
                        is AuthResult.Success -> {
                            val currentRememberMe = _uiState.value.rememberMe
                            _uiState.value = LoginUiState.Success(
                                user = result.data,
                                rememberMe = currentRememberMe
                            )

                            // Save remember me preference
                            if (currentRememberMe) {
                                userPreferences.setRememberMe(true)
                            }
                        }
                        is AuthResult.Error -> {
                            _uiState.value = LoginUiState.Error(
                                message = result.message,
                                currentState = _uiState.value
                            )
                        }
                        is AuthResult.NewPasswordRequired -> {
                            // Navigate to password change screen
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                requirePasswordChange = true,
                                passwordChangeUsername = result.username,
                                passwordChangeSessionId = result.sessionId,
                                errorMessage = null
                            )
                        }
                    }
                }
        }
    }

    /**
     * Sign in with scanned badge ID.
     *
     * Calls badge lookup API and authenticates user if successful.
     * Automatically stores the JWT token for subsequent API requests.
     */
    private fun signInWithBadge(badgeId: String) {
        viewModelScope.launch {
            try {
                // Wait for initialization to complete before proceeding
                initializationJob?.join()

                // Validate badge ID format and length
                val sanitizedBadgeId = badgeId.trim()

                if (sanitizedBadgeId.isEmpty()) {
                    Log.w(TAG, "Badge ID is empty")
                    addLog(LogLevel.WARNING, "Invalid badge ID: Empty")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        scannerStatus = ScannerStatus.ReadyToScan,
                        errorMessage = "Invalid badge ID"
                    )
                    return@launch
                }

                // Enforce maximum length (typical badge IDs are shorter)
                if (sanitizedBadgeId.length > 50) {
                    Log.w(TAG, "Badge ID exceeds maximum length: ${sanitizedBadgeId.length}")
                    addLog(LogLevel.WARNING, "Invalid badge ID: Too long")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        scannerStatus = ScannerStatus.ReadyToScan,
                        errorMessage = "Invalid badge ID format"
                    )
                    return@launch
                }

                Log.d(TAG, "Badge scan validation passed")
                addLog(LogLevel.INFO, "Processing badge scan")

                // Set loading state
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    scannerStatus = ScannerStatus.Scanning,
                    isReadyToScan = false
                )

                // Call badge authentication API
                val result = badgeAuthRepository.authenticateWithBadge(sanitizedBadgeId)

                if (result.isSuccess) {
                    val response = result.getOrThrow()
                    val operatorName = response.data?.name ?: "Unknown"
                    val firstName = response.data?.firstName ?: ""
                    val lastName = response.data?.lastName ?: ""

                    Log.d(TAG, "Badge authentication successful: $operatorName ($firstName $lastName)")
                    addLog(LogLevel.SUCCESS, "Badge authenticated: $operatorName")

                    // Log token status
                    if (response.token != null) {
                        addLog(LogLevel.INFO, "JWT token received and stored")
                    } else {
                        addLog(LogLevel.WARNING, "No JWT token in response")
                    }

                    // For now, just set authenticated state
                    // TODO: Integrate with Cognito or create user session
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        scannerStatus = ScannerStatus.ReadyToScan,
                        errorMessage = null
                    )
                } else {
                    val error = result.exceptionOrNull()
                    val errorMessage = error?.message ?: "Badge authentication failed"
                    Log.e(TAG, errorMessage, error)
                    addLog(LogLevel.ERROR, errorMessage)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        scannerStatus = ScannerStatus.ReadyToScan,
                        errorMessage = errorMessage
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "Badge authentication error", e)
                addLog(LogLevel.ERROR, "Badge auth error: ${e.message}")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    scannerStatus = ScannerStatus.ReadyToScan,
                    errorMessage = "Badge authentication failed: ${e.message}"
                )
            }
        }
    }

    /**
     * Set the state to ready to scan.
     */
    private fun setReadyToScan() {
        _uiState.value = _uiState.value.copy(
            isReadyToScan = true,
            errorMessage = null
        )
    }

    /**
     * Sign out the current user.
     *
     * Clears JWT token and user session data.
     */
    private fun signOut() {
        viewModelScope.launch {
            try {
                // Clear JWT token before signing out
                userPreferences.clearJwtToken()
                addLog(LogLevel.INFO, "JWT token cleared")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing JWT token during logout", e)
                addLog(LogLevel.WARNING, "Failed to clear JWT token")
            }

            signOutUseCase()
                .catch { exception ->
                    Log.e(TAG, "Sign-out flow error", exception)
                    emit(AuthResult.Error(
                        "Sign-out failed: ${exception.message ?: "Unknown error"}",
                        exception
                    ))
                }
                .collect { result ->
                    when (result) {
                        is AuthResult.Loading -> {
                            _uiState.value = LoginUiState.Loading(_uiState.value)
                        }
                        is AuthResult.Success -> {
                            val rememberMe = userPreferences.rememberMe.first()
                            // Preserve scanner state and debug logs on logout
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isAuthenticated = false,
                                user = null,
                                errorMessage = null,
                                rememberMe = rememberMe,
                                isSessionValidated = false,
                                requirePasswordChange = false,
                                passwordChangeUsername = null,
                                passwordChangeSessionId = null,
                                isReadyToScan = false
                                // Keep scannerStatus, scannerName, scannerId, and debugLogs from current state
                            )
                            addLog(LogLevel.INFO, "User logged out successfully")
                        }
                        is AuthResult.Error -> {
                            _uiState.value = LoginUiState.Error(
                                message = "Failed to sign out: ${result.message}",
                                currentState = _uiState.value
                            )
                        }
                        is AuthResult.NewPasswordRequired -> {
                            // This should never happen during sign-out
                            _uiState.value = LoginUiState.Error(
                                message = "Unexpected state during sign-out",
                                currentState = _uiState.value
                            )
                        }
                    }
                }
        }
    }

    /**
     * Toggle remember me preference.
     */
    private fun toggleRememberMe(remember: Boolean) {
        viewModelScope.launch {
            userPreferences.setRememberMe(remember)
            _uiState.value = _uiState.value.copy(rememberMe = remember)
        }
    }

    /**
     * Handle forgot password action.
     */
    private fun handleForgotPassword() {
        // For now, show a message to contact administrator
        _uiState.value = _uiState.value.copy(
            errorMessage = "Please contact your system administrator for password reset assistance."
        )
    }

    /**
     * Clear any error messages.
     */
    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Refresh the authentication session.
     */
    private fun refreshSession() {
        viewModelScope.launch {
            try {
                // This would typically call a refresh session use case
                // For now, we'll validate the current session
                val isLoggedIn = userPreferences.isLoggedIn.first()
                val userId = userPreferences.userId.first()

                if (isLoggedIn && userId != null) {
                    // Session appears valid, keep current state
                    _uiState.value = _uiState.value.copy(isSessionValidated = true)
                } else {
                    // Session invalid, redirect to login
                    val rememberMe = userPreferences.rememberMe.first()
                    _uiState.value = LoginUiState.Unauthenticated(rememberMe)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Session refresh error", e)
                _uiState.value = LoginUiState.Error(
                    message = "Failed to refresh session: ${e.message}",
                    currentState = _uiState.value
                )
            }
        }
    }

    /**
     * Clear password change state after successful change.
     */
    private fun clearPasswordChangeState() {
        _uiState.value = _uiState.value.copy(
            requirePasswordChange = false,
            passwordChangeUsername = null,
            passwordChangeSessionId = null
        )
    }

    /**
     * Update badge ID input field.
     */
    private fun updateBadgeId(badgeId: String) {
        _uiState.value = _uiState.value.copy(
            badgeId = badgeId,
            errorMessage = null
        )
    }

    /**
     * Update machine ID input field.
     */
    private fun updateMachineId(machineId: String) {
        _uiState.value = _uiState.value.copy(
            machineId = machineId,
            errorMessage = null
        )
    }

    /**
     * Submit the login form with badge ID and machine ID.
     *
     * Performs two-step authentication:
     * 1. Authenticate with badge ID to get JWT token
     * 2. Submit worker login with badge data and machine ID
     */
    private fun submitLoginForm(badgeId: String, machineId: String) {
        viewModelScope.launch {
            try {
                // Validate inputs
                val sanitizedBadgeId = badgeId.trim()
                val sanitizedMachineId = machineId.trim()

                if (sanitizedBadgeId.isEmpty()) {
                    Log.w(TAG, "Badge ID is empty")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Badge ID is required"
                    )
                    return@launch
                }

                if (sanitizedMachineId.isEmpty()) {
                    Log.w(TAG, "Machine ID is empty")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Machine ID is required"
                    )
                    return@launch
                }

                if (sanitizedBadgeId.length > 50) {
                    Log.w(TAG, "Badge ID exceeds maximum length: ${sanitizedBadgeId.length}")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Invalid badge ID format"
                    )
                    return@launch
                }

                if (sanitizedMachineId.length > 100) {
                    Log.w(TAG, "Machine ID exceeds maximum length: ${sanitizedMachineId.length}")
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Invalid machine ID format"
                    )
                    return@launch
                }

                Log.d(TAG, "Form validation passed, starting badge authentication")
                addLog(LogLevel.INFO, "Authenticating badge")

                // Set loading state
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                // Step 1: Authenticate with badge
                val badgeResult = badgeAuthRepository.authenticateWithBadge(sanitizedBadgeId)

                if (badgeResult.isFailure) {
                    val error = badgeResult.exceptionOrNull()
                    val errorMessage = error?.message ?: "Badge authentication failed"
                    Log.e(TAG, errorMessage, error)
                    addLog(LogLevel.ERROR, errorMessage)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                    return@launch
                }

                val badgeResponse = badgeResult.getOrThrow()
                val jwtToken = badgeResponse.token

                if (jwtToken.isNullOrEmpty()) {
                    Log.e(TAG, "No JWT token received from badge authentication")
                    addLog(LogLevel.ERROR, "No JWT token received")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Badge authentication failed: no token received"
                    )
                    return@launch
                }

                Log.d(TAG, "Badge authentication successful, proceeding to workers login")
                addLog(LogLevel.SUCCESS, "Badge authenticated")

                // Extract operator data from badge response
                val operatorName = badgeResponse.data?.name ?: "Unknown"
                val workerEmail = badgeResponse.data?.firstName ?: ""
                val firstName = badgeResponse.data?.firstName ?: ""
                val lastName = badgeResponse.data?.lastName ?: ""

                Log.d(TAG, "Badge operator: $operatorName ($firstName $lastName)")
                addLog(LogLevel.INFO, "Starting workers login")

                // Set state to indicate workers login is in progress
                _uiState.value = _uiState.value.copy(
                    isWorkersLoginInProgress = true
                )

                // Step 2: Submit workers login
                val workersResult = workersLoginRepository.loginWorker(
                    machineId = sanitizedMachineId,
                    workerId = sanitizedBadgeId,
                    workerName = operatorName,
                    workerEmail = workerEmail,
                    jwtToken = jwtToken
                )

                if (workersResult.isFailure) {
                    val error = workersResult.exceptionOrNull()
                    val errorMessage = error?.message ?: "Workers login failed"
                    Log.e(TAG, errorMessage, error)
                    addLog(LogLevel.ERROR, errorMessage)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isWorkersLoginInProgress = false,
                        errorMessage = errorMessage
                    )
                    return@launch
                }

                val workersResponse = workersResult.getOrThrow()
                Log.d(TAG, "Workers login successful: session_id=${workersResponse.sessionId}, status=${workersResponse.status}")
                addLog(LogLevel.SUCCESS, "Workers login successful")

                // Store session ID
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isWorkersLoginInProgress = false,
                    isAuthenticated = true,
                    sessionId = workersResponse.sessionId,
                    errorMessage = null
                )

            } catch (e: Exception) {
                Log.e(TAG, "Login form submission error", e)
                addLog(LogLevel.ERROR, "Login error: ${e.message}")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isWorkersLoginInProgress = false,
                    errorMessage = "Login failed: ${e.message}"
                )
            }
        }
    }

    /**
     * Retry workers login after failure with same badge and machine IDs.
     */
    private fun retryWorkersLogin() {
        val currentState = _uiState.value
        submitLoginForm(currentState.badgeId, currentState.machineId)
    }

    /**
     * Initialize the UI state based on stored preferences.
     *
     * Checks if user was previously logged in and validates their session with Amplify.
     * If session is valid, restores authenticated state. Otherwise, requires re-authentication.
     *
     * @return Job representing the initialization coroutine
     */
    private fun initializeState(): Job {
        return viewModelScope.launch {
            try {
                val isLoggedIn = userPreferences.isLoggedIn.first()
                val rememberMe = userPreferences.rememberMe.first()

                if (isLoggedIn) {
                    // Check if we have valid session data in preferences
                    val userId = userPreferences.userId.first()
                    val username = userPreferences.username.first()
                    val email = userPreferences.userEmail.first()
                    val roleString = userPreferences.userRole.first()

                    if (userId != null && username != null && email != null && roleString != null) {
                        // Show loading state while validating session
                        _uiState.value = LoginUiState.Loading(_uiState.value)

                        // Validate session with Amplify Auth
                        signInUseCase.validateSession()
                            .catch { exception ->
                                Log.e(TAG, "Session validation flow error", exception)
                                emit(AuthResult.Error(
                                    "Session validation failed: ${exception.message ?: "Unknown error"}",
                                    exception
                                ))
                            }
                            .collect { result ->
                                when (result) {
                                    is AuthResult.Success -> {
                                        // Session is valid, restore authenticated state
                                        _uiState.value = LoginUiState.Success(
                                            user = result.data,
                                            rememberMe = rememberMe
                                        )
                                    }
                                    is AuthResult.Error -> {
                                        // Session invalid, clear preferences and require login
                                        Log.w(TAG, "Session validation error: ${result.message}")
                                        userPreferences.clearUserSession()
                                        _uiState.value = LoginUiState.Unauthenticated(rememberMe)
                                    }
                                    is AuthResult.Loading -> {
                                        // Keep loading state
                                        _uiState.value = LoginUiState.Loading(_uiState.value)
                                    }
                                    is AuthResult.NewPasswordRequired -> {
                                        // This should never happen during session validation
                                        Log.w(TAG, "Unexpected password change required during session validation")
                                        userPreferences.clearUserSession()
                                        _uiState.value = LoginUiState.Unauthenticated(rememberMe)
                                    }
                                }
                            }
                    } else {
                        // Incomplete session data, require re-authentication
                        Log.d(TAG, "Incomplete session data, requiring re-authentication")
                        _uiState.value = LoginUiState.Unauthenticated(rememberMe)
                    }
                } else {
                    _uiState.value = LoginUiState.Unauthenticated(rememberMe)
                }
            } catch (e: Exception) {
                // On any error, clear session and show error
                Log.e(TAG, "Failed to initialize login state", e)
                userPreferences.clearUserSession()
                _uiState.value = LoginUiState.Error(
                    message = "Failed to initialize login state: ${e.message}",
                    currentState = LoginUiState.Initial
                )
            }
        }
    }

    // ========================================
    // Scanner-specific methods
    // ========================================

    /**
     * Start scanning mode.
     */
    private fun startScanning() {
        _uiState.value = _uiState.value.copy(
            scannerStatus = if (_uiState.value.scannerId != null) {
                ScannerStatus.ReadyToScan
            } else {
                ScannerStatus.ScannerNotFound
            },
            isReadyToScan = true,
            errorMessage = null
        )
        addLog(LogLevel.INFO, "Scanning mode activated")
    }

    /**
     * Stop scanning mode.
     */
    private fun stopScanning() {
        _uiState.value = _uiState.value.copy(
            scannerStatus = if (_uiState.value.scannerId != null) {
                ScannerStatus.Connected
            } else {
                ScannerStatus.Disconnected
            },
            isReadyToScan = false
        )
        addLog(LogLevel.INFO, "Scanning mode deactivated")
    }

    /**
     * Toggle debug logs modal visibility.
     */
    private fun toggleDebugLogs() {
        _uiState.value = _uiState.value.copy(
            showDebugModal = !_uiState.value.showDebugModal
        )
    }

    /**
     * Copy debug logs to clipboard.
     */
    private fun copyLogsToClipboard() {
        val logs = _uiState.value.debugLogs.joinToString("\n") { log ->
            val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date(log.timestamp))
            "[$timestamp] [${log.level.name}] ${log.message}"
        }

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Scanner Debug Logs", logs)
        clipboard.setPrimaryClip(clip)

        addLog(LogLevel.INFO, "Logs copied to clipboard (${_uiState.value.debugLogs.size} entries)")
    }

    /**
     * Clear debug logs.
     */
    private fun clearDebugLogs() {
        _uiState.value = _uiState.value.copy(debugLogs = emptyList())
        addLog(LogLevel.INFO, "Debug logs cleared")
    }

    /**
     * Handle scanner appeared event.
     */
    private fun handleScannerAppeared(scannerId: Int, name: String) {
        _uiState.value = _uiState.value.copy(
            scannerStatus = ScannerStatus.ScannerFound,
            scannerName = name,
            scannerId = scannerId
        )
    }

    /**
     * Handle scanner connected event.
     */
    private fun handleScannerConnected(scannerId: Int, name: String) {
        _uiState.value = _uiState.value.copy(
            scannerStatus = ScannerStatus.Connected,
            scannerName = name,
            scannerId = scannerId
        )
    }

    /**
     * Handle scanner disconnected event.
     * If scannerId is 0, it means no scanner was ever detected (initial state).
     */
    private fun handleScannerDisconnected(scannerId: Int) {
        _uiState.value = _uiState.value.copy(
            scannerStatus = if (scannerId == 0) {
                // scannerId 0 means no scanner was found during initialization
                ScannerStatus.ScannerNotFound
            } else {
                // A real scanner was disconnected
                ScannerStatus.Disconnected
            },
            scannerId = null,
            scannerName = null,
            isReadyToScan = false
        )
    }

    /**
     * Handle scanner error.
     */
    private fun handleScannerError(message: String) {
        _uiState.value = _uiState.value.copy(
            scannerStatus = ScannerStatus.Error,
            errorMessage = message
        )
    }

    /**
     * Add a log entry to debug logs.
     * Limits log size to maxDebugLogs to prevent memory issues.
     */
    private fun addLog(level: LogLevel, message: String) {
        val newLog = DebugLog(
            timestamp = System.currentTimeMillis(),
            level = level,
            message = message
        )

        val updatedLogs = (_uiState.value.debugLogs + newLog).takeLast(_uiState.value.maxDebugLogs)

        _uiState.value = _uiState.value.copy(debugLogs = updatedLogs)
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}