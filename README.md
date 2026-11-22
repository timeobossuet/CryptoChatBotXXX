# CryptoChatBotXXX

A decentralized peer-to-peer chat application built in Java 21. 
The project enables local network peer discovery and direct messaging without relying on a central server, 
supporting both a graphical desktop interface (JavaFX) and a command-line interface (CLI).

---

## Project Architecture

### Folder Structure
```text
src/
├── main/
│   ├── java/org/cryptochatbotxxx/
│   │   ├── App.java                  # Application entry point
│   │   ├── config/                   # Configuration** loading
│   │   ├── network/                  # UDP peer discovery, TCP messaging and NetworkManager
│   │   └── view/                     # User interfaces (CliView, GuiView)
│   └── resources/
│       └── config.json               # Default network and application settings
└── test/                             # JUnit test suite
```

### Design Patterns Used
* **Model-View-Controller:** 
    * Network state and messaging logic are strictly decoupled from presentation layers.
* **Observer Pattern:** 
    * Incoming network events notify active views asynchronously.
* **Singleton / Centralized Configuration:** 
    * Application and network parameters are parsed once into immutable configuration objects.

---

## Prerequisites

* **Java Development Kit (JDK) 21** or higher
* **Apache Maven 3.9+**

Verify your installation:
```bash
java -version
mvn -version
```

---

## Compile and Run

### 1. Compile the Project
Clean and build the project using Maven:
```bash
mvn clean compile
```

### 2. Run the Application
Launch the application via the JavaFX Maven plugin:
```bash
mvn clean compile javafx:run
```

Without using JavaFX : 
```bash
mvn clean compile exec:java
```


*(Optional)* To run the test suite:
```bash
mvn test
```

### Configuration
Default ports, broadcast addresses, and timeouts can be adjusted in `src/main/resources/config.json` prior to building.
