# PopcornPicks 🍿

PopcornPicks is an Android application for browsing and discovering movies using The Movie Database (TMDB) API.

## Features

- 🎬 Browse popular, trending, and top-rated movies
- 🔍 Search for movies
- 📱 View detailed movie information
- 💾 Local caching with Room Database
- 🏗️ MVVM Architecture
- 🎨 Modern UI with Material Design

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: Retrofit
- **Image Loading**: Glide
- **Local Database**: Room
- **Dependency Injection**: (As per project requirements)
- **Async**: Coroutines & Flow

## Project Structure

```
app/
├── src/main/java/com/popcornpicks/
│   ├── home/           # Home screen with movie lists
│   ├── details/        # Movie details screen
│   ├── search/         # Search functionality
│   ├── data/
│   │   ├── api/        # Retrofit API service
│   │   ├── local/      # Room database
│   │   ├── model/      # Data models
│   │   └── repository/ # Repository pattern
│   ├── adapter/        # RecyclerView adapters
│   ├── utils/          # Utility classes
│   └── viewmodel/      # ViewModels
```

## Documentation

- [MVVM Architecture](MVVM_ARCHITECTURE.md)
- [Room Database Implementation](ROOM_DB_IMPLEMENTATION.md)
- [RecyclerView Setup](RECYCLERVIEW_SETUP.md)
- [Gradle Refactoring](GRADLE_REFACTORING.md)
- [Dependency Management](DEPENDENCY_MANAGEMENT.md)

## Setup

1. Clone the repository:
```bash
git clone <your-repository-url>
```

2. Open the project in Android Studio

3. Add your TMDB API key in `local.properties`:
```properties
TMDB_API_KEY=your_api_key_here
```

4. Build and run the project

## Requirements

- Android Studio Arctic Fox or later
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Kotlin 1.9+
- Gradle 8.0+

## API

This project uses [The Movie Database (TMDB) API](https://www.themoviedb.org/documentation/api). You'll need to obtain an API key to run the application.

## License

This project is open source and available under the [MIT License](LICENSE).

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Acknowledgments

- Movie data provided by [TMDB](https://www.themoviedb.org/)
- Icons and images from the respective sources
