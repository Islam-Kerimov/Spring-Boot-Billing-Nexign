package ru.nexign.spring.boot.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nexign.spring.boot.billing.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByUsername(String username);

}
