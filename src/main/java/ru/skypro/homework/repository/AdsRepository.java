package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.UserEntity;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface AdsRepository extends JpaRepository<AdEntity, Integer> {

    ArrayList<AdEntity> findAdEntitiesByAuthor(Integer author);

    Optional<AdEntity> findByAuthor(UserEntity user);

    ArrayList<AdEntity> findAll();

}
