package ru.prusakova.mdm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import ru.prusakova.mdm.dto.MdmMessageResponse;
import ru.prusakova.mdm.dto.enums.DeliveryStatusEnum;
import ru.prusakova.mdm.dto.enums.TargetEnum;

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
    private DeliveryStatusEnum status;
    private TargetEnum target;
    private MdmMessageResponse responseData;


}
