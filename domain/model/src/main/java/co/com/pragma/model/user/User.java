package co.com.pragma.model.user;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class User {

    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String phoneNumber;
    private Long idNumber;
    private Integer idRole;
    private BigDecimal baseSalary;
    private LocalDate birthdate;
    private String address;
}
