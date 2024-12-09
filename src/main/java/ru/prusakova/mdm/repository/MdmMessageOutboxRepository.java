package ru.prusakova.mdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.prusakova.mdm.model.MdmMessageOutbox;

import java.util.UUID;

public interface MdmMessageOutboxRepository extends JpaRepository<MdmMessageOutbox, Long> {
}
