package ra.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import ra.validate.PasswordMatching;
import ra.validate.UniqueUsername;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@PasswordMatching
public class RegisterDTO {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 20)
    @UniqueUsername
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6)
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Email
    private String email;
}