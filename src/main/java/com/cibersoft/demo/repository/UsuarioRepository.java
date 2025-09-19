package com.cibersoft.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cibersoft.demo.entity.Rol;
import com.cibersoft.demo.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(Rol rol);
    boolean existsByRol(Rol rol);

}
