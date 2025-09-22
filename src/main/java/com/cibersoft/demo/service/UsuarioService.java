package com.cibersoft.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cibersoft.demo.entity.Rol;
import com.cibersoft.demo.entity.Usuario;
import com.cibersoft.demo.repository.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String registrar(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return "EMAIL ya está registrado.";
        }

        if (usuario.getRol() == null ) {
            usuario.setRol(Rol.USER);
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
        return "Usuario registrado correctamente.";
    }

    // -------------------------------
    // Listar todos los usuarios
    // -------------------------------
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // -------------------------------
    // Grabar usuario
    // -------------------------------
    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        } else {
            Usuario existente = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                usuario.setPassword(existente.getPassword());
            } else {
                if (!usuario.getPassword().startsWith("$2a$")) {
                    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
                }
            }
        }

        return usuarioRepository.save(usuario);
        
    }

    // -------------------------------
    // Borrar usuario
    // -------------------------------
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    // -------------------------------
    // Buscar usuario por ID
    // -------------------------------
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no Encontrado."));
        
        return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getPassword())
            .roles(usuario.getRol().name())
            .build();        
    }

    
}
