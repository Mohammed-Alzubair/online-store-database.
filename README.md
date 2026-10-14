# Online Store Database

A relational database for an online store, designed from scratch in MySQL and connected to a Java GUI application.

![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

## Overview

This project covers the full database workflow: designing the schema, enforcing business logic inside the database, and connecting it to a desktop application through JDBC.

## Features

- Normalized relational schema with **9 tables**
- **Triggers** that enforce business rules automatically
- **Views** that simplify common queries
- **Indexes** for faster lookups
- Java GUI application connected to the database via JDBC

## Tech Stack

- **Database:** MySQL
- **Application:** Java
- **Connectivity:** JDBC (MySQL Connector/J 9.7.0)

## Repository Structure

```
.
├── online_store_012.sql   # Database script: tables, triggers, views, indexes
├── Main.java              # Java GUI application
└── README.md
```

## Getting Started

### Prerequisites

- MySQL Server 8 or later
- JDK 17 or later
- [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) (not included in this repository)

### 1. Set up the database

Run the script with the MySQL command line, MySQL Workbench, or Navicat:

```bash
mysql -u <username> -p < online_store_012.sql
```

If the script does not create the database itself, create it first and then run the script against it.

### 2. Configure the connection

Open `Main.java` and set the database URL, username, and password to match your MySQL setup.

### 3. Compile and run

Place the Connector/J JAR next to `Main.java`, then run:

```bash
javac -cp ".;mysql-connector-j-9.7.0.jar" Main.java
java -cp ".;mysql-connector-j-9.7.0.jar" Main
```

On Linux or macOS, replace `;` with `:` in the classpath.

## Skills Demonstrated

Relational schema design · SQL triggers, views, and indexes · JDBC · Java GUI development · Technical documentation

## Author

**Mohammed Al-Zubair**
[github.com/Mohammed-Alzubair](https://github.com/Mohammed-Alzubair)
