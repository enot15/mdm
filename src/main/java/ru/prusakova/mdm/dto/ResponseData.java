package ru.prusakova.mdm.dto;

import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData<T> {

    private List<String> errorMessages;
    private T response;
}
