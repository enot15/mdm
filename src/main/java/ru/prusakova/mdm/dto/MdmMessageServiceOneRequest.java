package ru.prusakova.mdm.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MdmMessageServiceOneRequest {

    private UUID id;
    private String guid;
    private String phone;
}
