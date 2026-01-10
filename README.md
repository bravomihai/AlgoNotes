AlgoNotes is a lightweight desktop application built with JavaFX and SQLite that helps programmers organize competitive programming problems and personal notes.
It allows users to manage sites, problems, and notes in a structured way, making it easier to track progress, difficulty, and insights across competitive programming platforms like Codeforces and AtCoder.

## Dependencies (JAR files)

This project does NOT include external JAR files in the repository.
All dependencies must be added locally.

---

## Required

- Java JDK 25
- JavaFX SDK 25.0.1
- SQLite JDBC 3.46.0.1
- SLF4J 2.0.17 (logging)

---

## Download links

### JavaFX SDK
https://jdk.java.net/javafx25/

After extracting, add the `lib/` folder as a library in your IDE.

---

### SQLite JDBC
https://github.com/xerial/sqlite-jdbc/releases  
Download: sqlite-jdbc-3.46.0.1.jar

---

### SLF4J
https://www.slf4j.org/download.html  
Download:
- slf4j-api-2.0.17.jar
- slf4j-simple-2.0.17.jar

---

## Running the application

When running the application, add the following VM options:

--module-path /path/to/javafx-sdk-25.0.1/lib
--add-modules javafx.controls,javafx.fxml

---

## Notes

- JAR files are NOT committed to Git.
- Dependencies are expected to be available locally.
- The out/ and .idea/ directories are excluded from version control.

