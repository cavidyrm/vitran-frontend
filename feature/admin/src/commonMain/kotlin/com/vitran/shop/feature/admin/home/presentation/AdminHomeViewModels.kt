package com.vitran.shop.feature.admin.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.admin.home.domain.AdminHomeBanner
import com.vitran.shop.feature.admin.home.domain.AdminHomeRepository
import com.vitran.shop.feature.admin.home.domain.AdminHomeSection
import com.vitran.shop.feature.admin.home.domain.SaveAdminHomeBannerCommand
import com.vitran.shop.feature.admin.home.domain.SaveAdminHomeSectionCommand
import com.vitran.shop.feature.admin.rbac.AdminPermissions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AdminHomeAccess {
    Pending,
    Allowed,
    Denied,
}

data class AdminHomeSectionsUiState(
    val access: AdminHomeAccess = AdminHomeAccess.Pending,
    val sections: List<AdminHomeSection> = emptyList(),
    val loading: Boolean = false,
    val deletingIds: Set<Long> = emptySet(),
    val canDeleteSection: Boolean = false,
    val error: AppError? = null,
)

class AdminHomeSectionsViewModel(
    private val repository: AdminHomeRepository,
    private val permissions: AdminPermissions,
    accountRepository: AccountRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminHomeSectionsUiState())
    val uiState = _uiState.asStateFlow()
    private var loaded = false

    init {
        viewModelScope.launch {
            accountRepository.currentUserState.collect { current ->
                when (current) {
                    CurrentUserState.Unknown,
                    CurrentUserState.Loading,
                    -> _uiState.update { it.copy(access = AdminHomeAccess.Pending) }
                    is CurrentUserState.Error -> _uiState.update {
                        it.copy(access = AdminHomeAccess.Denied, loading = false)
                    }
                    is CurrentUserState.Available -> {
                        val allowed = permissions.canAccessAdmin(current.user.roles)
                        _uiState.update {
                            it.copy(
                                access = if (allowed) AdminHomeAccess.Allowed else AdminHomeAccess.Denied,
                                canDeleteSection = permissions.canDeleteHomeSection(current.user.roles),
                                loading = if (allowed) it.loading else false,
                            )
                        }
                        if (allowed && !loaded) refresh()
                    }
                }
            }
        }
    }

    fun refresh() {
        if (_uiState.value.access != AdminHomeAccess.Allowed) return
        loaded = true
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            when (val result = repository.listSections()) {
                is AppResult.Success -> _uiState.update {
                    it.copy(sections = result.value, loading = false)
                }
                is AppResult.Failure -> _uiState.update {
                    it.copy(loading = false, error = result.error)
                }
            }
        }
    }

    fun delete(id: Long) {
        val state = _uiState.value
        if (!state.canDeleteSection || id in state.deletingIds || state.access != AdminHomeAccess.Allowed) return
        _uiState.update { it.copy(deletingIds = it.deletingIds + id, error = null) }
        viewModelScope.launch {
            when (val result = repository.deleteSection(id)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(sections = it.sections.filterNot { section -> section.id == id })
                }
                is AppResult.Failure -> _uiState.update { it.copy(error = result.error) }
            }
            _uiState.update { it.copy(deletingIds = it.deletingIds - id) }
        }
    }
}

data class AdminHomeEditorUiState(
    val access: AdminHomeAccess = AdminHomeAccess.Pending,
    val section: AdminHomeSection? = null,
    val banners: List<AdminHomeBanner> = emptyList(),
    val loading: Boolean = false,
    val saving: Boolean = false,
    val deletingBannerIds: Set<Long> = emptySet(),
    val fieldErrors: Map<String, String> = emptyMap(),
    val bannerFieldErrors: Map<String, String> = emptyMap(),
    val editingBanner: AdminHomeBanner? = null,
    val message: String? = null,
    val error: AppError? = null,
)

