package ru.prusakova.mdm.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.prusakova.mdm.dto.MdmMessageResponse;
import ru.prusakova.mdm.dto.ResponseData;
import ru.prusakova.mdm.dto.enums.DeliveryStatus;
import ru.prusakova.mdm.dto.enums.Target;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MdmMessageOutbox extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID mdmMessageId;

    @Enumerated(value = EnumType.STRING)
    private DeliveryStatus status;

    @Enumerated(value = EnumType.STRING)
    private Target target;

    @JdbcTypeCode(SqlTypes.JSON)
    private ResponseData<MdmMessageResponse> responseData;


}
