package br.com.gs.habitatzero.config;

import br.com.gs.habitatzero.entity.Colono;
import br.com.gs.habitatzero.entity.Estufa;
import br.com.gs.habitatzero.entity.Planta;
import br.com.gs.habitatzero.repository.ColonoRepository;
import br.com.gs.habitatzero.repository.EstufaRepository;
import br.com.gs.habitatzero.repository.PlantaRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class DataSeeder implements ApplicationRunner {

    private final EstufaRepository estufaRepository;
    private final ColonoRepository colonoRepository;
    private final PlantaRepository plantaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(EstufaRepository estufaRepository,
                      ColonoRepository colonoRepository,
                      PlantaRepository plantaRepository,
                      PasswordEncoder passwordEncoder) {
        this.estufaRepository = estufaRepository;
        this.colonoRepository = colonoRepository;
        this.plantaRepository = plantaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (estufaRepository.count() > 0) {
            return; // already seeded
        }

        Estufa alpha = estufaRepository.save(Estufa.builder()
                .nome("Estufa Alpha")
                .localizacao("Setor A — Nível 1")
                .capacidadeM2(120.0)
                .status(Estufa.StatusEstufa.ATIVA)
                .thresholdOxigenioMin(19.5)
                .thresholdUmidadeMin(30.0)
                .thresholdRadiacaoMax(2.0)
                .thresholdTemperaturaMax(40.0)
                .build());

        Estufa beta = estufaRepository.save(Estufa.builder()
                .nome("Estufa Beta")
                .localizacao("Setor B — Nível 2")
                .capacidadeM2(80.0)
                .status(Estufa.StatusEstufa.ATIVA)
                .thresholdOxigenioMin(19.5)
                .thresholdUmidadeMin(30.0)
                .thresholdRadiacaoMax(2.0)
                .thresholdTemperaturaMax(40.0)
                .build());

        colonoRepository.save(Colono.builder()
                .nome("Comandante Silva")
                .email("silva@habitatzero.br")
                .senhaHash(passwordEncoder.encode("Senha@123"))
                .cargo(Colono.CargoColono.COMANDANTE)
                .estufa(alpha)
                .build());

        colonoRepository.save(Colono.builder()
                .nome("Engenheira Lima")
                .email("lima@habitatzero.br")
                .senhaHash(passwordEncoder.encode("Senha@123"))
                .cargo(Colono.CargoColono.ENGENHEIRO)
                .estufa(beta)
                .build());

        plantaRepository.save(Planta.builder()
                .nomeCientifico("Solanum lycopersicum")
                .nomeComum("Tomate")
                .faseCrescimento(Planta.FaseCrescimento.CRESCIMENTO)
                .dataPlantio(LocalDate.now().minusDays(30))
                .estufa(alpha)
                .build());

        plantaRepository.save(Planta.builder()
                .nomeCientifico("Lactuca sativa")
                .nomeComum("Alface")
                .faseCrescimento(Planta.FaseCrescimento.MATURACAO)
                .dataPlantio(LocalDate.now().minusDays(15))
                .estufa(alpha)
                .build());

        plantaRepository.save(Planta.builder()
                .nomeCientifico("Capsicum annuum")
                .nomeComum("Pimentão")
                .faseCrescimento(Planta.FaseCrescimento.GERMINACAO)
                .dataPlantio(LocalDate.now().minusDays(7))
                .estufa(beta)
                .build());
    }
}
