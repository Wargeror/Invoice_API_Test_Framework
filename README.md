# Invoice API Test Framework

This project is an automated API testing framework built to validate the endpoints of the **Invoice API** (specifically focusing on the `v3` endpoints). It utilizes modern Java testing tools to ensure robust, repeatable, and easily maintainable API integration tests.

## 🛠️ Technology Stack

*   **Java 25**: The core programming language.
*   **JUnit 5 (Jupiter)**: The testing framework used for writing and executing the tests.
*   **RestAssured**: A Java DSL for simplifying testing of REST based services. Used for crafting HTTP requests and parsing responses.
*   **Allure Framework**: An open-source framework for creating detailed and interactive test reports.
*   **Gson**: Used for serializing Java objects to JSON payloads and deserializing JSON responses back into Java objects (DTOs).
*   **Lombok**: Reduces boilerplate code (getters, setters, constructors) in the Data Transfer Objects (DTOs).
*   **Log4j 2**: Provides detailed logging of test execution, storing logs in both the console and a dedicated log file (`logs/api-tests.log`).
*   **Maven**: Dependency management and build automation.

## 📁 Project Structure

The project follows a clean, modular architecture, separating data models, API interactions, and the tests themselves:

```text
src/test/java/api/
├── base/                       # Base classes for tests
│   └── BaseTest.java           # Contains common @BeforeEach/@AfterEach setup and teardown logic
│   └── ...
├── dto_data_transfer_object/   # POJOs representing the JSON structures for requests/responses
│   ├── Client.java
│   ├── Credentials.java
│   └── ...
├── endpoints/                  # Classes handling the actual HTTP requests using RestAssured
│   ├── Endpoint.java           # An enum that centralizes all API endpoint paths
│   ├── ClientAPI.java
│   └── ...
├── tests/                      # The JUnit 5 test classes validating the endpoints
│   ├── ClientAPITest.java
│   └── ...
└── utils/                      # Utility classes for data generation and token management
    ├── Input.java              # Reads configuration properties
    ├── TokenManager.java       # Singleton manager that caches the auth token across tests
    └── Utils.java              # Generators for random strings, Bulgarian EGNs, Bulstats, and VATs
```

## ⚙️ Configuration

Before running the tests, you must configure your API credentials.

1.  Locate the `config.properties` file in the root directory of the project.
2.  Update the file with your valid credentials:

```properties
username=your_email@example.com
password=your_password
domain=your_company_domain
base_uri=https://api.
```

*Note: The `base_uri` should point to the production or staging API environment as required.*

## 🚀 Running the Tests & Viewing Reports

You can run the tests using your IDE (like IntelliJ IDEA) or via the command line using Maven.

### Running Tests with Maven

To run all tests:
```bash
mvn test
```

To run a specific test class (e.g., Invoice tests):
```bash
mvn test -Dtest=InvoiceAPITest
```

### Running Specific Test Groups (Tags)
The framework uses JUnit 5 `@Tag` annotations to categorize tests. You can run specific suites based on these tags. Currently, tests are tagged with the endpoint they test (e.g., `invoice`, `item`, `client`) and test type categories (`positive`, `negative`, `smoke`, `sanity`).

To run a specific group of tests, use the `groups` parameter with Maven Surefire:

```bash
# Run only the Smoke tests (e.g. core creation operations)
mvn test -Dgroups="smoke"

# Run only Sanity tests (e.g. read and list operations)
mvn test -Dgroups="sanity"

# Run only Invoice endpoint tests
mvn test -Dgroups="invoice"
```

### Viewing the Allure Test Report

After the test run is complete, you can generate and serve the Allure report with the following Maven command:

```bash
mvn allure:serve
```

This will open a detailed, interactive HTML report in your default web browser, where you can see test results, steps, severity, and descriptions.

## 🔑 Key Architectural Features

### Parallel Execution
To significantly speed up test suite execution, this framework is configured to run tests in parallel. This is enabled via the `src/test/resources/junit-platform.properties` file. Both test classes and test methods within classes will run concurrently, with the number of threads dynamically determined by the number of available CPU cores.

#### Parallel Execution Control (Maven)
You can override parallel execution behavior via the command line when running tests:

```bash
# Run tests sequentially (disable parallel execution)
mvn test -Djunit.jupiter.execution.parallel.enabled=false

# Run classes in parallel, but methods within them sequentially
mvn test -Djunit.jupiter.execution.parallel.mode.default=same_thread -Djunit.jupiter.execution.parallel.mode.classes.default=concurrent
```

### Centralized Test Lifecycle (`BaseTest.java`)
To avoid code duplication, all test classes extend a `BaseTest` class. This base class uses JUnit 5's `@BeforeEach` annotation to automatically handle token acquisition before each test and `@AfterEach` for logging and cleanup, ensuring tests are clean and focused on their specific logic.

### Type-Safe Endpoints (`Endpoint.java`)
All API resource paths (like `/clients`, `/invoices`, etc.) are centralized in an `Endpoint` enum. This prevents typos and makes the framework easier to maintain, as all paths are managed in a single, type-safe location.

### Allure Reporting
The framework is fully integrated with the Allure Framework. Tests are annotated with `@Epic`, `@Feature`, `@Story`, `@Severity`, and `@Description` to produce a rich, hierarchical, and easy-to-understand report that provides deep insight into test execution.

### Thread-Safe Token Management (`TokenManager.java`)
To prevent redundant login requests, the framework implements a thread-safe Singleton `TokenManager`. It fetches an authentication token once, caches it, and reuses it for all subsequent API calls across different test classes until the token is near expiration. The `getToken()` method is `synchronized` to prevent race conditions where multiple tests might otherwise try to fetch a new token simultaneously.

### Rate Limit Safeguard
Although the API documentation does not specify a rate limit, a 1-second delay (`Thread.sleep(1000)`) has been proactively added in the `BaseTest`'s `tearDown` method. This small pause between tests helps prevent potential `429 Too Many Requests` errors if running a large number of tests in quick succession.

### Data Generation (`Utils.java`)
Tests utilize dynamic data generation to ensure tests are repeatable and don't fail due to data collisions (e.g., trying to create an item with a name that already exists). The `Utils` class provides methods to generate:
*   Random alphanumeric strings.
*   Valid Bulgarian EGNs (Unified Civil Numbers).
*   Valid 9-digit Bulstat numbers.
*   Valid VAT numbers (BG + Bulstat).

### Detailed Logging (`log4j2.xml`)
The framework is configured with Log4j 2. Execution details, including test start/finish markers, payload details, and clear failure reasons, are logged to the console and saved to `logs/api-tests.log`.

## 📝 Test Coverage

The framework currently covers the "Happy Path" (positive scenarios) for the core CRUD operations on the following major endpoints:

*   **Login (`/login/token`)**: Valid authentication and error handling for invalid credentials.
*   **Clients (`/clients`)**: Creating, listing, partial updating (PATCH), and deleting clients.
*   **Items (`/items`)**: Creating, listing, full updating (PUT), partial updating (PATCH), and deleting items.
*   **Invoices (`/invoices`)**: Creating (with items), listing, partial updating (PATCH), and deleting invoices.
*   **Directories (`/directories`)**: Creating, listing, updating, and deleting directories.
*   **Files (`/files`)**: Uploading (multipart/form-data), listing, retrieving, downloading, and deleting files.
