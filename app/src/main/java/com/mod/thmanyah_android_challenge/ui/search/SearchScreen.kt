package com.mod.thmanyah_android_challenge.ui.search

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.mod.thmanyah_android_challenge.domain.model.SearchResult
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onResultClick: (SearchResult) -> Unit = {},
    viewModel: SearchViewModel = koinViewModel()
) {

}