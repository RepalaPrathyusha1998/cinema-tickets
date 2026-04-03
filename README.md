# Cinema Ticket Service

## Overview

This project is a **Java 21 implementation**  of a Cinema Ticket service.

The goal is to take a ticket request, validate it based on business rules, calculate the total cost and number of seats, and then call external services to process payment and reserve seats.

---

## Ticket Types

- Adult – £25
- Child – £15
- Infant – £0 (no seat required)

---

## Business Rules

The following rules are implemented:

- A maximum of 25 tickets can be purchased in a single request
- Child and Infant tickets cannot be purchased without at least one Adult ticket
- Infants do not pay and do not require a seat
- Account ID must be valid (greater than 0)

---

## Approach

- I used a **fail-fast validation approach** to stop processing as soon as an invalid request is detected
- Validation logic is split into **small methods** to keep the code readable
- A **single loop** is used to gather ticket summary (total tickets, adult count, etc.)
- Payment and seat calculation are handled separately for clarity
- External services are treated as dependencies and mocked in tests

---

## Tech Stack

- Java 21
- Maven
- JUnit 5
- Mockito

---

## Running the Project

### 1. Clone the repository

To clone the repository:

```bash
git clone https://github.com/your-username/cinema-tickets.git
cd cinema-tickets
```

### 2. Build the project

Run the Maven build to download dependencies and compile the code:

```bash
mvn clean install
```

### 3. Run Tests

This project is a service layer implementation, so it is mainly tested via unit tests.
You can validate functionality using:

```bash
mvn test
```

---

## Future Improvements

- Add more comprehensive test coverage for edge cases
- Introduce logging for better traceability
- Improve validation by returning detailed error responses
- Consider separating validation into a dedicated component
