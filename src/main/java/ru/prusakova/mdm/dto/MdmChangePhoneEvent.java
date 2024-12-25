package ru.prusakova.mdm.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MdmChangePhoneEvent {

    @NotNull(message = "Идентификатор не может отсутствовать или быть null")
    private String id;

    @NotNull(message = "Тип изменения не может отсутствовать или быть null")
    @Pattern(regexp = "^USER_PHONE_CHANGE$", message = "Тип изменения должен быть USER_PHONE_CHANGE")
    private String type;

    @NotNull(message = "Идентификатор клиента не может отсутствовать или быть null")
    @Size(min = 32, max = 32, message = "Идентификатор клиента должен быть длинной 32 символа")
    private String guid;

    @NotNull(message = "Телефон не может отсутствовать или быть null")
    @Pattern(regexp = "^\\+7\\d{10}", message = "Телефон не соответствует паттерну +70000000000")
    private String phone;
}
