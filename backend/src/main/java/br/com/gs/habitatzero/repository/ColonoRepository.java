package br.com.gs.habitatzero.repository;

import br.com.gs.habitatzero.entity.Colono;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColonoRepository extends JpaRepository<Colono, Long> {

    Optional<Colono> findByEmail(String email);

}
