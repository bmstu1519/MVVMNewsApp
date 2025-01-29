<div align="center">


<!--  <p><img src="https://github.com/bmstu1519/MVVMNewsApp/blob/master/app/src/main/res/drawable/no_content.png" width="200"></p> /> -->

# MVVMNewsApp

[![Android](https://img.shields.io/badge/Android-grey?logo=android&style=flat)](https://www.android.com/)
[![AndroidAPI](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/kotlin-2.1.0-blue.svg?logo=kotlin)](https://kotlinlang.org)


<p align="center"> 

  <a href=''>
    <img alt='Download APK' src='https://github.com/bmstu1519/MVVMNewsApp/blob/master/.github/assets/icons/img.png' height=75/></a>
</p>


</div>
 
----

## Features

- **Breaking News**: real time updates news
- **Favourites News**: save your favorite articles
- **Search News**: search news by keywords
- **Preload article page** for better user experience

<br>

<p float="center">
  <img src="https://github.com/bmstu1519/MVVMNewsApp/blob/master/.github/assets/screenshots/breaking_news.png" width="33%" />
  <img src="https://github.com/bmstu1519/MVVMNewsApp/blob/master/.github/assets/screenshots/saved_news.png" width="33%" />
  <img src="https://github.com/bmstu1519/MVVMNewsApp/blob/master/.github/assets/screenshots/search_news.png" width="33%" />
<!--   <img src="https://github.com/bmstu1519/MVVMNewsApp/blob/master/app/src/main/res/drawable/no_content.png" width="23%" /> -->
</p>


### Architecture
This application leverages [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) principles: </br>
-  _presentation(ui)_: Android module **MVVM**-based. It contains the Android UI framework components (Activitie, Fragments, ViewModels...). This module just observes data coming from the undelying modules through Kotlin Flows and displays it.
-  _domain_:
    * **Use Cases**: Platform-independent business logic;
    * **Models**: Platform-independent business models;
    * **Repository Interfaces**: Act as abstract definitions for data operations, following the Repository pattern to isolate domain logic from data source implementations;
-  _data_: Android module that acts as the **Single-Source-Of-Truth (SSOT)** of the App. It contains Repositories implementation, the Room Entities for persistence, the data source Api implementations and the corresponding api-specific models.
-  _di_: It contains all the dependency injection related classes and interfaces.


<div align="center">
  <img src="https://github.com/bmstu1519/MVVMNewsApp/blob/master/.github/assets/diagrams/project_structure.png" width="50%">
  <br>
  <em>Diagram 1: Application Structure Overview</em>
</div>


### MVVM Architecture Pattern

The application implements the [MVVM](https://developer.android.com/topic/architecture) (Model-View-ViewModel) architectural pattern, which provides:

- Clear separation of concerns between UI and business logic
- Improved testability through the separation of UI and business logic
- Better state management and data consistency
- Lifecycle-aware components

The MVVM implementation consists of:

- **Model**: Represents the data and business logic of the application
- **View**: Represents the UI layer (Activities/Fragments) that observes ViewModel
- **ViewModel**: Acts as a bridge between Model and View, managing UI-related data and business logic

The ViewModel communicates with the View through data streams, maintaining a clear separation between the UI and business logic layers.

<div align="center">
  <a href="https://github.com/bmstu1519/MVVMNewsApp/blob/master/.github/assets/diagrams/architecture_mvvm%2Bclean2.png" target="_blank">
    <img src="https://github.com/bmstu1519/MVVMNewsApp/blob/master/.github/assets/diagrams/architecture_mvvm%2Bclean2.png" 
      width="1200">
  </a>
  <br>
  <em>Diagram 2: Detailed Component Interaction Schema (click to enlarge)</em>
</div>

## Technical Details
### UI
- Android View System
- Material Design 3
- View Binding
### Core
- Kotlin
- Coroutines
- Flow
- Kotlin-DSL
### Dependency Injection
- Dagger2
### JetPack
- ViewModel
- Room
- Navigation
- Paging 3
### Network
- Retrofit
- OkHttp
### Image Loading
- Glide

## Powered By

- [News API](https://newsapi.org)

## TODO List

### Completed ✅
- [x] App version updated to 2.0.0
- [x] Migrated to Kotlin DSL
- [x] Implemented Version Catalog for build management
- [x] Replaced Kotlin synthetics with ViewBinding
- [x] Migrated to KSP for Room and Glide
- [x] Updated Kotlin to 2.1.0
- [x] Updated Gradle to 8.5
- [x] Updated Java to 17
- [x] Implemented Clean Architecture and MVVM
- [x] Integrated dependency injection with DependencyProvider
- [x] Refactored UI to Material 3

### In Progress 🚧
- [ ] Implement Dagger2 dependency injection
- [ ] Add system-wide dark theme support
- [ ] Setup pages preloading with WorkManager
- [ ] Integrate Chrome Custom Tabs for web content