package ru.pobopo.services.user.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pobopo.services.user.service.entity.UserTypeEntity;

@Repository
public interface UserTypeRepository extends JpaRepository<UserTypeEntity, String> {
    UserTypeEntity findByName(String name);
}