class AdminHomeSectionEditorViewModel(
    sectionId: Long?,
    private val repository: AdminHomeRepository,
    private val permissions: AdminPermissions,
    accountRepository: AccountRepository,
) : ViewModel() {
    private var currentSectionId: Long? = sectionId
    private val _uiState = MutableStateFlow(AdminHomeEditorUiState())
    val uiState = _uiState.asStateFlow()
    private var loaded = false

    init {
        viewModelScope.launch {
            accountRepository.currentUserState.collect { current ->
                when (current) {
                    CurrentUserState.Unknown,
                    CurrentUserState.Loading,
                    -> _uiState.update { it.copy(access = AdminHomeAccess.Pending) }
                    is CurrentUserState.Error -> _uiState.update {
                        it.copy(access = AdminHomeAccess.Denied, loading = false)
                    }
                    is CurrentUserState.Available -> {
                        val allowed = permissions.canAccessAdmin(current.user.roles)
                        _uiState.update {
                            it.copy(access = if (allowed) AdminHomeAccess.Allowed else AdminHomeAccess.Denied)
                        }
                        if (allowed && !loaded) {
                            loaded = true
                            load()
                        }
                    }
                }
            }
        }
    }

    fun load() {
        if (_uiState.value.access != AdminHomeAccess.Allowed) return
        val id = currentSectionId
        if (id == null) {
            _uiState.update { it.copy(loading = false, section = null, banners = emptyList()) }
            return
        }
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val result = repository.getSection(id)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(section = result.value, loading = false) }
                    if (result.value.source == "banners") loadBanners(id)
                }
                is AppResult.Failure -> _uiState.update { it.copy(loading = false, error = result.error) }
            }
        }
    }

    fun saveSection(command: SaveAdminHomeSectionCommand) {
        if (_uiState.value.access != AdminHomeAccess.Allowed || _uiState.value.saving) return
        val local = command.localSectionErrors(isCreate = currentSectionId == null)
        if (local.isNotEmpty()) {
            _uiState.update { it.copy(fieldErrors = local, message = null) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(saving = true, fieldErrors = emptyMap(), error = null, message = null) }
            val id = currentSectionId
            val result = if (id == null) {
                repository.createSection(command)
            } else {
                repository.updateSection(id, command)
            }
            when (result) {
                is AppResult.Success -> {
                    currentSectionId = result.value.id
                    _uiState.update {
                        it.copy(saving = false, section = result.value, message = "ذخیره شد")
                    }
                    if (result.value.source == "banners") {
                        loadBanners(result.value.id)
                    } else {
                        _uiState.update { it.copy(banners = emptyList()) }
                    }
                }
                is AppResult.Failure -> _uiState.update {
                    it.copy(
                        saving = false,
                        error = result.error,
                        fieldErrors = result.error.fieldMessages(),
                    )
                }
            }
        }
    }

    fun openBanner(id: Long) {
        if (_uiState.value.access != AdminHomeAccess.Allowed) return
        viewModelScope.launch {
            when (val result = repository.getBanner(id)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(editingBanner = result.value, bannerFieldErrors = emptyMap(), error = null)
                }
                is AppResult.Failure -> _uiState.update { it.copy(error = result.error) }
            }
        }
    }

    fun clearBanner() {
        _uiState.update { it.copy(editingBanner = null, bannerFieldErrors = emptyMap()) }
    }

    fun saveBanner(bannerId: Long?, command: SaveAdminHomeBannerCommand) {
        val sectionId = currentSectionId ?: return
        if (_uiState.value.access != AdminHomeAccess.Allowed || _uiState.value.saving) return
        if (_uiState.value.section?.source != "banners") return
        val local = command.localBannerErrors()
        if (local.isNotEmpty()) {
            _uiState.update { it.copy(bannerFieldErrors = local, message = null) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(saving = true, bannerFieldErrors = emptyMap(), error = null, message = null) }
            val result = if (bannerId == null) {
                repository.createBanner(sectionId, command)
            } else {
                repository.updateBanner(bannerId, command)
            }
            when (result) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(saving = false, editingBanner = null, message = "بنر ذخیره شد")
                    }
                    loadBanners(sectionId)
                }
                is AppResult.Failure -> _uiState.update {
                    it.copy(
                        saving = false,
                        error = result.error,
                        bannerFieldErrors = result.error.fieldMessages(),
                    )
                }
            }
        }
    }

    fun deleteBanner(id: Long) {
        val sectionId = currentSectionId ?: return
        if (_uiState.value.access != AdminHomeAccess.Allowed || id in _uiState.value.deletingBannerIds) return
        _uiState.update { it.copy(deletingBannerIds = it.deletingBannerIds + id, error = null) }
        viewModelScope.launch {
            when (val result = repository.deleteBanner(id)) {
                is AppResult.Success -> loadBanners(sectionId)
                is AppResult.Failure -> _uiState.update { it.copy(error = result.error) }
            }
            _uiState.update { it.copy(deletingBannerIds = it.deletingBannerIds - id) }
        }
    }

    private suspend fun loadBanners(sectionId: Long) {
        when (val result = repository.listBanners(sectionId)) {
            is AppResult.Success -> _uiState.update { it.copy(banners = result.value) }
            is AppResult.Failure -> _uiState.update { it.copy(error = result.error) }
        }
    }
}

private fun SaveAdminHomeSectionCommand.localSectionErrors(isCreate: Boolean): Map<String, String> {
    val errors = linkedMapOf<String, String>()
    if (isCreate && slug.orEmpty().isBlank()) errors["slug"] = "نامک را وارد کنید."
    if (title.isBlank()) errors["title"] = "عنوان را وارد کنید."
    if (itemLimit !in 1..100) errors["item_limit"] = "تعداد آیتم باید بین ۱ و ۱۰۰ باشد."
    return errors
}

private fun SaveAdminHomeBannerCommand.localBannerErrors(): Map<String, String> {
    val errors = linkedMapOf<String, String>()
    val url = imageUrl.trim()
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        errors["image_url"] = "آدرس تصویر باید با http یا https شروع شود."
    }
    if (actionType != "none" && actionValue.isBlank()) {
        errors["action_value"] = "مقدار اکشن را وارد کنید."
    }
    return errors
}

private fun AppError.fieldMessages(): Map<String, String> =
    fieldErrors.mapNotNull { field ->
        field.messages.firstOrNull()?.takeIf { it.isNotBlank() }?.let { field.reason to it }
    }.toMap()
