package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.skypro.homework.dto.*;
import ru.skypro.homework.entities.UserEntity;

import java.util.List;
import java.util.Optional;


@Repository
public interface UsersRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUsername(String username);

    Boolean existsByUsername(String username);
    void deleteByUsername(String username);

    @Query(value = "SELECT username, password \n" +
            "from users u\n" +
            "where username=?",nativeQuery = true)
    Optional<LoginDtoInterface> findLoginAndPasswordByUserName(String username);

    @Query(value = "SELECT r.name\n" +
            "from users as u\n" +
            "         join public.user_roles ur on u.id = ur.user_id\n" +
            "         join public.roles r on r.id = ur.role_id\n" +
            "where username=?",nativeQuery = true)
    List<RolesDtoInterface> findRolesByUserName(String username);

}
