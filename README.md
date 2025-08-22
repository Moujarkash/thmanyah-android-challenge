# Thmanyah Android Challenge

A modern Android application built with Jetpack Compose for browsing and searching audio content including podcasts, episodes, audiobooks, and articles. The app features horizontal scroll pagination, intelligent search with debouncing, and robust handling of malformed API data.

## 📱 Features

- **Home Screen**: Browse content organized in multiple section types with infinite horizontal scrolling
- **Search**: Real-time search with 200ms debouncing and intelligent data parsing
- **Multiple Content Types**: Support for podcasts, episodes, audiobooks, and audio articles
- **Responsive Design**: Material 3 design system with dynamic themes
- **Offline Resilience**: Graceful handling of network errors and malformed API responses
- **Accessibility**: Full accessibility support with content descriptions and semantic markup

## 🏗️ Architecture

The project follows **Clean Architecture** principles with **MVVM** pattern:

```
├── core/                 # Shared utilities and extensions
│   ├── util/            # Result wrapper, common utilities  
│   └── extensions/      # Extension functions for formatting
├── data/                # Data layer
│   ├── dto/            # Data Transfer Objects
│   ├── mapper/         # DTO to Domain mappers
│   ├── remote/api/     # API services (Ktor)
│   └── repository/     # Repository implementations
├── domain/              # Business logic layer
│   ├── model/          # Domain models
│   └── repository/     # Repository interfaces
├── ui/                 # Presentation layer
│   ├── components/     # Reusable UI components
│   ├── home/          # Home screen and ViewModel
│   ├── search/        # Search screen and ViewModel
│   ├── navigation/    # Navigation setup
│   └── theme/         # UI theme and styling
└── di/                # Dependency injection (Koin)
```

## 🛠️ Tech Stack

### **Core Technologies**
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Koin
- **Networking**: Ktor with OkHttp engine
- **Image Loading**: Coil 3
- **Navigation**: Navigation Compose
- **Serialization**: Kotlinx Serialization

### **Testing**
- **Unit Tests**: JUnit, Mockito, MockK
- **UI Tests**: Compose Testing, Espresso
- **API Testing**: Ktor MockEngine
- **Coroutines Testing**: kotlinx-coroutines-test

## 🚀 Getting Started

### **Prerequisites**
- Android Studio Hedgehog | 2023.1.1 or newer
- JDK 17
- Android SDK API 36
- Minimum SDK API 24

### **Installation**

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/thmanyah-android-challenge.git
   cd thmanyah-android-challenge
   ```

2. **Open in Android Studio**
    - File → Open → Select the project directory
    - Wait for Gradle sync to complete

3. **Add dependencies to `libs.versions.toml`**
   ```toml
   [versions]
   navigation-compose = "2.9.2"
   lifecycle-viewmodel-compose = "2.9.2"
   mockito = "5.8.0"
   mockito-kotlin = "5.2.1"
   coroutines-test = "1.8.0"
   arch-core-testing = "2.2.0"
   ktor-client-mock = "3.2.3"
   mockk = "1.13.8"

   [libraries]
   # Add navigation dependencies
   androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation-compose" }
   androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle-viewmodel-compose" }
   
   # Add test dependencies
   mockito-core = { group = "org.mockito", name = "mockito-core", version.ref = "mockito" }
   mockito-kotlin = { group = "org.mockito.kotlin", name = "mockito-kotlin", version.ref = "mockito-kotlin" }
   kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines-test" }
   androidx-arch-core-testing = { group = "androidx.arch.core", name = "core-testing", version.ref = "arch-core-testing" }
   ktor-client-mock = { group = "io.ktor", name = "ktor-client-mock", version.ref = "ktor-client-mock" }
   mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
   ```

4. **Add Internet permission to `AndroidManifest.xml`**
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```

5. **Build and run**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

## 📡 API Endpoints

The app integrates with the following APIs:

- **Home Sections**: `https://api-v2-b2sit6oh3a-uc.a.run.app/home_sections`
- **Search**: `https://mock.apidog.com/m1/735111-711675-default/search`

### **API Response Structure**

**Home Sections Response:**
```json
{
  "sections": [
    {
      "name": "Top Podcasts",
      "type": "square",
      "content_type": "podcast",
      "order": 1,
      "content": [...]
    }
  ],
  "pagination": {
    "next_page": "/home_sections?page=2",
    "total_pages": 10
  }
}
```

**Search Response:**
```json
{
  "sections": [
    {
      "name": "Results Section",
      "type": "square", 
      "content_type": "podcast",
      "order": "1",
      "content": [...]
    }
  ]
}
```

## 🎨 UI Components

### **Section Types**
- **Square Grid**: Horizontal scrolling cards (180dp width)
- **2-Lines Grid**: Fixed 2x2 grid (4 items max)
- **Big Square**: Larger horizontal cards (220dp width)
- **Queue**: Vertical list (5 items max)

### **Content Types**
- **Podcasts**: Show episode count, duration, and description
- **Episodes**: Display podcast name, duration, and episode details
- **Audiobooks**: Show author information and duration
- **Audio Articles**: Display author and article duration

## 🔄 Pagination Logic

The app implements intelligent horizontal scroll pagination:

1. **Horizontal Scroll Detection**: Each section monitors its scroll state
2. **Smart Triggering**: Loads more when user reaches last 2 items
3. **URL Parsing**: Extracts actual page numbers from `next_page` URLs
4. **Content Merging**: New content is merged into existing sections
5. **Duplicate Prevention**: Avoids duplicate items using unique IDs

