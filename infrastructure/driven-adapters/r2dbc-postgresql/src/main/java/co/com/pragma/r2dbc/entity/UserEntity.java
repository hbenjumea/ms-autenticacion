package co.com.pragma.r2dbc.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column("id_usuario")
    private Long id;
    @Column("nombre")
    private String name;
    @Column("apellido")
    private String lastname;
    private String email;
    @Column("documento_identidad")
    private Long idNumber;
    @Column("telefono")
    private String phoneNumber;
    @Column("id_rol")
    private Integer idRole;
    @Column("salario_base")
    private BigDecimal baseSalary;
    @Column("fecha_nacimiento")
    private LocalDate birthdate;
    @Column("direccion")
    private String address;
}
