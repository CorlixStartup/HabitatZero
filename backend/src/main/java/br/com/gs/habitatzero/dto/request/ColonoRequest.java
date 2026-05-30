package br.com.gs.habitatzero.dto.request;

import br.com.gs.habitatzero.entity.Colono;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ColonoRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Pattern(regexp = "^[^<>\"';&]*$", message = "Nome contém caracteres inválidos")
    private String nome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$!%*?&])[A-Za-z\\d@#$!%*?&]{8,}$",
        message = "Senha deve conter letras maiúsculas, minúsculas, número e caractere especial"
    )
    private String senha;

    @NotNull(message = "Cargo é obrigatório")
    private Colono.CargoColono cargo;

    private Long estufaId;
}
