CREATE TABLE AdminTbl(
	AdminID VARCHAR(20) PRIMARY KEY NOT NULL,
    InternalID INT UNIQUE NOT NULL,
    Password VARCHAR(100) NOT NULL
);

SELECT * FROM AdminTbl;

CREATE TABLE DentistTbl(
	DentistID VARCHAR(20) PRIMARY KEY NOT NULL,
    InternalID INT UNIQUE NOT NULL,
    Title VARCHAR (50) NOT NULL,
    FirstName VARCHAR(100) NOT NULL,
    MiddleName VARCHAR(100),
    LastName VARCHAR(100) NOT NULL,
    Age INT(3) NOT NULL,
    Bio VARCHAR(1000) NOT NULL,
    DentistImgPath VARCHAR(500)
);

SELECT * FROM DentistTbl;

CREATE TABLE ClinicInfoTbl(
	ClinicID VARCHAR(20) PRIMARY KEY NOT NULL,
    InternalID INT UNIQUE NOT NULL,
    PhoneNumber VARCHAR(20) NOT NULL,
    Email VARCHAR(100) NOT NULL,
    Location VARCHAR(100) NOT NULL,
    FacebookLink VARCHAR(100) NOT NULL,
    InstagramLink VARCHAR(100) NOT NULL
);

SELECT * FROM ClinicInfoTbl;

CREATE TABLE ServicesTbl(
	ServiceID VARCHAR(20) PRIMARY KEY NOT NULL,
    InternalID INT UNIQUE NOT NULL,
    ServiceName VARCHAR(100) NOT NULL,
    ServicDesc VARCHAR(100) NOT NULL,
    StartingPrice DECIMAL NOT NULL,
    Status ENUM('Available', 'Unavailable') NOT NULL
);

SELECT *  FROM ServicesTbl;

CREATE TABLE PatientTbl(
	PatientID VARCHAR(20) PRIMARY KEY NOT NULL,
    InternalID INT UNIQUE NOT NULL,
    FirstName VARCHAR(100) NOT NULL,
    MiddleName VARCHAR(100) NOT NULL,
    LastName VARCHAR(100) NOT NULL,
    BirthDate DATE NOT NULL,
    Gender ENUM('Male', 'Female', 'Other') NOT NULL,
    CreatedBy VARCHAR(100) NOT NULL
);

SELECT * FROM PatientTbl;

CREATE TABLE AccountTbl(
	AccountID VARCHAR(20) PRIMARY KEY NOT NULL,
    InternalID INT UNIQUE NOT NULL,
    FirstName VARCHAR(100) NOT NULL,
    MiddleName VARCHAR(100) NOT NULL,
    LastName VARCHAR(100) NOT NULL,
    BirthDate DATE NOT NULL,
    Gender ENUM('Male', 'Female', 'Other'),
    Email VARCHAR(100) NOT NULL,
    Password VARCHAR(100) NOT NULL,
    PhoneNumber VARCHAR(20) NOT NULL
);

SELECT * FROM AccountTbl;

CREATE TABLE AppointmentTbl(
	AppointmentID VARCHAR(20) PRIMARY KEY NOT NULL,
    InternalID INT UNIQUE NOT NULL,
    PatientID VARCHAR(20) NOT NULL,
    FOREIGN KEY (PatientID) REFERENCES PatientTbl(PatientID)
    ON DELETE RESTRICT 
    ON UPDATE CASCADE,
    Scheduledby VARCHAR(20) NOT NULL,
    FOREIGN KEY (ScheduledBy) REFERENCES AccountTbl(AccountID)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
    ServiceID VARCHAR(20) NOT NULL,
    FOREIGN KEY (ServiceID) REFERENCES ServicesTbl(ServiceID)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
    AppointmentDate DATE NOT NULL,
    AppointmentTime TIME NOT NULL,
    AppointmentDateTime DATETIME NOT NULL
);

SELECT * FROM AppointmentTbl;

ALTER TABLE appointmenttbl
ADD Status ENUM('Upcomming', 'Completed', 'Cancelled');

-- Admin Table
ALTER TABLE AdminTbl
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Dentist Table
ALTER TABLE DentistTbl
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Clinic Info Table
ALTER TABLE ClinicInfoTbl
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Services Table
ALTER TABLE ServicesTbl
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Patient Table
ALTER TABLE PatientTbl
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Account Table
ALTER TABLE AccountTbl
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Appointment Table
ALTER TABLE AppointmentTbl
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Admin Table
SELECT AdminID, InternalID, Password, created_at, updated_at FROM AdminTbl;

-- Dentist Table
SELECT DentistID, InternalID, Title, FirstName, MiddleName, LastName, Age, Bio, DentistImgPath, created_at, updated_at FROM DentistTbl;

-- Clinic Info Table
SELECT ClinicID, InternalID, PhoneNumber, Email, Location, FacebookLink, InstagramLink, created_at, updated_at FROM ClinicInfoTbl;

-- Services Table
SELECT ServiceID, InternalID, ServiceName, ServicDesc, StartingPrice, Status, created_at, updated_at FROM ServicesTbl;

-- Patient Table
SELECT PatientID, InternalID, FirstName, MiddleName, LastName, BirthDate, Gender, CreatedBy, created_at, updated_at FROM PatientTbl;

-- Account Table
SELECT AccountID, InternalID, FirstName, MiddleName, LastName, BirthDate, Gender, Email, Password, PhoneNumber, created_at, updated_at FROM AccountTbl;

-- Appointment Table
SELECT AppointmentID, InternalID, PatientID, ScheduledBy, ServiceID, AppointmentDate, AppointmentTime, AppointmentDateTime, created_at, updated_at FROM AppointmentTbl;

ALTER TABLE table_name
MODIFY COLUMN MiddleName VARCHAR(100) NULL;

ALTER TABLE PatientTbl
MODIFY COLUMN Gender ENUM('Male', 'Female', 'Other') NULL;

ALTER TABLE patienttbl
ADD CONSTRAINT fk_patient_createdby
FOREIGN KEY (CreatedBy) REFERENCES AccountTbl(AccountID)
ON DELETE CASCADE  -- Optional behavior
ON UPDATE CASCADE; -- Optional behavior

UPDATE patienttbl
SET CreatedBy = 'ACC-1000001'
WHERE CreatedBy = 'user';

ALTER TABLE dentisttbl
ADD Prefix VARCHAR(50);