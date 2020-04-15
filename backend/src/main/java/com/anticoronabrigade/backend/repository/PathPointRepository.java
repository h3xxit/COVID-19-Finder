package com.anticoronabrigade.backend.repository;

import com.anticoronabrigade.backend.entity.Path_point;
import com.anticoronabrigade.backend.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PathPointRepository extends JpaRepository<Path_point, Long> {
    @Query("SELECT path_point.point_id FROM Path_point path_point WHERE path_point.path_id=?1")
    List<Long> findAllByPathId(Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM Path_point path_point WHERE path_point.path_id=?1")
    void deleteAllPointsPathByPathId(Long pathId);
}
