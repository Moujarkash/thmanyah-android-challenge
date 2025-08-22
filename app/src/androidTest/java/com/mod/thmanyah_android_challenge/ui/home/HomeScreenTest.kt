package com.mod.thmanyah_android_challenge.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mod.thmanyah_android_challenge.domain.model.HomeSection
import com.mod.thmanyah_android_challenge.domain.model.Podcast
import com.mod.thmanyah_android_challenge.ui.components.ContentCard
import com.mod.thmanyah_android_challenge.ui.theme.ThmanyahAndroidChallengeTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysLoadingState() {
        val mockViewModel = mockk<HomeViewModel>()
        val loadingState = HomeUiState(isLoading = true)
        every { mockViewModel.uiState } returns MutableStateFlow(loadingState)

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                HomeScreen(viewModel = mockViewModel)
            }
        }

        composeTestRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysErrorState() {
        val mockViewModel = mockk<HomeViewModel>()
        val errorState = HomeUiState(
            isLoading = false,
            error = "Network error occurred"
        )
        every { mockViewModel.uiState } returns MutableStateFlow(errorState)
        every { mockViewModel.retry() } returns Unit

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                HomeScreen(viewModel = mockViewModel)
            }
        }

        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysSectionsWithContent() {
        val mockViewModel = mockk<HomeViewModel>()
        val testPodcast = Podcast(
            podcastId = "1",
            name = "Test Podcast",
            description = "A test podcast description",
            avatarUrl = "https://test.com/image.jpg",
            episodeCount = 10,
            duration = 3600,
            language = "en",
            priority = 1,
            popularityScore = 5,
            score = 100.0
        )

        val testSection = HomeSection(
            name = "Top Podcasts",
            type = "square",
            contentType = "podcast",
            order = 1,
            content = listOf(testPodcast)
        )

        val successState = HomeUiState(
            isLoading = false,
            sections = listOf(testSection),
            error = null
        )

        every { mockViewModel.uiState } returns MutableStateFlow(successState)
        every { mockViewModel.loadMore() } returns Unit

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                HomeScreen(viewModel = mockViewModel)
            }
        }

        composeTestRule.onNodeWithText("Top Podcasts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Podcast").assertIsDisplayed()
        composeTestRule.onNodeWithText("10 episodes").assertIsDisplayed()
    }

    @Test
    fun homeScreen_searchButtonNavigatesToSearch() {
        val mockViewModel = mockk<HomeViewModel>()
        val successState = HomeUiState(
            isLoading = false,
            sections = emptyList(),
            error = null
        )
        every { mockViewModel.uiState } returns MutableStateFlow(successState)

        var searchClicked = false

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                HomeScreen(
                    viewModel = mockViewModel,
                    onSearchClick = { searchClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Search").performClick()

        assert(searchClicked)
    }

    @Test
    fun contentCard_displaysCorrectInformation() {
        val testPodcast = Podcast(
            podcastId = "1",
            name = "Test Podcast Title",
            description = "This is a detailed description of the test podcast content",
            avatarUrl = "https://test.com/image.jpg",
            episodeCount = 25,
            duration = 7200, // 2 hours
            language = "en",
            priority = 1,
            popularityScore = 8,
            score = 95.5
        )

        var contentClicked = false

        composeTestRule.setContent {
            ThmanyahAndroidChallengeTheme {
                ContentCard(
                    contentItem = testPodcast,
                    onClick = { contentClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Test Podcast Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("25 episodes").assertIsDisplayed()
        composeTestRule.onNodeWithText("2:00:00").assertIsDisplayed()
        composeTestRule.onNodeWithText("This is a detailed description of the test podcast content")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Test Podcast Title").performClick()
        assert(contentClicked)
    }
}