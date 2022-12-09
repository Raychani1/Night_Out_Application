# Night Out Application

## **About The Project**

The main objective of this Project is to create an Android application in [Kotlin](https://kotlinlang.org/) for the [Mobile Computing](https://uim.fei.stuba.sk/predmet/i-mobv/) Course at [FEI STU](https://www.fei.stuba.sk/english.html?page_id=793). The theme of the application is based around different kinds of businesses (e.g. Bar, Pubs, Cafés, Nightclubs etc.) and social interaction between users.

### **Features**

* **User Authentication** - The application supports user registration and login. Bearer, User and Refresh Tokens are used to enhance the security aspects of this application and also to provide a way of persistency. 

* **Display of Businesses** - The application allows to display every business if there are active users.

* **Display Businesses Details** - The application provides a way to display business details such as Number of Active Users, GPS Location, Website, Phone number or Opening Hours.

* **Location-based check in system** - The application allows users to check in to let their friends know where they are and potentially meet up.

* **Friends and Social Interaction** - The application also supports adding Friends and sharing your location with them (in the form of Business you are currently checked into).

### **Built With**

* [![Kotlin][Kotlin]][Kotlin-url]
* [![SQLite][SQLite]][SQLite-url]
* [![Gradle][Gradle]][Gradle-url]


## **Getting Started**

To get a local copy up and running follow these simple steps.

### **Prerequisites**

* **Android Studio** - In order to build the applications source code you will need to have Android Studio installed. Follow the instructions on the [Official Website](https://developer.android.com/studio).

## **Usage**

1. Clone the repo.
   ```sh
   git clone https://github.com/Raychani1/Night_Out_Application
   ```
2. Add the API Key to `local.properties` file the following way.
    ```
    API_KEY=YOUR_API_KEY
    ```
3. Build the Project.


## **License**

Distributed under the **MIT License**. See [LICENSE](https://github.com/Raychani1/Night_Out_Application/blob/main/LICENSE) for more information.


## **Acknowledgments**

* [Maroš Čavojský: MOBV2022](https://github.com/marosc/mobv2022)
* [Omondi Alex: Creating Custom Extendable and Expandable Floating Action Button in Android Using Kotlin](https://www.section.io/engineering-education/creating-custom-expandable-fab/)
* [foxandroid: BottomNavYTKotlin](https://github.com/foxandroid/BottomNavYTKotlin)

<!-- MARKDOWN LINKS & IMAGES -->
[Kotlin]: https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white
[Kotlin-url]: https://kotlinlang.org/
[SQLite]: https://img.shields.io/badge/sqlite-%2307405e.svg?style=for-the-badge&logo=sqlite&logoColor=white
[SQLite-url]: https://www.sqlite.org/index.html
[Gradle]: https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white
[Gradle-url]: https://gradle.org/
