package ru.prusakova.mdm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.prusakova.mdm.model.MdmMessage;

import java.util.UUID;

public interface MdmMessageRepository extends JpaRepository<MdmMessage, UUID> {
}
