# Spring Boot Starter Project

A Spring Boot starter project with built-in authentication, user management, and various integrations ready to use.

## Features

- **Authentication & Authorization**
  - JWT-based authentication with refresh tokens
  - Role-based access control
  - OTP (One-Time Password) authentication
  - Password reset functionality

- **User Management**
  - User registration and profile management
  - Secure password handling with BCrypt

- **Security**
  - CORS configuration
  - Comprehensive security headers (HSTS, CSP, etc.)
  - Stateless session management

- **Integrations**
  - Email service (SMTP)
  - SMS service (Msegat)
  - S3 compatible storage (Digital Ocean Spaces or AWS)

- **Database**
  - JPA/Hibernate ORM
  - H2 database for development
  - PostgresSQL for production (You can use any other database)

- **Modern Java Features**
  - Virtual Threads support (Java 21)
  - Async processing

## Technologies

- Java 21
- Spring Boot 3.4.4
- Spring Security
- Spring Data JPA
- JWT (JSON Web Tokens)
- Lombok
- AWS S3 SDK
- H2 Database (Development)
- PostgresSQL (Production)

## Getting Started

### Prerequisites

- Java 21
- Maven

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/d11l/spring-boot-starter.git
   cd spring-boot-starter
   ```

2. Configure application properties:
   - Update `src/main/resources/application.properties` with your settings
   - For production, update `src/main/resources/application-production.properties`

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Configuration

### Database Configuration

The project uses an H2 in-memory database for development

```properties
spring.datasource.url=jdbc:h2:mem:starter
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

For production, the application uses PostgresSQL (You can use any other database)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/starter
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=root
```

### S3 Storage Configuration

Configure your S3-compatible storage (like Digital Ocean Spaces)

```properties
s3.space.key=YOUR_KEY
s3.space.secret=YOUR_SECRET
s3.space.endpoint=REGION.DOMAIN.TLD
s3.space.region=REGION
s3.space.bucket=BUCKET_NAME
s3.space.base-url=https://BUCKET_NAME.REGION.DOMAIN.TLD
```

### Email Configuration

```properties
spring.mail.host=HOST
spring.mail.port=587
spring.mail.username=no-reply@DOMAIN.TLD
spring.mail.password=PASSWORD
spring.mail.protocol=smtp
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### SMS Configuration (Msegat)

```properties
msegat.api.url=https://api.msegat.com
msegat.username=YOUR_USERNAME
msegat.sender.id=YOUR_SENDER_ID
msegat.api.key=YOUR_API_KEY
```

### JWT Configuration

```properties
jwt.secret=YourSecretKeyHereMakeItLongAndComplexForBetterSecurity
jwt.expiration=86400000
jwt.refresh.expiration=604800000
```

## API Endpoints

### Authentication

- `POST /api/v1/auth/login` - Standard login
- `POST /api/v1/auth/login-otp` - OTP-based login
- `POST /api/v1/auth/request-otp` - Request an OTP
- `POST /api/v1/auth/register` - Register a new user
- `POST /api/v1/auth/refresh-token` - Refresh JWT token
- `POST /api/v1/auth/forgot-password` - Initiate password reset
- `POST /api/v1/auth/reset-password` - Reset password with OTP

### User Management

- `POST /api/v1/user/reset-password` - Reset user password
- `POST /api/v1/user/update-profile` - Update user profile

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Contact

Developer - [abdulrahman.radhi.11@gmail.com](mailto:abdulrahman.radhi.11@gmail.com)
