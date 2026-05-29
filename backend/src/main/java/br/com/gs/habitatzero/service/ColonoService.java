package br.com.gs.habitatzero.service;

import br.com.gs.habitatzero.entity.Colono;
import br.com.gs.habitatzero.repository.ColonoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ColonoService {

    private final ColonoRepository repository;

    private final PasswordEncoder passwordEncoder;

    public ColonoService(ColonoRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Colono register(Colono colono) {
        colono.setPassword(passwordEncoder.encode(colono.getPassword()));

        return repository.save(colono);
    }

    public Colono searchByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Colono não encontrado"));
    }

}
