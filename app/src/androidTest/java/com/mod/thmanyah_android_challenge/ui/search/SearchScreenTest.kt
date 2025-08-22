package com.mod.thmanyah_android_challenge.ui.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mod.thmanyah_android_challenge.domain.model.*
import com.mod.thmanyah_android_challenge.ui.theme.ThmanyahAndroidChallengeTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchScreen_displaysEmptyStateInitially() {
        val mockViewModel = mockk<SearchViewModel>()
        val emptyState = SearchUiState()
        every { mockViewModel.uiState } returns MutableStateFlow(emptyState)
        every { mockViewModel.searchQuery } returns MutableStateFlow("")
        every { mockViewModel.updateSearchQuery(any()) } returns Unit

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                SearchScreen(
                    onBackClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Start typing to search").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search for podcasts, episodes, audiobooks, and articles")
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_updatesQueryOnTextInput() {
        val mockViewModel = mockk<SearchViewModel>()
        val emptyState = SearchUiState()
        every { mockViewModel.uiState } returns MutableStateFlow(emptyState)
        every { mockViewModel.searchQuery } returns MutableStateFlow("")
        every { mockViewModel.updateSearchQuery(any()) } returns Unit

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                SearchScreen(
                    onBackClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Search content… (min 2 chars)")
            .performTextInput("kotlin")

        verify { mockViewModel.updateSearchQuery("kotlin") }
    }

    @Test
    fun searchScreen_displaysLoadingStateWithQuery() {
        val mockViewModel = mockk<SearchViewModel>()
        val loadingState = SearchUiState(isLoading = true)
        every { mockViewModel.uiState } returns MutableStateFlow(loadingState)
        every { mockViewModel.searchQuery } returns MutableStateFlow("android")
        every { mockViewModel.updateSearchQuery(any()) } returns Unit

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                SearchScreen(
                    onBackClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithTag("Loading").assertIsDisplayed()
        composeTestRule.onNodeWithText("Searching for \"android\"…").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysSearchResults() {
        // Give
        val mockViewModel = mockk<SearchViewModel>()
        val testSections = listOf(
            createTestSection("Podcasts", listOf(
                createTestPodcast("podcast_1", "Android Development"),
                createTestPodcast("podcast_2", "Kotlin Programming")
            )),
            createTestSection("Episodes", listOf(
                createTestEpisode("episode_1", "Latest Android Features")
            ))
        )

        val resultState = SearchUiState(
            isLoading = false,
            sections = testSections,
            totalResults = 3,
            searchPerformed = true
        )

        every { mockViewModel.uiState } returns MutableStateFlow(resultState)
        every { mockViewModel.searchQuery } returns MutableStateFlow("android")
        every { mockViewModel.updateSearchQuery(any()) } returns Unit

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                SearchScreen(
                    onBackClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Search Results").assertIsDisplayed()
        composeTestRule.onNodeWithText("Found 3 items in 2 sections for \"android\"").assertIsDisplayed()
        composeTestRule.onNodeWithText("Podcasts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Episodes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Android Development").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kotlin Programming").assertIsDisplayed()
        composeTestRule.onNodeWithText("Latest Android Features").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysNoResultsState() {
        val mockViewModel = mockk<SearchViewModel>()
        val noResultsState = SearchUiState(
            isLoading = false,
            sections = emptyList(),
            totalResults = 0,
            searchPerformed = true
        )

        every { mockViewModel.uiState } returns MutableStateFlow(noResultsState)
        every { mockViewModel.searchQuery } returns MutableStateFlow("nonexistent")
        every { mockViewModel.updateSearchQuery(any()) } returns Unit

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                SearchScreen(
                    onBackClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithText("No results found").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysErrorState() {
        val mockViewModel = mockk<SearchViewModel>()
        val errorState = SearchUiState(
            isLoading = false,
            sections = emptyList(),
            error = "Network connection failed",
            searchPerformed = true
        )

        every { mockViewModel.uiState } returns MutableStateFlow(errorState)
        every { mockViewModel.searchQuery } returns MutableStateFlow("failed")
        every { mockViewModel.updateSearchQuery(any()) } returns Unit
        every { mockViewModel.retry() } returns Unit

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                SearchScreen(
                    onBackClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Search Failed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Failed to search for \"failed\"").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()

        composeTestRule.onNodeWithText("Try Again").performClick()
        verify { mockViewModel.retry() }
    }

    @Test
    fun searchScreen_backButtonCallsOnBackClick() {
        val mockViewModel = mockk<SearchViewModel>()
        val emptyState = SearchUiState()
        every { mockViewModel.uiState } returns MutableStateFlow(emptyState)
        every { mockViewModel.searchQuery } returns MutableStateFlow("")
        every { mockViewModel.updateSearchQuery(any()) } returns Unit

        var backClicked = false

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                SearchScreen(
                    onBackClick = { backClicked = true },
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        assert(backClicked)
    }

    @Test
    fun searchScreen_clearButtonClearsQuery() {
        val mockViewModel = mockk<SearchViewModel>()
        val emptyState = SearchUiState()
        every { mockViewModel.uiState } returns MutableStateFlow(emptyState)
        every { mockViewModel.searchQuery } returns MutableStateFlow("test query")
        every { mockViewModel.updateSearchQuery(any()) } returns Unit

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                SearchScreen(
                    onBackClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Clear").performClick()

        verify { mockViewModel.updateSearchQuery("") }
    }

    @Test
    fun searchScreen_contentClickCallsOnContentClick() {
        val mockViewModel = mockk<SearchViewModel>()
        val testPodcast = createTestPodcast("clickable_podcast", "Clickable Podcast")
        val testSections = listOf(
            createTestSection("Test Section", listOf(testPodcast))
        )

        val resultState = SearchUiState(
            isLoading = false,
            sections = testSections,
            totalResults = 1,
            searchPerformed = true
        )

        every { mockViewModel.uiState } returns MutableStateFlow(resultState)
        every { mockViewModel.searchQuery } returns MutableStateFlow("clickable")
        every { mockViewModel.updateSearchQuery(any()) } returns Unit

        var clickedContent: ContentItem? = null

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                SearchScreen(
                    onBackClick = {},
                    onContentClick = { clickedContent = it },
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Clickable Podcast").performClick()

        assertNotNull(clickedContent)
        assertTrue(clickedContent is Podcast)
        assertEquals("Clickable Podcast", clickedContent?.name)
    }

    @Test
    fun searchScreen_searchResultsHeaderDisplaysCorrectCounts() {
        val mockViewModel = mockk<SearchViewModel>()
        val testSections = listOf(
            createTestSection("Section 1", listOf(
                createTestPodcast("1", "Podcast 1"),
                createTestPodcast("2", "Podcast 2")
            )),
            createTestSection("Section 2", listOf(
                createTestPodcast("3", "Podcast 3"),
                createTestPodcast("4", "Podcast 4"),
                createTestPodcast("5", "Podcast 5")
            ))
        )

        val resultState = SearchUiState(
            isLoading = false,
            sections = testSections,
            totalResults = 5,
            searchPerformed = true
        )

        every { mockViewModel.uiState } returns MutableStateFlow(resultState)
        every { mockViewModel.searchQuery } returns MutableStateFlow("multiple")
        every { mockViewModel.updateSearchQuery(any()) } returns Unit

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                SearchScreen(
                    onBackClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Found 5 items in 2 sections for \"multiple\"").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysMinimumCharacterHint() {
        val mockViewModel = mockk<SearchViewModel>()
        val emptyState = SearchUiState()
        every { mockViewModel.uiState } returns MutableStateFlow(emptyState)
        every { mockViewModel.searchQuery } returns MutableStateFlow("")
        every { mockViewModel.updateSearchQuery(any()) } returns Unit

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                SearchScreen(
                    onBackClick = {},
                    viewModel = mockViewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Search content… (min 2 chars)").assertIsDisplayed()
    }

    private fun createTestSection(name: String, content: List<ContentItem>): HomeSection {
        return HomeSection(
            name = name,
            type = "square",
            contentType = "podcast",
            order = 1,
            content = content
        )
    }

    private fun createTestPodcast(id: String, name: String): Podcast {
        return Podcast(
            podcastId = id,
            name = name,
            description = "Description for $name",
            avatarUrl = "https://test.com/$id.jpg",
            episodeCount = 10,
            duration = 3600,
            language = "en",
            priority = 1,
            popularityScore = 5,
            score = 95.0
        )
    }

    private fun createTestEpisode(id: String, name: String): Episode {
        return Episode(
            episodeId = id,
            name = name,
            seasonNumber = 1,
            episodeType = "full",
            podcastName = "Test Podcast",
            authorName = "Test Author",
            description = "Description for $name",
            number = 1,
            duration = 1800,
            avatarUrl = "https://test.com/$id.jpg",
            separatedAudioUrl = null,
            audioUrl = "https://test.com/$id.mp3",
            releaseDate = "2024-01-01T00:00:00Z",
            podcastId = "test_podcast",
            podcastPopularityScore = 8,
            podcastPriority = 4,
            score = 92.0
        )
    }
}