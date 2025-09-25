package com.cibersoft.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cibersoft.demo.entity.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

    List<Categoria> findByActivo(boolean activo);
    boolean existsByNombre(String nombre);

}
