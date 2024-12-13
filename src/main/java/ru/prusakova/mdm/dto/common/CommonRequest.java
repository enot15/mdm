package ru.prusakova.mdm.dto.common;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.prusakova.mdm.dto.MetaRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonRequest<T> {

    private MetaRequest meta;

    @Valid
    private T body;
}