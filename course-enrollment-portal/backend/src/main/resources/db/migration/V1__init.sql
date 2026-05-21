CREATE TABLE students (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  email VARCHAR(320) NULL,
  phone VARCHAR(32) NULL,
  pass VARCHAR(255) NULL,
  role VARCHAR(32) NOT NULL DEFAULT 'STUDENT',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_students_email UNIQUE (email),
  CONSTRAINT uq_students_phone UNIQUE (phone)
);

CREATE TABLE courses (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  fee DECIMAL(10,2) NOT NULL,
  duration VARCHAR(100) NOT NULL,
  seats INT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE enrollments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  enrolled_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status VARCHAR(32) NOT NULL,
  CONSTRAINT fk_enrollments_student FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_enrollments_course FOREIGN KEY (course_id) REFERENCES courses(id),
  INDEX idx_enrollments_student (student_id),
  INDEX idx_enrollments_course (course_id),
  INDEX idx_enrollments_status (status)
);

CREATE TABLE otp_tokens (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  identifier VARCHAR(320) NOT NULL,
  code_hash VARCHAR(255) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  used BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_otp_identifier (identifier),
  INDEX idx_otp_expires_at (expires_at)
);

INSERT INTO students (name, email, phone, role) VALUES ('Admin', 'admin@course.local', '0000000000', 'ADMIN');

INSERT INTO courses (name, fee, duration, seats) VALUES
('Java Fundamentals', 1999.00, '6 weeks', 30),
('Web Development with React', 2499.00, '8 weeks', 25),
('Data Structures & Algorithms', 2999.00, '10 weeks', 20),
('Introduction to AI', 3499.00, '8 weeks', 15);

