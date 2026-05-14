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

    @Column(nullable = false)
    private Long pacienteId;

    @Column(nullable = false)
    private Long medicoId;

    @Column(nullable = false)
    private Long horarioId;

    @Column(nullable = false, length = 200)
    private String motivo;

    @Builder.Default
    @Column(nullable = false, length = 30)
    private String estado = "AGENDADA";

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
}