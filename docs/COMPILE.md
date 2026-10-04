# Compiling FastEnvironment

`FastEnvironment` utilizes the modern Java 21+ **Foreign Function & Memory API (FFM)**.

> [!NOTE]
> FastEnvironment requires **NO native C/C++ compiler**, **NO Visual Studio MSVC**, and **NO native DLL compilation**. It dynamically links into Windows operating system DLLs (`kernel32.dll` and `user32.dll`) directly at JVM runtime!

---

## Prerequisites

- **Java Development Kit**: JDK 21 or newer (e.g. OpenJDK 21, Oracle JDK 21).
- **Apache Maven**: Version 3.8+ (accessible via `PATH`).

---

## 1. Building FastEnvironment from Source

Run Maven to package the core library:

```powershell
mvn clean package -DskipTests
```

This compiles `fastenvironment` classes and outputs `target/FastEnvironment-0.1.0.jar`.

---

## 2. Running the Interactive Demo

Launch the interactive showcase CLI launcher:

```powershell
.\run-demo.bat
```

Or execute directly with Java 21:

```powershell
java --enable-preview --enable-native-access=ALL-UNNAMED -cp target/FastEnvironment-0.1.0.jar fastenvironment.demo.Demo
```

---

## 3. Running the JMH Microbenchmarks

```powershell
.\run-benchmark.bat
```
