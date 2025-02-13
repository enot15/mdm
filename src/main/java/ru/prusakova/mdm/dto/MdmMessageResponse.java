package ru.prusakova.mdm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.prusakova.mdm.dto.enums.IntegrationStatus;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MdmMessageResponse {

    private UUID id;
    private IntegrationStatus status;
    private String errorMessage;
}
