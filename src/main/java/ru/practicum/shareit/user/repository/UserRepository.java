package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    User create(User user);
//
//    User update(User user, long userId);
//
//    void delete(long userId);
//
//    Optional<User> getById(long userId);
//
//    boolean isEmailAvailability(String email);
//
//    Collection<User> getAll();
}
