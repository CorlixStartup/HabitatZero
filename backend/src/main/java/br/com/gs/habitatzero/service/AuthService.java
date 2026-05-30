package br.com.gs.habitatzero.service;

import br.com.gs.habitatzero.dto.request.LoginRequest;
import br.com.gs.habitatzero.dto.response.TokenResponse;
import br.com.gs.habitatzero.entity.Colono;
import br.com.gs.habitatzero.exception.ResourceNotFoundException;
import br.com.gs.habitatzero.repository.ColonoRepository;
import br.com.gs.habitatzero.secutiry.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ColonoRepository colonoRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       ColonoRepository colonoRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.colonoRepository = colonoRepository;
    }

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        Colono colono = colonoRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Colono não encontrado"));

        String token = jwtUtil.generateToken(request.getEmail());

        return TokenResponse.builder()
                .token(token)
                .tipo("Bearer")
                .expiracaoMs(jwtUtil.getExpirationMs())
                .email(colono.getEmail())
                .nome(colono.getNome())
                .build();
    }

}
