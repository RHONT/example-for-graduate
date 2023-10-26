package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.skypro.homework.entities.UserEntity;

import java.util.Optional;


@Repository
public interface UsersRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUsername(String username);

    Boolean existsByUsername(String username);
    void deleteByUsername(String username);
//
//    @Transactional
//    @Modifying
//    @Query(value = "delete from user_roles where user_id=?;\n" +
//                   "delete from users where id=?",nativeQuery = true)
//    void deleteByUsername(Integer idUserRole,Integer idUser);
}
