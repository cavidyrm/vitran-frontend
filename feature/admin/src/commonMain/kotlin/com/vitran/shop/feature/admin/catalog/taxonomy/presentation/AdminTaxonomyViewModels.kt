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
import com.vitran.shop.feature.taxonomy.domain.model.CategoryDetails
import com.vitran.shop.feature.taxonomy.domain.model.CategoryNode
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.feature.taxonomy.domain.repository.TaxonomyRepository
import kotlinx.coroutines.CancellationException
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

data class TaxonomyBrowseUiState(
    val loading: Boolean = true,
    val roots: List<CategoryNode> = emptyList(),
    val path: List<CategorySlug> = emptyList(),
    val loadError: AppError? = null,
    val editingSlug: CategorySlug? = null,
    val editName: String = "",
    val editTitle: String = "",
    val editIconUrl: String? = null,
    val editLoading: Boolean = false,
    val saving: Boolean = false,
    val nameSaved: Boolean = false,
    val iconSaved: Boolean = false,
    val editError: AppError? = null,
) {
    val levelNodes: List<CategoryNode>
        get() = taxonomyNodesAt(roots, path)

    val pathNodes: List<CategoryNode>
        get() = taxonomyPathNodes(roots, path)

    /** Parent is a branch, but this list response did not include its children. */
    val unlistedChildren: Boolean
        get() {
            val parent = pathNodes.lastOrNull() ?: return false
            return !parent.isLeaf && parent.children.isEmpty()
        }
}

