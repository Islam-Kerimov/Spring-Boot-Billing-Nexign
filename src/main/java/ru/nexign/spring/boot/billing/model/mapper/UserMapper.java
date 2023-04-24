package ru.nexign.spring.boot.billing.model.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.nexign.spring.boot.billing.model.dto.UserDto;
import ru.nexign.spring.boot.billing.model.entity.User;

import java.util.List;

/**
 * Маппер объекта Entity в DTO.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

	@Named("User")
	UserDto entityUserToDto(User user);

	@IterableMapping(qualifiedByName = "User")
	List<UserDto> entityUserListToDtoList(List<User> users);
}
