package example.ms_fichas_clinicas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fichas_clinicas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FichaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pacienteId;

    @Column(nullable = false)
    private Long medicoId;

    @Column(nullable = false, unique = true)
    private Long citaId;

    @Column(nullable = false, length = 500)
    private String motivoConsulta;

    @Column(nullable = false, length = 1000)
    private String diagnostico;

    @Column(nullable = false, length = 1000)
    private String tratamiento;

    @Column(length = 1000)
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;
}
