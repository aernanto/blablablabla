package apap.ti._5.tour_package_2306165963_be.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String id;
    private String username;
    private String email;
    private String name;
    private String role;
}