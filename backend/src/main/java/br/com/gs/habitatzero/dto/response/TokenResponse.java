package br.com.gs.habitatzero.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {

    private String token;
    private String tipo;
    private Long expiracaoMs;
    private String email;
    private String nome;

}
