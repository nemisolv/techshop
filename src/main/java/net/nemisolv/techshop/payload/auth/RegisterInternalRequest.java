package net.nemisolv.techshop.payload.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.nemisolv.techshop.core._enum.RoleName;
import net.nemisolv.techshop.core.validation.EnumValue;

@Getter
@Setter
@SuperBuilder
public class RegisterInternalRequest extends RegisterRequest {
    @NotEmpty(message = "Role can not be empty")
    @NotNull(message = "Role can not be null")
    @EnumValue(enumClass = RoleName.class, message = "Role is not valid")
    private RoleName role;
}