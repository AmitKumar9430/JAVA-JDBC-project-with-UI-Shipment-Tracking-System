# JAVA-JDBC-project-with-UI-Shipment-Tracking-System
 Shipment Tracking System
Description
The Shipment Tracking System is a Java-based desktop application built with Swing for managing shipment records. It provides a user-friendly interface for administrators to perform CRUD (Create, Read, Update, Delete) operations on shipment data, store deleted records in an archive, and export data to CSV. The application connects to a MySQL database to store and retrieve shipment information, featuring a modern UI with gradient headers, styled components, and real-time search/filter capabilities.
Features

Admin Login: Secure access with predefined credentials.
Add Shipment: Create new shipment records with customer details, origin, destination, cost, delivery date, and status.
View Shipments: Display all shipments in a sortable table with export to CSV functionality.
Search/Filter: Filter shipments by ID, cost range, date range, or status with real-time updates.
Update Status: Update the status of existing shipments.
Delete Shipment: Move shipments to an archive table upon deletion.
Deletion History: View and manage archived (deleted) shipment records.
Responsive UI: Gradient headers, styled buttons, and text fields with focus effects.

Prerequisites
To run the Shipment Tracking System, ensure you have the following installed:

Java Development Kit (JDK): Version 8 or higher (JDK 17 recommended).
MySQL Server: Version 5.7 or higher.
MySQL Connector/J: JDBC driver for MySQL (included in the project or downloadable).
Integrated Development Environment (IDE): Optional, e.g., IntelliJ IDEA, Eclipse, or NetBeans for easier setup.

Setup Instructions
Follow these steps to set up and run the application:
1. Clone the Repository
Clone the project to your local machine using Git:
git clone https://github.com/your-username/shipment-tracking-system.git
cd shipment-tracking-system

2. Install MySQL Server

Download and install MySQL Server from mysql.com.
Start the MySQL server and ensure it’s running on localhost:3306 (default port).

3. Set Up the MySQL Database

Log in to MySQL using a client (e.g., MySQL Workbench or command line):mysql -u root -p


Create a database named shiptmenttrack_db:CREATE DATABASE shiptmenttrack_db;


Verify the database was created:SHOW DATABASES;



4. Configure MySQL Connector/J

Download the MySQL Connector/J JAR file from MySQL Downloads if not included in the project.
Add the JAR file to your project’s classpath:
IDE: Add the JAR to the project’s library/dependencies.
Command Line: Place the JAR in the project directory and include it when compiling/running.



5. Update Database Credentials

Open the ShipmentTrackingSystem.java file.
Update the database connection details to match your MySQL setup:private static final String URL = "jdbc:mysql://localhost:3306/shiptmenttrack_db";
private static final String USER = "root"; // Your MySQL username
private static final String PASSWORD = "1234"; // Your MySQL password


Update the admin login credentials if desired:private static final String ADMIN_USERNAME = "dd";
private static final String ADMIN_PASSWORD = "1";



6. Compile and Run the Application

Using an IDE:
Import the project into your IDE.
Ensure the MySQL Connector/J JAR is added to the project’s build path.
Run the ShipmentTrackingSystem class (contains the main method).


Using Command Line:
Compile the Java file with the MySQL Connector/J JAR:javac -cp .;mysql-connector-java-<version>.jar ShipmentTrackingSystem.java

Replace <version> with the actual version of the MySQL Connector/J JAR.
Run the application:java -cp .;mysql-connector-java-<version>.jar ShipmentTrackingSystem

On Unix-based systems, use : instead of ; in the classpath.

UI SCREENSHOTS
![Screenshot 2025-05-28 022339](https://github.com/user-attachments/assets/df20a3f4-d466-4457-b14a-ddd4343cd18a)
![Screenshot 2025-05-28 022345](https://github.com/user-attachments/assets/abe162c4-217f-48fb-bf27-d47af95d3401)
![Screenshot 2025-05-28 022353](https://github.com/user-attachments/assets/c718ebc3-083a-4924-bf11-285cc6f21326)
![Screenshot 2025-05-28 022359](https://github.com/user-attachments/assets/48779936-ccc0-487e-9a2c-5a1e8163fa9d)
![Screenshot 2025-05-28 022405](https://github.com/user-attachments/assets/3d234c5a-c4be-4746-a8c4-ab7f70111a73)
![Screenshot 2025-05-28 022410](https://github.com/user-attachments/assets/9a229496-cf88-423f-b91b-d26320db00d0)

Usage

Launch the Application:

Run the program. A login dialog will appear.
Enter the admin credentials (default: username dd, password 1).


Main Interface:

The application opens with a tabbed interface containing:
Add Shipment: Enter shipment details (customer name, origin, destination, cost, delivery date, status) and click "Add Shipment."
View Shipments: View all shipments in a table. Click "Refresh Data" to update or "Export to CSV" to save the table as a CSV file.
Search/Filter: Enter filters (Shipment ID, cost range, date range, status) to narrow down shipments. Results update in real-time.
Update Status: Enter a Shipment ID, click "Find Shipment," select a new status, and click "Update Status."
Delete: Enter a Shipment ID, click "Find Shipment," and click "Delete Shipment" to archive the record.
Deletion History: View archived (deleted) shipments, search by criteria, and permanently delete selected or all search results.




Tips:

Use the YYYY-MM-DD format for dates (e.g., 2025-12-31).
Ensure numeric fields (e.g., cost, Shipment ID) contain valid numbers.
Confirm deletions and updates via dialog prompts to prevent accidental changes.



Database Schema
The application creates two tables in the shiptmenttrack_db database:

shipments:
shipment_id (INT, Primary Key, Auto-increment)
customer_name (VARCHAR(50))
origin (VARCHAR(50))
destination (VARCHAR(50))
cost (DECIMAL(10,2))
delivery_date (DATE)
status (VARCHAR(20), Default: 'Pending')
created_at (TIMESTAMP, Default: CURRENT_TIMESTAMP)


deleted_shipments:
deletion_id (INT, Primary Key, Auto-increment)
shipment_id (INT)
customer_name (VARCHAR(50))
origin (VARCHAR(50))
destination (VARCHAR(50))
cost (DECIMAL(10,2))
delivery_date (DATE)
status (VARCHAR(20))
created_at (TIMESTAMP)
deleted_at (TIMESTAMP, Default: CURRENT_TIMESTAMP)
deleted_by (VARCHAR(50), Default: 'admin')



Troubleshooting

Database Connection Error:
Ensure MySQL Server is running and credentials in ShipmentTrackingSystem.java match your setup.
Verify the MySQL Connector/J JAR is included in the classpath.


Login Failure:
Check the ADMIN_USERNAME and ADMIN_PASSWORD constants in the code.


Date Format Issues:
Use YYYY-MM-DD format for all date inputs.


Export to CSV Fails:
Ensure you have write permissions in the directory where the CSV file is saved.



Contributing
Contributions are welcome! To contribute:
🧑‍💻 Author
Amit Kumar - www.linkedin.com/in/amit-kumar-9t5m2i3a
 

License
This project is licensed under the MIT License.
 
 
