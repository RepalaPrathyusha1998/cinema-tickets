# Cinema Ticket Service

## Overview

This project is a simple implementation of a cinema ticket booking service.

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

## Running the Project

To run the tests:

```bash
mvn clean test
```

## Future Improvements

- Add more comprehensive test coverage for edge cases
- Introduce logging for better traceability
- Improve validation by returning detailed error responses
- Consider separating validation into a dedicated component
