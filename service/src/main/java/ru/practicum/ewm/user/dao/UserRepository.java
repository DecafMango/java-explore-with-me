package ru.practicum.ewm.user.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllBy(Pageable page);

    Page<User> findAllByIdIn(List<Long> ids, Pageable page);

}
