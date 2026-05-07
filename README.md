This repository contains the files involved in the 3rd Year Development of Information Systems Project, this involves the work of Group 7, of which includes:
Zainab Muhammad,
Daelyn Salim,
Vasiliqi Macolli,
Lauren Buenagua,
and Dominic Tong.
## Contributors

| Name | Contributions |
|---|---|
| Zainab Muhammad | I* model stategic dependency and strategic rationale (contributer), Automation (as per commits) (leader), forms (contributer), integration  of components (leader), DB creation and handling (leader), operational model (contributer), operational bpmn model (contributer)  |
| Daelyn Salim | Strategic bpmn model (contributer), operational bpmn model (contributer), testing report (leader), forms (contributer), automation (as per commits) (contributer)|
| Vasiliqi Macolli |Strategic bpmn model (leader), operational bpmn model (leader), testing report (contributer), automation (as per commits), DB creation and handling (contributer)  |
| Lauren Buenagua |Strategic bpmn model (contributer), operational bpmn model (contributer), testing report (contributer), automation (as per commits) (contributer) |
| Dominic Tong | I* model stategic dependency and strategic rationale (contributer), Automation (as per commits) (contributer), Testing report (contributer) |

The Automation Folder contains all the relevant code to run. The entity package maps out the database, the repository package makes calls to the database, the service package contains all the business logic, and the worker package makes calls to Camunda. We used a PostGreSQL Database and the schema of the database ER model is linked inside, called "probuilds er model.png"

The BPMN Models Folder contain all relevant BPMN related Models

The Probuild Folder contains all the Operational Model and the Forms

The I* Model Folder contains the i* Model saved as a .txt file, ready to be downloaded and opened within the piStar tool.

