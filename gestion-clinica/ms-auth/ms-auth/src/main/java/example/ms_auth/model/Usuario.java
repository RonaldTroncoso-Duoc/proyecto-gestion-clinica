package example.ms_auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data 
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; //Rut

    @Column(nullable =false)
    private String password; //Usar BCrypt

    @Column(nullable =false)
    private String email;

    @Column(nullable =false)
    private String rol; //ADMIN, PACIENTE, OPERADOR
}
