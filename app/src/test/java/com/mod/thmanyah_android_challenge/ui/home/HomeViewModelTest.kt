package com.mod.thmanyah_android_challenge.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mod.thmanyah_android_challenge.core.util.Result
import com.mod.thmanyah_android_challenge.domain.model.*
import com.mod.thmanyah_android_challenge.domain.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class HomeViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var homeRepository: HomeRepository

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should show loading and empty sections`() = runTest {
        val mockResponse = HomeSectionsResponse(
            sections = emptyList(),
            pagination = Pagination(null, 1)
        )
        whenever(homeRepository.getHomeSections(1)).thenReturn(Result.Success(mockResponse))

        viewModel = HomeViewModel(homeRepository)

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertTrue(state.sections.isEmpty())
        assertEquals(1, state.currentPage)
        assertEquals(1, state.totalPages)
        assertFalse(state.hasNextPage)
        assertNull(state.error)
    }

    @Test
    fun `loadHomeSections should update state with success result`() = runTest {
        val mockSections = listOf(
            HomeSection(
                name = "Test Section",
                type = "square",
                contentType = "podcast",
                order = 1,
                content = listOf(
                    Podcast(
                        podcastId = "1",
                        name = "Test Podcast",
                        description = "Description",
                        avatarUrl = "url",
                        episodeCount = 10,
                        duration = 3600,
                        language = "en",
                        priority = 1,
                        popularityScore = 5,
                        score = 100.0
                    )
                )
            )
        )
        val mockResponse = HomeSectionsResponse(
            sections = mockSections,
            pagination = Pagination("/page2", 2)
        )
        whenever(homeRepository.getHomeSections(1)).thenReturn(Result.Success(mockResponse))

        viewModel = HomeViewModel(homeRepository)

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(1, state.sections.size)
        assertEquals("Test Section", state.sections[0].name)
        assertEquals(1, state.currentPage)
        assertEquals(2, state.totalPages)
        assertTrue(state.hasNextPage)
        assertNull(state.error)
    }

    @Test
    fun `loadHomeSections should update state with error result`() = runTest {
        val errorMessage = "Network error"
        whenever(homeRepository.getHomeSections(1)).thenReturn(Result.Error(Exception(errorMessage)))

        viewModel = HomeViewModel(homeRepository)

        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertTrue(state.sections.isEmpty())
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `loadMore should merge content within existing sections instead of duplicating sections`() = runTest {
        val firstPagePodcast = Podcast(
            podcastId = "podcast_1",
            name = "Test Podcast 1",
            description = "Description 1",
            avatarUrl = "url1",
            episodeCount = 10,
            duration = 3600,
            language = "en",
            priority = 1,
            popularityScore = 5,
            score = 100.0
        )

        val secondPagePodcast = Podcast(
            podcastId = "podcast_2", // Different ID
            name = "Test Podcast 2",
            description = "Description 2",
            avatarUrl = "url2",
            episodeCount = 15,
            duration = 4800,
            language = "en",
            priority = 2,
            popularityScore = 7,
            score = 95.0
        )

        val firstPageSections = listOf(
            HomeSection("Top Podcasts", "square", "podcast", 1, listOf(firstPagePodcast))
        )

        val secondPageSections = listOf(
            HomeSection("Top Podcasts", "square", "podcast", 1, listOf(secondPagePodcast))
        )

        val firstResponse = HomeSectionsResponse(
            sections = firstPageSections,
            pagination = Pagination("/home_sections?page=2", 2)
        )
        val secondResponse = HomeSectionsResponse(
            sections = secondPageSections,
            pagination = Pagination(null, 2)
        )

        whenever(homeRepository.getHomeSections(1)).thenReturn(Result.Success(firstResponse))
        whenever(homeRepository.getHomeSections(2)).thenReturn(Result.Success(secondResponse))

        viewModel = HomeViewModel(homeRepository)
        viewModel.loadMore()

        val state = viewModel.uiState.first()

        assertEquals(1, state.sections.size)
        assertEquals("Top Podcasts", state.sections[0].name)

        assertEquals(2, state.sections[0].content.size)

        val firstContent = state.sections[0].content[0] as Podcast
        val secondContent = state.sections[0].content[1] as Podcast

        assertEquals("podcast_1", firstContent.podcastId)
        assertEquals("podcast_2", secondContent.podcastId)
        assertEquals(2, state.currentPage)
        assertFalse(state.hasNextPage)
    }

    @Test
    fun `loadMore should not duplicate content items with same ID`() = runTest {
        val samePodcast = Podcast(
            podcastId = "podcast_1", // Same ID
            name = "Test Podcast 1",
            description = "Description 1",
            avatarUrl = "url1",
            episodeCount = 10,
            duration = 3600,
            language = "en",
            priority = 1,
            popularityScore = 5,
            score = 100.0
        )

        val firstPageSections = listOf(
            HomeSection("Top Podcasts", "square", "podcast", 1, listOf(samePodcast))
        )

        val secondPageSections = listOf(
            HomeSection("Top Podcasts", "square", "podcast", 1, listOf(samePodcast)) // Same podcast
        )

        val firstResponse = HomeSectionsResponse(
            sections = firstPageSections,
            pagination = Pagination("/home_sections?page=2", 2)
        )
        val secondResponse = HomeSectionsResponse(
            sections = secondPageSections,
            pagination = Pagination(null, 2)
        )

        whenever(homeRepository.getHomeSections(1)).thenReturn(Result.Success(firstResponse))
        whenever(homeRepository.getHomeSections(2)).thenReturn(Result.Success(secondResponse))

        viewModel = HomeViewModel(homeRepository)
        viewModel.loadMore()

        val state = viewModel.uiState.first()

        assertEquals(1, state.sections.size)
        assertEquals("Top Podcasts", state.sections[0].name)

        assertEquals(1, state.sections[0].content.size)

        val content = state.sections[0].content[0] as Podcast
        assertEquals("podcast_1", content.podcastId)
    }

    @Test
    fun `loadMore should handle mixed content types correctly`() = runTest {
        val podcast = Podcast(
            podcastId = "podcast_1",
            name = "Test Podcast",
            description = "Description",
            avatarUrl = "url1",
            episodeCount = 10,
            duration = 3600,
            language = "en",
            priority = 1,
            popularityScore = 5,
            score = 100.0
        )

        val episode = Episode(
            episodeId = "episode_1",
            name = "Test Episode",
            seasonNumber = 1,
            episodeType = "full",
            podcastName = "Host Podcast",
            authorName = "Author",
            description = "Episode desc",
            number = 1,
            duration = 1800,
            avatarUrl = "url2",
            separatedAudioUrl = null,
            audioUrl = "audio.mp3",
            releaseDate = "2024-01-01",
            podcastId = "podcast_1",
            podcastPopularityScore = 8,
            podcastPriority = 4,
            score = 95.0
        )

        val firstPageSections = listOf(
            HomeSection("Mixed Content", "square", "mixed", 1, listOf(podcast))
        )

        val secondPageSections = listOf(
            HomeSection("Mixed Content", "square", "mixed", 1, listOf(episode))
        )

        val firstResponse = HomeSectionsResponse(
            sections = firstPageSections,
            pagination = Pagination("/home_sections?page=2", 2)
        )
        val secondResponse = HomeSectionsResponse(
            sections = secondPageSections,
            pagination = Pagination(null, 2)
        )

        whenever(homeRepository.getHomeSections(1)).thenReturn(Result.Success(firstResponse))
        whenever(homeRepository.getHomeSections(2)).thenReturn(Result.Success(secondResponse))

        viewModel = HomeViewModel(homeRepository)
        viewModel.loadMore()

        val state = viewModel.uiState.first()

        assertEquals(1, state.sections.size)
        assertEquals(2, state.sections[0].content.size)

        val firstContent = state.sections[0].content[0]
        val secondContent = state.sections[0].content[1]

        assertTrue(firstContent is Podcast)
        assertTrue(secondContent is Episode)

        assertEquals("podcast_1", (firstContent as Podcast).podcastId)
        assertEquals("episode_1", (secondContent as Episode).episodeId)
    }

    @Test
    fun `retry should call loadHomeSections with page 1`() = runTest {
        val errorMessage = "Network error"
        val successResponse = HomeSectionsResponse(
            sections = listOf(HomeSection("Test", "square", "podcast", 1, emptyList())),
            pagination = Pagination(null, 1)
        )

        whenever(homeRepository.getHomeSections(1))
            .thenReturn(Result.Error(Exception(errorMessage)))
            .thenReturn(Result.Success(successResponse))

        viewModel = HomeViewModel(homeRepository)

        var state = viewModel.uiState.first()
        assertEquals(errorMessage, state.error)

        viewModel.retry()

        state = viewModel.uiState.first()
        assertNull(state.error)
        assertEquals(1, state.sections.size)
    }
}