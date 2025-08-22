package com.mod.thmanyah_android_challenge.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mod.thmanyah_android_challenge.domain.model.ContentItem
import com.mod.thmanyah_android_challenge.domain.model.HomeSection

@Composable
fun HomeSectionView(
    section: HomeSection,
    modifier: Modifier = Modifier,
    onContentClick: (ContentItem) -> Unit = {},
    onLoadMore: () -> Unit = {},
    isLoading: Boolean = false
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            }
        }

        when (section.type) {
            "square" -> SquareGridSection(
                items = section.content,
                onContentClick = onContentClick,
                onLoadMore = onLoadMore
            )
            "2_lines_grid" -> TwoLinesGridSection(
                items = section.content,
                onContentClick = onContentClick
            )
            "big_square" -> BigSquareSection(
                items = section.content,
                onContentClick = onContentClick,
                onLoadMore = onLoadMore
            )
            "queue" -> QueueSection(
                items = section.content,
                onContentClick = onContentClick
            )
            else -> SquareGridSection(
                items = section.content,
                onContentClick = onContentClick,
                onLoadMore = onLoadMore
            )
        }
    }
}

@Composable
fun SquareGridSection(
    items: List<ContentItem>,
    onContentClick: (ContentItem) -> Unit,
    onLoadMore: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - 2) // Load when 2 items left
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && listState.layoutInfo.totalItemsCount > 0) {
                onLoadMore()
            }
        }
    }

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            ContentCard(
                contentItem = item,
                modifier = Modifier.width(180.dp),
                onClick = { onContentClick(item) }
            )
        }
    }
}

@Composable
fun TwoLinesGridSection(
    items: List<ContentItem>,
    onContentClick: (ContentItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.height(400.dp) // Fixed height for 2 rows
    ) {
        items(items.take(4)) { item ->
            ContentCard(
                contentItem = item,
                onClick = { onContentClick(item) }
            )
        }
    }
}

@Composable
fun BigSquareSection(
    items: List<ContentItem>,
    onContentClick: (ContentItem) -> Unit,
    onLoadMore: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - 2) // Load when 2 items left
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && listState.layoutInfo.totalItemsCount > 0) {
                onLoadMore()
            }
        }
    }

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            ContentCard(
                contentItem = item,
                modifier = Modifier.width(220.dp),
                onClick = { onContentClick(item) }
            )
        }
    }
}

@Composable
fun QueueSection(
    items: List<ContentItem>,
    onContentClick: (ContentItem) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.take(5).forEach { item ->
            QueueContentCard(
                contentItem = item,
                onClick = { onContentClick(item) }
            )
        }
    }
}