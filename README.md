# Loan Process Service

Backend service for a loan approval workflow built for the Coop Pank software developer internship assignment.

## Table of Contents

| Section                        | Link                                                              |
| ------------------------------ | ----------------------------------------------------------------- |
| Prerequisites                  | [Prerequisites](#prerequisites)                                   |
| Quick Start (Docker Compose)   | [Quick Start (Docker Compose)](#quick-start-docker-compose)       |
| API Documentation              | [API Documentation](#api-documentation)                           |
| Main Features                  | [Main Features](#main-features)                                   |
| Business Rules and Constraints | [Business Rules and Constraints](#business-rules-and-constraints) |
| Endpoints                      | [Endpoints](#endpoints)                                           |
| Database Schema Overview       | [Database Schema Overview](#database-schema-overview)             |
| Database Migrations            | [Database Migrations](#database-migrations)                       |
| Assignment Coverage            | [Assignment Coverage](#assignment-coverage)                       |
| Optional Tasks                 | [Optional Tasks](#optional-tasks)                                 |
| Troubleshooting                | [Troubleshooting](#troubleshooting)                               |
| Notes                          | [Notes](#notes)                                                   |
| Author                         | [Author](#author)                                                 |

## Prerequisites

- Docker
- Docker Compose (included with Docker Desktop)

## Quick Start (Docker Compose)

Clone and run:

```bash
git clone https://github.com/tugilane/loan-approval-service.git
cd loan-process-service
docker compose up -d --build
```

## API Documentation

- API Base URL: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Main Features

- Loan application submission with validation
- Estonian personal code validation (format, date of birth, checksum)
- Configurable max applicant age via `loan.max-age`
- Automatic rejection when applicant is older than configured max age
- Single active application per customer (IN_REVIEW)
- Annuity payment schedule generation and persistence
- Review endpoints to approve or reject IN_REVIEW applications
- Global exception handling with `@RestControllerAdvice`
- Unit tests for core service logic using Mockito mocks

## Business Rules and Constraints

### Application Input

- firstName: max 32 chars, not blank
- lastName: max 32 chars, not blank
- personalCode: exactly 11 digits and must be a valid Estonian personal code
- loanPeriodMonths: min 6, max 360
- interestMargin: min 0.0, max 10.0
- baseInterestRate: min 0.0, max 10.0
- loanAmount: min 5000.00, max 9999999999999.99
- Every application has a unique database ID

### Age Check

- Applicant age is derived from personal code date of birth
- If age is greater than `loan.max-age`, application is auto-rejected with `CUSTOMER_TOO_OLD`
- Default value: `loan.max-age=70`

### Payment Schedule

- Generated only for applications with status `IN_REVIEW`
- Stored in `payment_schedule`
- First payment date defaults to `LocalDate.now()`
- Final payment row adjusts rounding remainder so remaining balance reaches `0.00`

### Review Flow

- Approve endpoint allowed only for `IN_REVIEW` applications
- Reject endpoint allowed only for `IN_REVIEW` applications
- Final states are `APPROVED` or `REJECTED`

## Endpoints

- `POST /apply` - Submit a new loan application
- `GET /applications/{id}` - Get one application with payment schedule
- `GET /applications` - List all applications (paged)
- `GET /applications/in-review` - List IN_REVIEW applications (paged)
- `POST /applications/{id}/approve` - Approve an application
- `POST /applications/{id}/reject` - Reject an application with a reason

Paging query parameters for list endpoints:

- `page` (default `0`)
- `size` (default `20`)
- `sortBy` (default `id`)
- `direction` (default `DESC`)

## Database Schema Overview

Main tables:

- `loan_applications`
	- Stores one row per loan application
	- Includes personal data, loan input, status, and rejection reason
- `payment_schedule`
	- Stores generated monthly payment rows
	- Linked to `loan_applications` through `application_id`

Relationship:

- One application can have many schedule rows

Indexes:

- `idx_application_personal_code_status` on (`personal_code`, `status`)
- `idx_schedule_row_application_id_payment_number` on (`application_id`, `payment_number`)

## Database Migrations

Liquibase master changelog:

- `src/main/resources/changelog/changelog-master.yml`

Included migrations:

- `001-init.yml`
- `002-init.yml`
- `003-init.yml`

## Assignment Coverage

### Mandatory Business Requirements

Implemented:

- Loan application submission with required core fields and validation
- Data persisted with unique ID
- One active `IN_REVIEW` application per customer
- Age check with configurable max age
- Automatic rejection for too old customer (`CUSTOMER_TOO_OLD`)
- Annuity schedule generation and persistence
- First payment date defaults to today
- `IN_REVIEW` review step with approve/reject actions

### Mandatory Technical Requirements

Implemented:

- Java 25
- Spring Boot 4.x
- PostgreSQL
- Liquibase migrations
- OpenAPI 3 with Swagger UI
- Docker/Compose runtime

## Optional Tasks

Implemented:

- Global exception handling with `@RestControllerAdvice`
- Unit tests with Mockito for service layer

## Troubleshooting

If port `8080` is already in use, change app mapping in `docker-compose.yml` (for example `8081:8080`) and use port `8081` in URLs.

If you suspect stale build cache after migration or config changes:

```bash
docker compose down -v
docker compose build --no-cache app
docker compose up -d
```

## Notes

- Approximate total time spent: 30 hours
- Main challenge: rounding behavior in schedule calculations.
- Planned future work is tracked in the issue tracker
- AI assistance was used for Swagger/OpenAPI annotation cleanup, test generation support, core logic improvements, and refining the `@RestControllerAdvice` approach

## Author

Marten Peljo
