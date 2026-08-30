package com.vitran.shop.feature.admin.catalog.taxonomy.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.admin.catalog.taxonomy.domain.AdminTaxonomyRepository
import com.vitran.shop.feature.admin.rbac.AdminPermissions
import com.vitran.shop.feature.admin.state.AdminSessionStateStore
import com.vitran.shop.feature.taxonomy.domain.model.AttributeSlug
import com.vitran.shop.feature.taxonomy.domain.model.AttributeValueSlug
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TaxonomyImportUiState(
    val categoriesFile: SelectedFile? = null,
    val attributesFile: SelectedFile? = null,
    val isConfirmed: Boolean = false,
    val canImport: Boolean = false,
    val isSubmitting: Boolean = false,
    val imported: Boolean = false,
    val error: AppError? = null,
)

class TaxonomyImportViewModel(
    private val repository: AdminTaxonomyRepository,
    private val accountRepository: AccountRepository,
    private val permissions: AdminPermissions,
    sessionStateStore: AdminSessionStateStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaxonomyImportUiState(canImport = canImport()))
    val uiState: StateFlow<TaxonomyImportUiState> = _uiState.asStateFlow()
    private val unregisterClear = sessionStateStore.registerClearCallback {
        _uiState.value = TaxonomyImportUiState()
    }
    private var submitJob: Job? = null

    init {
        viewModelScope.launch {
            accountRepository.currentUserState.collect {
                _uiState.update { state -> state.copy(canImport = canImport()) }
            }
        }
    }

    fun setCategoriesFile(file: SelectedFile?) {
        if (!_uiState.value.isSubmitting) _uiState.update { it.copy(categoriesFile = file, imported = false) }
    }

    fun setAttributesFile(file: SelectedFile?) {
        if (!_uiState.value.isSubmitting) _uiState.update { it.copy(attributesFile = file, imported = false) }
    }

    fun setConfirmed(confirmed: Boolean) {
        if (!_uiState.value.isSubmitting) _uiState.update { it.copy(isConfirmed = confirmed) }
    }

    fun import() {
        if (_uiState.value.isSubmitting || submitJob?.isActive == true) return
        val allowed = canImport()
        val categories = _uiState.value.categoriesFile
        val attributes = _uiState.value.attributesFile
        _uiState.update { it.copy(canImport = allowed) }
        val validationError =
            when {
                !allowed -> AppError.Forbidden(message = "اجازه ورود طبقه‌بندی را ندارید")
                !_uiState.value.isConfirmed -> AppError.Validation(message = "تأیید ورود اطلاعات الزامی است")
                categories == null || attributes == null ->
                    AppError.Validation(message = "هر دو فایل دسته‌بندی و ویژگی الزامی است")
                else -> null
            }
        if (validationError != null || categories == null || attributes == null) {
            _uiState.update { it.copy(error = validationError) }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, imported = false, error = null) }
        submitJob =
            viewModelScope.launch {
                when (val result = repository.importTaxonomy(categories, attributes)) {
                    is AppResult.Success ->
                        _uiState.update { it.copy(isSubmitting = false, imported = true) }
                    is AppResult.Failure ->
                        _uiState.update { it.copy(isSubmitting = false, error = result.error) }
                }
            }
    }

    private fun canImport(): Boolean {
        val roles =
            (accountRepository.currentUserState.value as? CurrentUserState.Available)
                ?.user
                ?.roles
                .orEmpty()
        return permissions.canImportTaxonomy(roles)
    }

    override fun onCleared() {
        submitJob?.cancel()
        unregisterClear()
    }
}

data class CategoryEditUiState(
    val isSubmitting: Boolean = false,
    val nameSaved: Boolean = false,
    val iconSaved: Boolean = false,
    val error: AppError? = null,
)

class CategoryEditViewModel(
    private val slug: CategorySlug,
    private val repository: AdminTaxonomyRepository,
    sessionStateStore: AdminSessionStateStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryEditUiState())
    val uiState: StateFlow<CategoryEditUiState> = _uiState.asStateFlow()
    private val unregisterClear = sessionStateStore.registerClearCallback {
        _uiState.value = CategoryEditUiState()
    }
    private var submitJob: Job? = null

    fun rename(name: String) = submit(nameSaved = true) {
        repository.renameCategory(slug, name.trim())
    }

    fun uploadIcon(image: SelectedFile) = submit(iconSaved = true) {
        repository.uploadCategoryIcon(slug, image)
    }

    private fun submit(
        nameSaved: Boolean = false,
        iconSaved: Boolean = false,
        operation: suspend () -> AppResult<Unit>,
    ) {
        if (_uiState.value.isSubmitting || submitJob?.isActive == true) return
        _uiState.update { it.copy(isSubmitting = true, error = null) }
        submitJob =
            viewModelScope.launch {
                when (val result = operation()) {
                    is AppResult.Success ->
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                nameSaved = it.nameSaved || nameSaved,
                                iconSaved = it.iconSaved || iconSaved,
                            )
                        }
                    is AppResult.Failure ->
                        _uiState.update { it.copy(isSubmitting = false, error = result.error) }
                }
            }
    }

    override fun onCleared() {
        submitJob?.cancel()
        unregisterClear()
    }
}

data class TaxonomyNameEditUiState(
    val isSubmitting: Boolean = false,
    val saved: Boolean = false,
    val error: AppError? = null,
)

class AttributeNameEditViewModel(
    private val slug: AttributeSlug,
    private val repository: AdminTaxonomyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaxonomyNameEditUiState())
    val uiState: StateFlow<TaxonomyNameEditUiState> = _uiState.asStateFlow()

    fun rename(name: String) {
        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            when (val result = repository.renameAttribute(slug, name.trim())) {
                is AppResult.Success -> _uiState.value = TaxonomyNameEditUiState(saved = true)
                is AppResult.Failure -> _uiState.update { it.copy(isSubmitting = false, error = result.error) }
            }
        }
    }
}

class ValueNameEditViewModel(
    private val slug: AttributeValueSlug,
    private val repository: AdminTaxonomyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaxonomyNameEditUiState())
    val uiState: StateFlow<TaxonomyNameEditUiState> = _uiState.asStateFlow()

    fun rename(name: String) {
        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            when (val result = repository.renameValue(slug, name.trim())) {
                is AppResult.Success -> _uiState.value = TaxonomyNameEditUiState(saved = true)
                is AppResult.Failure -> _uiState.update { it.copy(isSubmitting = false, error = result.error) }
            }
        }
    }
}
