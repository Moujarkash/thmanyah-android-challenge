package com.mod.thmanyah_android_challenge.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mod.thmanyah_android_challenge.R
import com.mod.thmanyah_android_challenge.domain.model.ContentItem
import com.mod.thmanyah_android_challenge.ui.components.EmptySearchView
import com.mod.thmanyah_android_challenge.ui.components.HomeSectionView
import com.mod.thmanyah_android_challenge.ui.components.NoResultsView
import com.mod.thmanyah_android_challenge.ui.components.SearchErrorView
import com.mod.thmanyah_android_challenge.ui.components.SearchFooter
import com.mod.thmanyah_android_challenge.ui.components.SearchResultsHeader
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onContentClick: (ContentItem) -> Unit = {},
    viewModel: SearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = viewModel::updateSearchQuery,
                        placeholder = { Text(stringResource(R.string.search_content_min_2_chars)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.updateSearchQuery("") }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear"
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                searchQuery.isEmpty() || (!uiState.searchPerformed && !uiState.isLoading) -> {
                    EmptySearchView(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(modifier = Modifier.testTag("Loading"))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.searching_for, searchQuery),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                uiState.error != null -> {
                    SearchErrorView(
                        query = searchQuery,
                        onRetry = viewModel::retry,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.sections.isEmpty() -> {
                    NoResultsView(
                        query = searchQuery,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        item {
                            SearchResultsHeader(
                                totalResults = uiState.totalResults,
                                query = searchQuery,
                                sectionsCount = uiState.sections.size
                            )
                        }

                        items(
                            items = uiState.sections,
                            key = { "${it.name}_search_${it.hashCode()}" }
                        ) { section ->
                            HomeSectionView(
                                section = section,
                                onContentClick = onContentClick,
                                onLoadMore = { },
                                isLoading = false
                            )
                        }

                        item {
                            SearchFooter()
                        }
                    }
                }
            }
        }
    }
}