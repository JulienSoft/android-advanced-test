# Chargemap Senior Android Technical Test

## Overview
This project is a technical test for senior Android developer positions at Chargemap. It's designed to evaluate advanced Android development skills, architectural decisions, and code quality standards.

## Requirements
Create an Android application that displays charging stations on a map and their details. The app should demonstrate your expertise in:

### Technical Requirements
- Modern Android development with Kotlin
- Clean Architecture principles
- Dependency Injection
- Reactive programming
- Unit testing and UI testing
- Custom UI components
- Memory management and performance optimization

### Features
1. **Map View**
   - Display charging stations on a map
   - Implement custom markers with station status indicators
   - Handle map state preservation across configuration changes
   - Implement efficient viewport-based data loading

2. **Station Details**
   - Create a bottom sheet with smooth transitions and gestures
   - Display comprehensive station information
   - Implement real-time availability updates
   - Handle deep linking to station details

3. **Search & Filters**
   - Implement advanced search with auto-suggestions
   - Add multiple filter options (connector types, power, availability)
   - Create an efficient filter UI with proper state management

## Technical requirements
- Use Compose UI only
- Use Kotlin Coroutines and Flow for asynchronous operations
- Use Compose Navigation
- Implement proper error handling and recovery
- Implement proper memory management for resources
- Add proper logging and crash reporting setup

## Evaluation Criteria
- Architecture design and implementation
- Code quality and organization
- Testing strategy and implementation
- Git commit history and documentation
- UI/UX implementation

## Bonus Points
- Map markers clustering
- Caching strategies
- Custom animations and transitions

## Submission
- Fork this repository
- Create a feature branch
- Submit a pull request with your implementation
- The test should be completed within 5 days

## API Documentation
API documentation and endpoints are available at: https://openchargemap.org/site/develop/api

## Versions:
- Kotlin : 1.9.22
- Gradle : 8.7
- Android Gradle plugin : 8.5.2
- Compile and run on Android Studio Koala | 2024.1.1 Patch 1