The Strategic Dependency Model will be updated and edited via the .txt file itself, to open it download the file and open it within piStar (https://www.cin.ufpe.br/~jhcp/pistar/tool/).

# ProBuilds Tool Management System

A business process automation system built with **Camunda 8**, **Spring Boot**, and **PostgreSQL**, 
designed to manage the end-to-end lifecycle of tool hire and sale operations for a tools retailer.

---

## Overview

This projects automates the full customer journey from tool selection through to order fulfilment, 
integrating membership management, payment processing, credit applications, and inventory tracking 
into a set of coordinated BPMN processes.

---

## Features

### Customer Journey
- In-store and online order flows
- Tool availability checking with real-time stock from individual tool instances
- Dynamic pricing based on tool type, quantity, and hire duration
- Delivery and pickup distribution options with a £4 delivery fee

### Membership & Loyalty
- Member registration with automatic ID generation
- Membership validation against the database
- Loyalty points system — 100 points = 1% discount, max 10%
- £10 membership registration fee added to order total

### Payment Processing
- Cash and card payment support (card only for online orders)
- Card expiry validation
- Credit/finance application flow for orders over £100
- Integration with Fintrust for credit decisions via message events
- 6 and 12 month instalment plans

### Inventory Management
- Tool instance tracking — each physical tool has its own record with a serial number
- Instance status lifecycle: `AVAILABLE → HIRED/SOLD → RETURNED/MAINTENANCE → AVAILABLE/RETIRED`
- Automatic stock count derived from available instances
- Order-to-instance association tracking via `order_tool_instance`

### Hire Management
- Hire order creation with issue date, expected return date, and condition logging
- Rental agreement activation on tool pickup
- Automatic `LATE` status for overdue hire orders via scheduled job
- Tool return flow with condition assessment
- Maintenance transfer workflow with authorisation approval
- Tools marked as `AVAILABLE` or `RETIRED` post-maintenance

### Order Fulfilment
- Separate hire and sale order tables
- Delivery confirmation and pickup notification flows
- Order status tracking: `PENDING → PICKED_UP/DELIVERED`

---

## Tech Stack

| Layer | Technology |
|---|---|
| Process Orchestration | Camunda 8 (Self-Managed) |
| Backend | Java 21, Spring Boot |
| Database | PostgreSQL |
| ORM | Hibernate / Spring Data JPA |
| Build Tool | Maven |
| Process Modelling | Camunda Modeler |
| Process Monitoring | Camunda Operate |

---

## Architecture

The system is built around two collaborating BPMN processes:

**MainProcess** — the primary customer-facing process covering:
- Tool selection and availability check
- Membership validation and registration
- Price calculation with loyalty discounts
- Payment collection
- Order creation and fulfilment notification

**Tool Hire Services Process** — the operational fulfilment process covering:
- Tool preparation and dispatch
- Rental agreement activation
- Return processing and condition assessment
- Maintenance authorisation and tool lifecycle management

The two processes communicate via **Camunda message events**, using `orderId` and `serialNumber` 
as correlation keys depending on the stage of the process.

---

## Database Schema

The system uses two schemas:

**`probuilds`** — core business data
- `member` — customer membership records with loyalty points
- `tool` — tool catalogue with type and pricing
- `tool_instance` — individual physical tool records with serial numbers and status
- `hire_order` — hire transactions with condition logs and return tracking
- `sale_order` — sale transactions
- `order_tool_instance` — association between orders and specific tool instances
- `credit_plan` — instalment plan records

**`fintrust`** — credit processing data
- `credit_plan` — credit applications and repayment schedules

---

## Process Workers

Each Camunda job type is handled by a dedicated Spring component following a 
**Worker → Service** pattern:

| Job Type | Description |
|---|---|
| `checkAvailability` | Fetches available tools by type from tool instances |
| `membershipValidation` | Validates membership number against DB |
| `membershipRegistration` | Registers new member and returns member ID |
| `calculatePrice` | Calculates total with hire days, delivery fee, loyalty discount |
| `processApplication` | Checks for existing active credit plan |
| `createCreditApplication` | Generates UUID application ID |
| `sendCreditApplication` | Publishes message to Fintrust process |
| `createCreditPlan` | Saves approved credit plan to DB |
| `sendApplicationDecision` | Publishes credit decision back to main process |
| `processPayment` | Validates payment details including card expiry |
| `createOrder` | Creates hire/sale order and marks tool instances |
| `orderDetails` | Publishes order details message |
| `activateRentalAgreement` | Updates hire order with issue details and sets PICKED_UP |
| `deliveryConfirmation` | Marks sale order as DELIVERED |
| `informCustomer` | Marks order as PICKED_UP and notifies customer |
| `transferMaintenanceTools` | Sets tool instances to MAINTENANCE |
| `sendAuthorisationRequest` | Publishes maintenance authorisation request |
| `sendRepairDecision` | Publishes repair decision correlated by serial number |
| `ToolsReturned` | Publishes tools returned message |
| `markToolsAvailable` | Sets instances to AVAILABLE or RETIRED post-maintenance |

---

## Getting Started

### Prerequisites
- Java 21
- Maven
- PostgreSQL
- Camunda 8 Self-Managed (c8run)

### Setup

**1. Start Camunda:**
```bash
cd ~/Downloads/camunda8-getting-started-bundle/c8run-8.9.0/
./c8run start
```

**2. Create the database schemas:**
```sql
CREATE SCHEMA probuilds;
CREATE SCHEMA fintrust;
```
then run db.sql 

**3. Run the SQL scripts** to create tables, enums and seed data.

**4. Configure `application.properties`:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_user
spring.datasource.password=your_password
zeebe.client.broker.grpcAddress=http://localhost:26500
zeebe.client.broker.restAddress=http://localhost:8080
```

**5. Deploy the BPMN processes** via Camunda Modeler.

**6. Run the Spring Boot application:**
```bash
mvn spring-boot:run
```

**7. Access Camunda Operate** at `http://localhost:8080` to monitor process instances.


