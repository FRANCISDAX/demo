package com.cibersoft.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cibersoft.demo.entity.Usuario;
import com.cibersoft.demo.repository.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public String registrar(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return "EMAIL ya está registrado.";
        }

        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("USER");
        }

        usuario.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
        return "Usuario registrado correctamente.";
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no Encontrado."));

        return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getPassword())
            .roles(usuario.getRol())
            .build();        
    }

}
