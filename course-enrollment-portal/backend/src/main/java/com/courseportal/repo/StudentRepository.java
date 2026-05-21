package com.courseportal.repo;

import com.courseportal.model.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
  Optional<Student> findByEmailIgnoreCase(String email);

  Optional<Student> findByPhone(String phone);
}

