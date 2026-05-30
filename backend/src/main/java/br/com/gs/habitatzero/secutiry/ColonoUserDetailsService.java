package br.com.gs.habitatzero.secutiry;

import br.com.gs.habitatzero.entity.Colono;
import br.com.gs.habitatzero.repository.ColonoRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ColonoUserDetailsService implements UserDetailsService {

    private final ColonoRepository colonoRepository;

    public ColonoUserDetailsService(ColonoRepository colonoRepository) {
        this.colonoRepository = colonoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Colono colono = colonoRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Colono não encontrado com email: " + email));

        return User.builder()
                .username(colono.getEmail())
                .password(colono.getSenhaHash())
                .roles(colono.getCargo().name())
                .build();
    }
}
