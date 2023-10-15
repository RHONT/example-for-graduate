package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.AdEntity;

@Repository
public interface AdsRepository extends JpaRepository<AdEntity,Long> {
}
