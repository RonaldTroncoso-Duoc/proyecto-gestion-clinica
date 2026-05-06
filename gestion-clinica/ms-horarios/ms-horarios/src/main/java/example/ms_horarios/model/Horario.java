package example.ms_horarios.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "horarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del médico es obligatorio")
    @Column(nullable = false)
    private Long medicoId;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    @FutureOrPresent(message = "La fecha debe ser desde hoy")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    @Column(nullable = false)
    private LocalTime horaInicio;

    @NotNull(message = "La hora de término es obligatoria")
    @Column(nullable = false)
    private LocalTime horaTermino;

    @Column(nullable = false)
    private boolean disponible = true;

}
