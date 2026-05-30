package br.com.gs.habitatzero.repository;

import br.com.gs.habitatzero.entity.Colono;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColonoRepository extends JpaRepository<Colono, Long> {

    Optional<Colono> findByEmail(String email);
    boolean existsByEmail(String email);

}
