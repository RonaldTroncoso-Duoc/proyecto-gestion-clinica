package example.ms_citas.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pacienteId; // ID que vive en ms-pacientes
    private Long medicoId;   // ID que vive en ms-medicos
    private LocalDateTime fechaHora;
    private String motivo;
}