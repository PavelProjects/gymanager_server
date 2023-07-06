package ru.pobopo.gymanager.services.user.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pobopo.gymanager.services.user.service.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findByLogin(String login);
    UserEntity findByLoginAndId(String login, String id);
}
