package com.cibersoft.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.cibersoft.demo.entity.Rol;
import com.cibersoft.demo.entity.Usuario;
import com.cibersoft.demo.repository.UsuarioRepository;

@Configuration
public class DataInit {

    @Bean
    CommandLineRunner initAdmin(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            boolean existeAdminReal = usuarioRepository.findAll().stream()
                .anyMatch(u -> u.getRol() == Rol.ADMIN && !u.getEmail().equals("admin@mitienda.com"));

            if (!existeAdminReal) {
                if (!usuarioRepository.findByEmail("admin@mitienda.com").isPresent()) {
                    Usuario adminPorDefecto = Usuario.builder()
                            .nombre("Administrador")
                            .email("admin@mitienda.com")
                            .password(passwordEncoder.encode("Administ"))
                            .rol(Rol.ADMIN)
                            .build();
                    usuarioRepository.save(adminPorDefecto);
                }
            } else {
                eliminarAdminPorDefecto(usuarioRepository);
            }        
        };
    }

    @Transactional
    public void eliminarAdminPorDefecto(UsuarioRepository usuarioRepository) {
        usuarioRepository.findByEmail("admin@mitienda.com")
            .ifPresent(admin -> {
                if (admin.getRol() == Rol.ADMIN) {
                        usuarioRepository.delete(admin);
                    }
                }
            );
    }

}
