package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.sections.categories.CategoriesBrowseCategoriesSection
import com.vitran.shop.ui.sections.categories.CategoriesExploreFeaturedSection
import com.vitran.shop.ui.sections.categories.rememberMockBrowseCategories
import com.vitran.shop.ui.sections.categories.rememberMockExploreEdits

/**
 * Categories (Explore) screen host. Sections are added one at a time.
 * Route: `/categories` ↔ shop.app Explore.
 */
@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val edits = rememberMockExploreEdits()
    val browseCategories = rememberMockBrowseCategories()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        CategoriesExploreFeaturedSection(
            edits = edits,
            modifier = Modifier.fillMaxWidth(),
            onEditClick = { /* mock — collection landing not wired yet */ },
        )
        CategoriesBrowseCategoriesSection(
            categories = browseCategories,
            modifier = Modifier.fillMaxWidth(),
            onCategoryClick = { /* mock — category landing not wired yet */ },
        )
    }
}
