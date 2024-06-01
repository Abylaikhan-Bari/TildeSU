# TildeSU - Kazakh Language Grammar Exercises App

## Overview
TildeSU is a mobile application designed to enhance the grammar skills of Kazakh language learners. Developed as a part of a final year diploma project at Satbayev University, this app leverages interactive exercises and a user-friendly interface to provide a comprehensive learning experience. The app is built using modern technologies, ensuring high performance and a seamless user experience.

![Overview]([![Tilde-SU-main-1.png](https://i.postimg.cc/8kJC3tTB/Tilde-SU-main-1.png)](https://postimg.cc/Bt3sP5LX))

## Features
- **Grammar Exercises**: Engage with a variety of grammar-related exercises to improve your understanding and usage of the Kazakh language. Exercises include multiple-choice questions, true/false quizzes, image-based quizzes, sentence construction tasks, and vocabulary cards.
- **User Progress Tracking**: Monitor your learning progress with detailed statistics and achievement milestones, tracking completed exercises, quiz scores, and overall progress.
- **Interactive Quizzes**: Test your knowledge with quizzes that adapt to your skill level, providing a dynamic and personalized learning experience.
- **Multimedia Learning Materials**: Learn through text, images, and interactive content to keep engagement high and cater to different learning styles.
- **User Authentication**: Manage your learning experience with secure login and profile management functionalities, ensuring your progress and personal data are safe.
- **Admin Panel**: A separate admin panel built using Flutter allows for efficient management of content, user data, and overall application settings.

![Dictionary Cards](https://ibb.co.com/Gs6w5Db)
![Puzzles](https://ibb.co.com/98SNtWR)
![True or False](https://ibb.co.com/s3115kL)
![Quizzes](https://ibb.co.com/Gc6wy5z)
![Lessons](https://ibb.co.com/swHvSgr)
![Registration](https://ibb.co.com/jgZhfFr)
![Login](https://ibb.co.com/8ss0fg9)

## Installation

### Prerequisites
- Ensure you have the latest version of [Android Studio](https://developer.android.com/studio) and [Flutter SDK](https://flutter.dev/docs/get-started/install) installed.
- An Android device or emulator to run the app.
- A Firebase project setup for authentication and Firestore.

### Kotlin App
1. Clone the repository from GitHub:
    ```bash
    git clone https://gitlab.com/Abylaikhan-Bari/tildesu.git
    ```
2. Open the project in Android Studio.
3. Configure the Firebase project:
    - Add the `google-services.json` file to the `app` directory.
    - Ensure Firebase authentication and Firestore are set up.
4. Build and run the app on an Android device or emulator.


### Flutter Admin Panel
1. Clone the repository from GitHub:
    ```bash
    git clone https://gitlab.com/Abylaikhan-Bari/tildesu-teacher.git
    ```
2. Navigate to the project directory and get the dependencies:
    ```bash
    cd TildeSU_Admin
    flutter pub get
    ```
3. Configure the Firebase project:
    - Add the `google-services.json` file to the `android/app` directory.
    - Add the `GoogleService-Info.plist` file to the `ios/Runner` directory.
4. Run the app on an Android/iOS device or web:
    ```bash
    flutter run
    ```

## Usage

### User Guide
1. **Registration and Login**: Download the app from the Play Store, register with your email, and log in. If you are a new user, complete the registration process by providing the necessary personal information.
2. **Main Interface**: Navigate through the app using the intuitive menu. Access various grammar lessons and exercises from the main interface.
3. **Lessons**: Each lesson contains detailed explanations, examples, and interactive components to help you understand and practice Kazakh grammar.
4. **Exercises**: Complete various exercises to test your knowledge and track your progress. Use the progress journal to monitor your learning journey.
5. **Admin Panel**: The admin panel allows administrators to manage content, track user activities, and update lessons and exercises.


### Technical Specifications
- **Kotlin**: Used for the main Android application, offering concise syntax and full interoperability with Java.
- **Jetpack Compose**: Utilized for creating the UI components of the app, enabling a modern and reactive approach to UI development.
- **Firebase**: Powers authentication, Firestore database, and storage for user data and application content.
- **Flutter**: Used for developing the admin panel, allowing for cross-platform deployment with a single codebase.
- **MVVM Architecture**: Ensures a clean separation of concerns, making the codebase modular, scalable, and easier to maintain.

### Development and Testing
- The application follows a structured development process, including system modeling, UI design, and rigorous testing.
- **Unit Testing**: Conducted on individual components to ensure they work as intended.
- **Integration Testing**: Ensures that different components of the application work together seamlessly.
- **User Acceptance Testing (UAT)**: Performed to ensure the application meets the requirements and provides a satisfactory user experience.

## Contributing
We welcome contributions to enhance the app and fix any issues. Please fork the repository, make your changes, and submit a pull request. Ensure your code follows the project's coding standards and includes relevant tests.

## License & Privacy Policy
For detailed license and privacy policy information, please refer to the [License & Privacy Policy](https://docs.google.com/document/d/1fN2QVP2al9k8MJ5cKqhKCoz3GzDBRTkpUNAVAxChUAY/edit?usp=sharing).

## Contact
For any questions or support, please contact:

- **Abylaikhan Bari**: [Telegram](https://t.me/Abylaikhan_Bari)
- **Ilyas Ashim**: [Telegram](https://t.me/ilyashim)
