package co.com.pragma.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaveUserDTO(String name, String lastname, String email, Long idNumber, Integer idRole,
                          BigDecimal baseSalary, String phoneNumber, LocalDate birthdate, String address) {
}
