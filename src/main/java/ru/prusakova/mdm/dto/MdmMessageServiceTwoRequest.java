package ru.prusakova.mdm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MdmMessageServiceTwoRequest {

    private UUID id;
    private String systemId;
    private List<EventRequest> events;
}
