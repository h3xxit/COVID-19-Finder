package com.anticoronabrigade.backend.repository;

import com.anticoronabrigade.backend.entity.Path;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PathManagementRepository extends JpaRepository<Path, Long> {

    @Query("SELECT path FROM Path path WHERE " +
            "path.startLongitude BETWEEN ?1 AND ?2 " +
            "AND path.startLatitude BETWEEN ?3 AND ?4")
    List<Path> findAllPathsThatStartsInRange(double longitudeMin, double longitudeMax, double latitudeMin, double latitudeMax);

    @Query("SELECT path FROM Path path WHERE " +
            "path.startLongitude BETWEEN ?1 AND ?2 " +
            "AND path.startLatitude BETWEEN ?3 AND ?4 AND path.date > ?5")
    List<Path> getAllPathsThatStartsInRangeFromLast5Days(double longitudeMin, double longitudeMax, double latitudeMin, double latitudeMax, Long currentDateInMillis);

    @Query("SELECT path FROM Path path WHERE path.date < ?1")
    List<Path> findAllPathsThatAreOlderThan10Days(Long currentDateInMillis);

    @Transactional
    @Modifying
    @Query("DELETE FROM Path path WHERE path.date < ?1")
    void deleteAllPathsThatAreOlderThan10Days(Long currentDateInMillis);

}
