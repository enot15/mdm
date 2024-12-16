package ru.prusakova.mdm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MdmMessagePayload {

    private UUID id;
    private String guid;
    private String phone;
}
