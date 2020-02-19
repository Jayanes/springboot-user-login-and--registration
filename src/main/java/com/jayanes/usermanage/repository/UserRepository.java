package com.jayanes.usermanage.repository;

import com.jayanes.usermanage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

//    Optional<User> findByUsername(String username);

    List<User> findByIdIn(List<Long> userIds);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM users arp where arp.username=?1  ",nativeQuery = true)
    List<User> findOneByUsername(String username);

    @Query(value = "SELECT * FROM users arp where arp.login_attempt_count=?1  ",nativeQuery = true)
    List<User> findByLoginAttempt(int loginAttemptExceed);

    @Query(value = "SELECT * FROM users arp where arp.status=?1  ",nativeQuery = true)
    List<User> findByPendingStatus(int loginPendingStatus);
}

