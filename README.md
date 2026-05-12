# Coupon management
REST service responsible for managing discount coupons

## Tech stack
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white&style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?logo=springboot&logoColor=white&style=flat-square)
![Java](https://img.shields.io/badge/Java_21-ED8B00?logo=openjdk&logoColor=white&style=flat-square)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white&style=flat-square)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?logo=flyway&logoColor=white&style=flat-square)
![Gradle](https://img.shields.io/badge/Gradle-02303A?logo=gradle&logoColor=white&style=flat-square)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white&style=flat-square)

## Usage
Required: Docker

```bash
docker-compose up --build
```

App starts on: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui.html

## Project decisions
### Architecture
I used the hexagonal architecture because of the adapters' interchangeability. The geolocation feature in the project uses Maxmind, but it can easily be swapped out for any external API without touching the business logic. The same applies to the database - it’s easy to switch to a different database system.
In addition, testability is much easier. The services are unaware of infrastructure concerns like JPA or HTTP. They can be unit tested. Tests are fast and isolated.
For such a small project, hexagonal architecture might seem like a bit of overengineering, but adopting it early would pay off as the project grows.

### Concurrency
Coupon redemption doesn't follow the typical read-then-update pattern, which would introduce a race condition under concurrent requests. Instead, redemption is handled with a single atomic SQL query that increments the counter only if it hasn't reached the limit yet. If the query affects 0 rows, the coupon is exhausted. No locks, no retries.

### Geolocation
Geolocation is resolved using a local MaxMind GeoLite2 database rather than an external API. I initially considered using an external geolocation API, but the free tier rate limits were too restrictive for any meaningful use and could render the application unusable under moderate traffic. The local database eliminates network latency on every redemption request, removes the risk of third-party downtime, and avoids rate limits. The downside is that the .mmdb file needs to be downloaded manually and updated periodically to stay accurate.

### One usage per-user
Each coupon can only be redeemed once per user. This is enforced at two levels - the application checks before attempting redemption, and the database has a UNIQUE (coupon_id, user_id) constraint as a safety net. Even if two concurrent requests for the same user slip past the application-level check simultaneously, the constraint guarantees that only one redemption goes through.

## What can be added in the future
- Expiry date on coupon
- Redis cache for hot coupons
- Micrometer metrics
- Unique constraint on (code, country_code) instead of code alone - currently the same coupon code cannot exist for multiple countries.

---
Created by <a href="https://github.com/mbalcer"> mbalcer </a>