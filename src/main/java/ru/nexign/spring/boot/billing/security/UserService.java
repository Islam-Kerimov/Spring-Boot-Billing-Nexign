package ru.nexign.spring.boot.billing.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.entity.User;
import ru.nexign.spring.boot.billing.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис поиска авторизованных пользователей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;

	public List<User> getAll() {
		log.info("Получение всех пользователей");
		return userRepository.findAll();
	}

	public Optional<User> findById(Integer id) {
		log.info("Получение пользователя по id {}", id);
		return userRepository.findById(id);
	}
}
