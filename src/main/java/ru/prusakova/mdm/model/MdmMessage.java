package ru.prusakova.mdm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import ru.prusakova.mdm.dto.MdmMessagePayload;
import ru.prusakova.mdm.dto.enums.EventTypeEnum;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MdmMessage extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID externalId;
    private String guid;
    private EventTypeEnum type;
    private MdmMessagePayload payload;


}