```kotlin
// Example pagination flow:
// User scrolls "Top Podcasts" horizontally → Near end detected →
// API call to next page → New podcasts merged into section →  
// All sections updated with fresh content
```

## 🔍 Search Implementation

### **Search Features**
- **200ms Debouncing**: Prevents excessive API calls
- **Minimum 2 Characters**: Reduces unnecessary requests
- **Real-time Results**: Instant feedback as user types
- **Malformed Data Handling**: Robust parsing of API inconsistencies

### **Data Transformation**
The search API returns malformed data that requires intelligent parsing:

```kotlin
// API Response (malformed):
{
  "episode_count": "not_a_number",
  "duration": "Lorem ipsum", 
  "score": "minim",
  "language": "in dolore laborum"
}

// Transformed (clean):
Podcast(
  episodeCount = 0,           // Safely defaulted
  duration = 0L,              // Safely defaulted
  score = null,               // Null for invalid data
  language = "en"             // Cleaned Lorem ipsum
)
```

## 🧪 Testing

### **Test Coverage**
- **Unit Tests**: Data mappers, ViewModels, repositories, utilities
- **Integration Tests**: API services with mock responses
- **UI Tests**: User interactions, state changes, navigation flows
- **Edge Case Tests**: Malformed data, network errors, boundary conditions

### **Running Tests**

```bash
# Run all unit tests
./gradlew test

# Run UI tests  
./gradlew connectedAndroidTest

# Run with coverage report
./gradlew testDebugUnitTest jacocoTestReport

# Run specific test suites
./gradlew test --tests "*Search*"
./gradlew test --tests "*Home*" 
./gradlew test --tests "*Mapper*"
```

### **Test Categories**

1. **API Service Tests**: Mock HTTP responses, error scenarios
2. **Mapper Tests**: DTO to Domain conversion, malformed data handling
3. **Repository Tests**: Success/error flows, data transformation
4. **ViewModel Tests**: State management, user interactions, debouncing
5. **UI Tests**: Screen rendering, user flows, accessibility

## 🛡️ Error Handling

The app implements comprehensive error handling:

### **Network Errors**
- Connection timeouts
- Server errors (4xx, 5xx)
- Network unavailability
- Malformed JSON responses

### **Data Parsing**
- String numbers converted safely
- Lorem ipsum text filtered out
- Null/missing fields handled gracefully
- Invalid values defaulted appropriately

### **User Experience**
- Clear error messages
- Retry functionality
- Graceful degradation
- Loading state management

## 📁 Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/mod/thmanyah_android_challenge/
│   │   │   ├── core/
│   │   │   │   ├── util/Result.kt
│   │   │   │   └── extensions/
│   │   │   ├── data/
│   │   │   │   ├── dto/
│   │   │   │   ├── mapper/
│   │   │   │   ├── remote/api/
│   │   │   │   └── repository/
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   └── repository/
│   │   │   ├── ui/
│   │   │   │   ├── components/
│   │   │   │   ├── home/
│   │   │   │   ├── search/
│   │   │   │   ├── navigation/
│   │   │   │   └── theme/
│   │   │   ├── di/
│   │   │   └── MainActivity.kt
│   │   └── AndroidManifest.xml
│   ├── test/                    # Unit tests
│   └── androidTest/             # UI tests
├── build.gradle.kts
└── libs.versions.toml
```

## 🎯 Key Features Implemented

### **Home Screen**
- ✅ Multiple section types with different layouts
- ✅ Horizontal scroll pagination
- ✅ Content type recognition (podcast, episode, etc.)
- ✅ Loading states and error handling
- ✅ Search navigation

### **Search Screen**
- ✅ Real-time search with debouncing
- ✅ Malformed API data parsing
- ✅ Section-based results display
- ✅ Empty states and error recovery
- ✅ Clear and back navigation

### **Technical Excellence**
- ✅ Clean Architecture with MVVM
- ✅ Dependency Injection with Koin
- ✅ Reactive UI with StateFlow
- ✅ Comprehensive test coverage
- ✅ Material 3 design system

## 🔮 Future Enhancements

### **Potential Improvements**
- **Offline Support**: Local caching with Room database
- **Audio Playback**: Integrated media player
- **User Accounts**: Authentication and personalization
- **Content Filtering**: Advanced search filters
- **Push Notifications**: New content alerts
- **Analytics**: User behavior tracking
- **Accessibility**: Enhanced screen reader support

### **Technical Optimizations**
- **Paging 3**: Replace custom pagination
- **DataStore**: User preferences storage
- **WorkManager**: Background sync
- **Hilt**: Alternative DI framework
- **Compose Multiplatform**: iOS support

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### **Development Guidelines**
- Follow Clean Architecture principles
- Write comprehensive tests for new features
- Use Material 3 design components
- Ensure accessibility compliance
- Document complex business logic

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Contact

- **Developer**: [Your Name]
- **Email**: [your.email@example.com]
- **LinkedIn**: [Your LinkedIn Profile]
- **GitHub**: [Your GitHub Profile]

## 🙏 Acknowledgments

- **Thmanyah** for the interesting technical challenge
- **JetBrains** for Kotlin and development tools
- **Google** for Android and Jetpack Compose
- **Open Source Community** for the amazing libraries used

---

**Built with ❤️ using Kotlin and Jetpack Compose**