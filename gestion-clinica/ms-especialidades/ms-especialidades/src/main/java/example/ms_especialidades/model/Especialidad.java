package example.ms_especialidades.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "especialidades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la especialidad es obligatorio")
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @NotBlank(message = "La descripción de la especialidad es obligatoria")
    @Column
    private String descripcion;

    @NotNull(message = "El estado de la especialidad es obligatorio")
    @Column(nullable = false)
    private boolean activo = true;

}
