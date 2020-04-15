package com.anticoronabrigade.backend.repository;

import com.anticoronabrigade.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT user FROM User user WHERE user.email=?1")
    User findUserByEmailOrPhoneNumber(String email);
}
