package com.anticoronabrigade.backend.repository;

import com.anticoronabrigade.backend.entity.RegisterCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RegisterCodeRepository extends JpaRepository<RegisterCode, Long> {

    @Query("DELETE FROM RegisterCode registerCode WHERE registerCode.date<?1")
    @Transactional
    @Modifying
    void deleteOldRegisterCodes(Long time);

    @Query("SELECT registerCode.id FROM RegisterCode registerCode WHERE registerCode.code=?1")
    List<Long> selectByCode(Integer code);
}
