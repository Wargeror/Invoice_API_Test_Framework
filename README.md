# Invoice API Test Framework

This project is an automated API testing framework built to validate the endpoints of the **Inv.bg API** (specifically focusing on the `v3` endpoints). It utilizes modern Java testing tools to ensure robust, repeatable, and easily maintainable API integration tests.

## 🛠️ Technology Stack

*   **Java 25**: The core programming language.
*   **JUnit 5 (Jupiter)**: The testing framework used for writing and executing the tests.
*   **RestAssured**: A Java DSL for simplifying testing of REST based services. Used for crafting HTTP requests and parsing responses.
*   **Gson**: Used for serializing Java objects to JSON payloads and deserializing JSON responses back into Java objects (DTOs).
*   **Lombok**: Reduces boilerplate code (getters, setters, constructors) in the Data Transfer Objects (DTOs).
*   **Log4j 2**: Provides detailed logging of test execution, storing logs in both the console and a dedicated log file (`logs/api-tests.log`).
*   **Maven**: Dependency management and build automation.

## 📁 Project Structure

The project follows a clean, modular architecture, separating data models, API interactions, and the tests themselves:

```text
src/test/java/api/
├── dto_data_transfer_object/   # POJOs representing the JSON structures for requests/responses
│   ├── Client.java
│   ├── Credentials.java
│   ├── Directory.java
│   ├── File.java
│   ├── Invoice.java
│   ├── InvoiceItem.java
│   └── Item.java
├── endpoints/                  # Classes handling the actual HTTP requests using RestAssured
│   ├── ClientAPI.java
│   ├── DirectoryAPI.java
│   ├── FileAPI.java
│   ├── InvoiceAPI.java
│   ├── ItemAPI.java
│   └── LoginAPI.java
├── tests/                      # The JUnit 5 test classes validating the endpoints
│   ├── ClientAPITest.java
│   ├── DirectoryAPITest.java
│   ├── FileAPITest.java
│   ├── InvoiceAPITest.java
│   ├── ItemAPITest.java
│   └── LoginAPITest.java
└── utils/                      # Utility classes for data generation and token management
    ├── Input.java              # Reads configuration properties
    ├── TokenManager.java       # Singleton manager that caches the auth token across tests
    └── Utils.java              # Generators for random strings, Bulgarian EGNs, Bulstats, and VATs
```

## ⚙️ Configuration

Before running the tests, you must configure your API credentials.

1.  Locate the `config.properties` file in the root directory of the project.
2.  Update the file with your valid Inv.bg credentials:

```properties
username=your_email@example.com
password=your_password
domain=your_company_domain
base_uri=https://api.inv.bg
```

*Note: The `base_uri` should point to the production or staging API environment as required (default is `https://api.inv.bg`).*

## 🚀 Running the Tests

You can run the tests using your IDE (like IntelliJ IDEA) or via the command line using Maven.

**Using Maven:**

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

## 🔑 Key Features

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

### Thread-Safe Token Management (`TokenManager.java`)
To prevent redundant login requests and support parallel execution, the framework implements a thread-safe Singleton `TokenManager`. It fetches an authentication token once, caches it, and reuses it for all subsequent API calls across different test classes until the token is near expiration. The `getToken()` method is `synchronized` to prevent race conditions where multiple tests might otherwise try to fetch a new token simultaneously.

### Data Generation (`Utils.java`)
Tests utilize dynamic data generation to ensure tests are repeatable and don't fail due to data collisions (e.g., trying to create an item with a name that already exists). The `Utils` class provides methods to generate:
*   Random alphanumeric strings.
*   Valid Bulgarian EGNs (Unified Civil Numbers).
*   Valid 9-digit Bulstat numbers.
*   Valid VAT numbers (BG + Bulstat).

### Detailed Logging (`log4j2.xml`)
The framework is configured with Log4j 2. Execution details, including test start/finish markers, payload details, and clear failure reasons, are logged to the console and saved to `logs/api-tests.log`.

## 📝 Test Coverage

The framework currently covers the "Happy Path" for the following major endpoints:

*   **Login (`/login/token`)**: Valid authentication and error handling for missing/invalid credentials.
*   **Clients (`/clients`)**: Creating, listing, partial updating (PATCH), and deleting clients.
*   **Items (`/items`)**: Creating, listing, full updating (PUT), partial updating (PATCH), and deleting items.
*   **Invoices (`/invoices`)**: Creating (with items), listing, updating, and deleting invoices.
*   **Directories (`/directories`)**: Creating, listing, updating, and deleting directories.
*   **Files (`/files`)**: Uploading (multipart/form-data), listing, retrieving, downloading, and deleting files.