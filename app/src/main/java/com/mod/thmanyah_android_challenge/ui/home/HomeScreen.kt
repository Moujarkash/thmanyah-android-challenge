package com.mod.thmanyah_android_challenge.ui.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.mod.thmanyah_android_challenge.domain.model.ContentItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onContentClick: (ContentItem) -> Unit = {},
    onSearchClick: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {

}