package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.Pagination;
import ru.practicum.ewm.exception.exceptions.ObjectNotFoundException;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable page = Pagination.createPageTemplate(from, size);
        if (ids.isEmpty())
            return userRepository.findAllBy(page).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        else
            return userRepository.findAllByIdIn(ids, page).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
    }

    @Transactional
    public UserDto postUser(NewUserDto newUserDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toNewUser(newUserDto)));
    }

    @Transactional
    public void deleteUser(Long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("User with id=" + userId + " doesn't exist.");
        }
    }

}
