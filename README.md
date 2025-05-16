# DeepHire Mobile

![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?logo=kotlin&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

**DeepHire Mobile** is a modern Android application built with Kotlin, showcasing core mobile development concepts through a job search platform. Integrated with the Adzuna API, it provides real-time job listings with a sleek, user-friendly interface.

## 📱 Features

- **Real-Time Job Listings**: Fetches up-to-date job postings using the Adzuna API.
- **Efficient RecyclerView**: Displays job listings with smooth scrolling and optimized performance.
- **Custom Adapter**: Implements view recycling for seamless data presentation.
- **Smart Location Search**: AutoCompleteTextView for intuitive location-based job filtering.
- **Robust Networking**: Uses Retrofit for reliable API communication.
- **Error Handling**: Gracefully falls back to mock data during network failures.
- **Modern UI**: Clean, responsive design following Material Design guidelines.

## 🛠 Tech Stack

- **Language**: java
- **Platform**: Android (min SDK 21)
- **Networking**: Retrofit, OkHttp
- **UI Components**: RecyclerView, AutoCompleteTextView, Material Components
- **API**: Adzuna Job Search API
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Manual (Hilt or Koin can be added)

## 📸 Screenshots

| Home Screen | Search Results | Location Filter |
|-------------|----------------|-----------------|
| ![Home](screenshots/home.png) | ![Results](screenshots/results.png) | ![Filter](screenshots/filter.png) |

> *Note*: Replace the placeholder screenshot paths with actual images uploaded to your repo under a `screenshots/` folder.

## 🚀 Getting Started

### Prerequisites

- Android Studio (latest stable version)
- JDK 17 or higher
- Adzuna API key (sign up at [Adzuna Developer Portal](https://developer.adzuna.com/))

### Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/AMIROUNI/deephire_mobile.git
   cd deephire_mobile