class TaxonomyBrowseViewModel(
    private val taxonomyRepository: TaxonomyRepository,
    private val adminRepository: AdminTaxonomyRepository,
    sessionStateStore: AdminSessionStateStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaxonomyBrowseUiState())
    val uiState: StateFlow<TaxonomyBrowseUiState> = _uiState.asStateFlow()
    private val unregisterClear = sessionStateStore.registerClearCallback { clearEdit() }
    private var loadJob: Job? = null
    private var saveJob: Job? = null
    private var detailJob: Job? = null

    init {
        refresh(forceRefresh = true)
    }

    fun retry() = refresh(forceRefresh = true)

    fun open(slug: CategorySlug) {
        val node = _uiState.value.levelNodes.firstOrNull { it.slug == slug } ?: return
        if (node.isLeaf && node.children.isEmpty()) return
        clearEdit()
        _uiState.update { it.copy(path = it.path + slug) }
    }

    /** [depth] 0 returns to the roots. Larger values keep that many path segments. */
    fun openCrumb(depth: Int) {
        clearEdit()
        _uiState.update { it.copy(path = it.path.take(depth.coerceAtLeast(0))) }
    }

    fun beginEdit(slug: CategorySlug) {
        val node = _uiState.value.levelNodes.firstOrNull { it.slug == slug } ?: return
        detailJob?.cancel()
        _uiState.update {
            it.copy(
                editingSlug = slug,
                editName = node.localizedName.orEmpty(),
                editTitle = node.sourceTitle,
                editIconUrl = null,
                editLoading = true,
                saving = false,
                nameSaved = false,
                iconSaved = false,
                editError = null,
            )
        }
        detailJob =
            viewModelScope.launch {
                when (val result = taxonomyRepository.getCategory(slug, forceRefresh = true)) {
                    is AppResult.Success -> applyDetails(slug, node.localizedName.orEmpty(), result.value)
                    is AppResult.Failure ->
                        _uiState.update { state ->
                            if (state.editingSlug != slug) state
                            else state.copy(editLoading = false, editError = result.error)
                        }
                }
            }
    }

    fun setEditName(name: String) {
        if (_uiState.value.saving) return
        _uiState.update { it.copy(editName = name, nameSaved = false, editError = null) }
    }

    fun saveName() {
        val slug = _uiState.value.editingSlug ?: return
        val name = _uiState.value.editName.trim()
        if (name.isBlank()) {
            _uiState.update {
                it.copy(editError = AppError.Validation(message = "نام فارسی الزامی است"))
            }
            return
        }
        submit(slug) { adminRepository.renameCategory(slug, name) }
    }

    fun uploadIcon(image: SelectedFile) {
        val slug = _uiState.value.editingSlug ?: return
        submit(slug, iconSaved = true) { adminRepository.uploadCategoryIcon(slug, image) }
    }

    private fun submit(
        slug: CategorySlug,
        iconSaved: Boolean = false,
        operation: suspend () -> AppResult<Unit>,
    ) {
        if (_uiState.value.saving || saveJob?.isActive == true) return
        _uiState.update { it.copy(saving = true, editError = null) }
        saveJob =
            viewModelScope.launch {
                try {
                    when (val result = operation()) {
                        is AppResult.Success -> refreshAfterSave(slug, iconSaved = iconSaved)
                        is AppResult.Failure ->
                            _uiState.update { it.copy(saving = false, editError = result.error) }
                    }
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (throwable: Throwable) {
                    _uiState.update {
                        it.copy(
                            saving = false,
                            editError = AppError.Unexpected(message = throwable.message),
                        )
                    }
                }
            }
    }

    private fun refresh(forceRefresh: Boolean) {
        loadJob?.cancel()
        val path = _uiState.value.path
        _uiState.update { it.copy(loading = true, loadError = null) }
        loadJob =
            viewModelScope.launch {
                try {
                    applyTree(taxonomyRepository.getCategoryTree(forceRefresh), path)
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (throwable: Throwable) {
                    _uiState.update {
                        it.copy(
                            loading = false,
                            loadError = AppError.Unexpected(message = throwable.message),
                        )
                    }
                }
            }
    }

    private suspend fun refreshAfterSave(slug: CategorySlug, iconSaved: Boolean) {
        val path = _uiState.value.path
        applyTree(taxonomyRepository.getCategoryTree(forceRefresh = true), path)
        _uiState.update {
            it.copy(
                saving = false,
                nameSaved = it.nameSaved || !iconSaved,
                iconSaved = it.iconSaved || iconSaved,
            )
        }
        when (val details = taxonomyRepository.getCategory(slug, forceRefresh = true)) {
            is AppResult.Success -> {
                val currentName = _uiState.value.editName
                applyDetails(slug, currentName, details.value)
            }
            is AppResult.Failure ->
                _uiState.update { state ->
                    if (state.editingSlug != slug) state else state.copy(editError = details.error)
                }
        }
    }

    private fun applyTree(result: AppResult<List<CategoryNode>>, path: List<CategorySlug>) {
        when (result) {
            is AppResult.Success ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        roots = result.value,
                        path = resolveTaxonomyPath(result.value, path),
                        loadError = null,
                    )
                }
            is AppResult.Failure ->
                _uiState.update { it.copy(loading = false, loadError = result.error) }
        }
    }

    private fun applyDetails(slug: CategorySlug, nameBeforeDetails: String, details: CategoryDetails) {
        _uiState.update { state ->
            if (state.editingSlug != slug) return@update state
            val keepTypedName = state.editName != nameBeforeDetails && state.editName.isNotBlank()
            state.copy(
                editLoading = false,
                editTitle = details.sourceTitle,
                editIconUrl = details.iconUrl,
                editName = if (keepTypedName) state.editName else details.localizedName.orEmpty(),
            )
        }
    }

    private fun clearEdit() {
        detailJob?.cancel()
        _uiState.update {
            it.copy(
                editingSlug = null,
                editName = "",
                editTitle = "",
                editIconUrl = null,
                editLoading = false,
                saving = false,
                nameSaved = false,
                iconSaved = false,
                editError = null,
            )
        }
    }

    override fun onCleared() {
        loadJob?.cancel()
        saveJob?.cancel()
        detailJob?.cancel()
        unregisterClear()
    }
}

internal fun resolveTaxonomyPath(
    roots: List<CategoryNode>,
    path: List<CategorySlug>,
): List<CategorySlug> {
    val kept = mutableListOf<CategorySlug>()
    var nodes = roots
    for (slug in path) {
        val node = nodes.firstOrNull { it.slug == slug } ?: break
        kept += slug
        nodes = node.children
    }
    return kept
}

internal fun taxonomyNodesAt(
    roots: List<CategoryNode>,
    path: List<CategorySlug>,
): List<CategoryNode> {
    var nodes = roots
    for (slug in path) {
        val node = nodes.firstOrNull { it.slug == slug } ?: return emptyList()
        nodes = node.children
    }
    return nodes
}

internal fun taxonomyPathNodes(
    roots: List<CategoryNode>,
    path: List<CategorySlug>,
): List<CategoryNode> {
    val found = mutableListOf<CategoryNode>()
    var nodes = roots
    for (slug in path) {
        val node = nodes.firstOrNull { it.slug == slug } ?: break
        found += node
        nodes = node.children
    }
    return found
}
