# Narrativo - Full-Stack Blogging Platform

## Overview
Narrativo is a full-stack blogging platform built with **Spring Boot (Java)** and **Angular**. It provides user authentication, blog management, customizable visibility settings, and dynamic dashboards for insights.

## Tech Stack
- **Backend:** Spring Boot, Spring Security, JPA (Hibernate), PostgreSQL
- **Frontend:** Angular, TypeScript, Angular Material
- **Database:** PostgreSQL
- **Build Tools:** Maven

## Features
### 1. User Authentication
- JWT-based authentication with Spring Security
- User registration & login
- Route guards for protected pages

### 2. Blog Management
- Create, edit, delete blogs
- Public & private blog visibility
- Fetch blogs by author or visibility

### 3. Dynamic Dashboards
- View total blogs, user activity, and analytics
- Role-based dashboard for admin and users

### 4. Customizable Visibility
- Set blogs as **Public, Private, or Friends-only**
- Control access based on visibility settings

## Installation & Setup
### Backend (Spring Boot)
1. Clone the repository:
   ```sh
   git clone https://github.com/alifiyah29/Narrativo.git
   cd Narrativo/backend
   ```
2. Configure PostgreSQL in `application.properties`.
3. Build & run the backend:
   ```sh
   mvn spring-boot:run
   ```

### Frontend (Angular)
1. Navigate to the frontend directory:
   ```sh
   cd ../frontend
   ```
2. Install dependencies:
   ```sh
   npm install
   ```
3. Run the Angular app:
   ```sh
   ng serve
   ```

## API Endpoints
- **Auth APIs:**
  - `POST /auth/register` - Register new users
  - `POST /auth/login` - Authenticate users
- **Blog APIs:**
  - `POST /blogs` - Create a new blog
  - `GET /blogs` - Get all blogs (filtered by visibility)
  - `PUT /blogs/{id}` - Edit a blog
  - `DELETE /blogs/{id}` - Delete a blog

## License
MIT License

