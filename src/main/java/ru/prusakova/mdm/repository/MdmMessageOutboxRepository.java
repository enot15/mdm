package ru.prusakova.mdm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.prusakova.mdm.model.MdmMessageOutbox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MdmMessageOutboxRepository extends JpaRepository<MdmMessageOutbox, Long> {

    @Query(value = """
        SELECT m FROM MdmMessageOutbox m
        WHERE m.lastUpdateTime > :ago15Minutes
        AND m.lastUpdateTime < :ago24Hours
        AND m.status IN ('NEW', 'ERROR') ORDER BY m.lastUpdateTime
        """, nativeQuery = true)
    List<MdmMessageOutbox> findByUpdateTimeAndStatus(LocalDateTime ago15Minutes, LocalDateTime ago24Hours, Pageable pageable);
}
