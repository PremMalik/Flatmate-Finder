
# 🏠 Roommate Finder — Spring Boot Backend

A simple and functional backend built with **Spring Boot** and **MongoDB** for a Roommate Finder application.  
It supports **Google OAuth2 Login**, ad posting/editing/deleting, and role-based access for users and admins.

---

## 🚀 Features

- 🔐 **Google Login (OAuth2)**
- 👤 **Role-based access** (Admin / User)
- 📦 **MongoDB Integration**
- 🧩 **RESTful APIs for Ads and Users**
- ✏️ **Create, Edit, and Delete Ads**
- 🍪 **Session-based authentication**
- ⚡ **Frontend Ready** — Works with a simple JS frontend (index.html + app.js)

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-------------|
| Backend | Spring Boot 3, Spring Security |
| Authentication | Google OAuth2 |
| Database | MongoDB |
| Build Tool | Maven |
| Frontend | HTML, CSS, JS (Vanilla) |

---

## 📁 Project Structure

```

RoommateFinder/
├── src/
│   ├── main/
│   │   ├── java/com/roommatefinder/
│   │   │   ├── controller/      # REST Controllers
│   │   │   ├── model/           # Data Models (User, Ad)
│   │   │   ├── repo/            # MongoDB Repositories
│   │   │   ├── service/         # Business Logic
│   │   │   └── config/          # Security + OAuth2 Configuration
│   │   └── resources/
│   │       ├── static/          # index.html, app.js, styles.css
│   │       └── application.yml  # Spring Boot Configuration
└── pom.xml

````

---

## ⚙️ Setup Instructions

### 1️⃣ Prerequisites
- Java 17+
- Maven
- MongoDB running locally
- Google Cloud Console project with OAuth2 credentials


### 3️⃣ Add Configuration

Edit your `application.yml` file in `src/main/resources/`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/roommatefinder

  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
            scope:
              - email
              - profile

admin:
  email: your-admin-email@gmail.com
```

### 4️⃣ Run the Application

```bash
mvn spring-boot:run
```

The app will start at 👉 [http://localhost:8080](http://localhost:8080)

---

## 🔑 API Endpoints

| Method | Endpoint        | Description             |
| ------ | --------------- | ----------------------- |
| GET    | `/api/ads`      | Fetch all ads           |
| POST   | `/api/ads`      | Create a new ad         |
| PUT    | `/api/ads/{id}` | Update ad               |
| DELETE | `/api/ads/{id}` | Delete ad               |
| GET    | `/api/user/me`  | Get logged-in user info |

---

## 🧠 Notes

* First login with Google creates the user in MongoDB automatically.
* Only **Admins or Ad Owners** can edit or delete ads.
* Frontend communicates via `fetch()` API using session cookies.

---

## 📸 Screenshots Uploaded Soon

* Google Login
* Ads listing
* Posting a new ad

---

## 🤝 Contributing

Feel free to fork this repository and submit pull requests for improvements or new features.

---

## 🧑‍💻 Author

**Prem Malik**
💼 [LinkedIn](https://www.linkedin.com/](https://www.linkedin.com/in/prem-malik/)) | 🐙 [GitHub](https://github.com/PremMalik)

---

## 🪪 License

This project is licensed under the **MIT License**.
