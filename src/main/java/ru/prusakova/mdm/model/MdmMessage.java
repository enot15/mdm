package ru.prusakova.mdm.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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

    @Enumerated(value = EnumType.STRING)
    private EventTypeEnum type;

    @JdbcTypeCode(SqlTypes.JSON)
    private MdmMessagePayload payload;
}
