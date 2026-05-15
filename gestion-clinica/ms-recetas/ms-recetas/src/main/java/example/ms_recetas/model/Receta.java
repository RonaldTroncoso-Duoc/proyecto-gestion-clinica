package example.ms_recetas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recetas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long fichaClinicaId;

    @Column(nullable = false)
    private Long pacienteId;

    @Column(nullable = false)
    private Long medicoId;

    @Column(nullable = false, length = 150)
    private String medicamento;

    @Column(nullable = false, length = 150)
    private String dosis;

    @Column(nullable = false, length = 300)
    private String indicaciones;

    @Column(nullable = false)
    private Integer duracionDias;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activa = true;
}
