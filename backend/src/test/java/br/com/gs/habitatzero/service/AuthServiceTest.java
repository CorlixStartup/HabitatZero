package br.com.gs.habitatzero.service;

import br.com.gs.habitatzero.dto.request.LoginRequest;
import br.com.gs.habitatzero.repository.ColonoRepository;
import br.com.gs.habitatzero.secutiry.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ColonoRepository colonoRepository;

    @InjectMocks
    private AuthService authService;

    // TC-08: Login com senha incorreta deve lançar BadCredentialsException
    @Test
    void login_comSenhaInvalida_deveDispararException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("usuario@habitat.com");
        request.setSenha("senhaErrada");

        doThrow(new BadCredentialsException("Credenciais inválidas"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
        verify(colonoRepository, never()).findByEmail(any());
    }
}
