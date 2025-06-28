-- For Account Address Table
CREATE TABLE accountaddresstbl (
  AccountAddressID varchar(20) NOT NULL,
  InternalID int NOT NULL,
  Barangay varchar(50) DEFAULT NULL,
  City varchar(50) NOT NULL,
  Province varchar(50) NOT NULL,
  AccountID varchar(20) NOT NULL, 
  PRIMARY KEY (AccountAddressID),
  UNIQUE KEY InternalID (InternalID),
  KEY AccountID (AccountID),
  CONSTRAINT accountaddresstbl_ibfk_1 FOREIGN KEY (AccountID) REFERENCES accounttbl (AccountID)
);

-- For Account Table
CREATE TABLE accounttbl (
  AccountID varchar(20) NOT NULL,
  InternalID int NOT NULL,
  FirstName varchar(100) NOT NULL,
  MiddleName varchar(100) NOT NULL,
  LastName varchar(100) NOT NULL,
  BirthDate date NOT NULL,
  Gender enum('Male','Female','Other') DEFAULT NULL,
  Email varchar(100) NOT NULL,
  Password varchar(100) NOT NULL,
  PhoneNumber varchar(20) NOT NULL,
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (AccountID),
  UNIQUE KEY InternalID (InternalID)
);

-- For Admin Table
CREATE TABLE admintbl (
  AdminID varchar(20) NOT NULL,
  InternalID int NOT NULL,
  Password varchar(100) NOT NULL,
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (AdminID),
  UNIQUE KEY InternalID (InternalID)
);

-- Table Appointment Table
CREATE TABLE appointmenttbl (
  AppointmentID varchar(20) NOT NULL,
  InternalID int NOT NULL AUTO_INCREMENT,
  PatientID varchar(20) NOT NULL,
  Scheduledby varchar(20) NOT NULL,
  ServiceID varchar(20) NOT NULL,
  AppointmentDate date NOT NULL,
  AppointmentTime time NOT NULL,
  AppointmentDateTime datetime NOT NULL,
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  Status enum('Upcoming', 'Completed', 'Canceled') DEFAULT NULL,
  PRIMARY KEY (AppointmentID),
  UNIQUE KEY InternalID (InternalID),
  KEY PatientID (PatientID),
  KEY Scheduledby (Scheduledby),
  KEY ServiceID (ServiceID),
  CONSTRAINT appointmenttbl_ibfk_1 FOREIGN KEY (PatientID) REFERENCES patienttbl (PatientID) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT appointmenttbl_ibfk_2 FOREIGN KEY (Scheduledby) REFERENCES accounttbl (AccountID) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT appointmenttbl_ibfk_3 FOREIGN KEY (ServiceID) REFERENCES servicestbl (ServiceID) ON DELETE RESTRICT ON UPDATE CASCADE
) 

-- For Clinic Info
CREATE TABLE clinicinfotbl (
  ClinicID varchar(20) NOT NULL,
  InternalID int NOT NULL,
  PhoneNumber varchar(20) NOT NULL,
  Email varchar(100) NOT NULL,
  Location varchar(100) NOT NULL,
  FacebookLink varchar(100) NOT NULL,
  InstagramLink varchar(100) NOT NULL,
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (ClinicID),
  UNIQUE KEY InternalID (InternalID)
);

-- For Dentist Table
CREATE TABLE dentisttbl (
  DentistID varchar(20) NOT NULL,
  InternalID int NOT NULL,
  Title varchar(50) NOT NULL,
  FirstName varchar(100) NOT NULL,
  MiddleName varchar(100) DEFAULT NULL,
  LastName varchar(100) NOT NULL,
  Age int NOT NULL,
  Bio varchar(1000) NOT NULL,
  DentistImgPath varchar(500) DEFAULT NULL,
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  Prefix varchar(50) DEFAULT NULL,
  PRIMARY KEY (DentistID),
  UNIQUE KEY InternalID (InternalID)
);

-- For Patient Address
CREATE TABLE patientaddresstbl (
  PatientAddressID varchar(20) NOT NULL,
  InternalID int NOT NULL AUTO_INCREMENT,
  Barangay varchar(50) DEFAULT NULL,
  City varchar(50) NOT NULL,
  Province varchar(50) NOT NULL,
  PatientID varchar(20) NOT NULL,
  PRIMARY KEY (PatientAddressID),
  UNIQUE KEY InternalID (InternalID),
  KEY PatientID (PatientID),
  CONSTRAINT patientaddresstbl_ibfk_1 FOREIGN KEY (PatientID) REFERENCES patienttbl (PatientID)
);

-- For Patient Table
CREATE TABLE patienttbl (
  PatientID varchar(20) NOT NULL,
  InternalID int NOT NULL AUTO_INCREMENT,
  FirstName varchar(100) NOT NULL,
  MiddleName varchar(100) DEFAULT NULL,
  LastName varchar(100) NOT NULL,
  BirthDate date NOT NULL,
  Gender enum('Male', 'Female', 'Other') DEFAULT NULL,
  CreatedBy varchar(100) NOT NULL,
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PhoneNumber varchar(20) NOT NULL,
  PRIMARY KEY (PatientID),
  UNIQUE KEY InternalID (InternalID),
  KEY fk_patient_createdby (CreatedBy),
  CONSTRAINT fk_patient_createdby FOREIGN KEY (CreatedBy) REFERENCES accounttbl (AccountID) ON DELETE CASCADE ON UPDATE CASCADE
);

-- For Services Table
CREATE TABLE servicestbl (
  ServiceID varchar(20) NOT NULL,
  InternalID int NOT NULL,
  ServiceName varchar(100) NOT NULL,
  ServiceDesc varchar(100) NOT NULL,
  StartingPrice decimal(10,0) NOT NULL,
  Status enum('Available', 'Unavailable') NOT NULL,
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (ServiceID),
  UNIQUE KEY InternalID (InternalID)
);

