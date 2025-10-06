package com.cibersoft.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cibersoft.demo.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByDestacado(boolean destacado);

    List<Producto> findByNombreContainingAllIgnoreCase(String nombre);

    List<Producto> findByCategoria_Id(Long categoriaId);

    boolean existsByCategoria_Id(Long categoriaId);
    
    boolean existsByNombre(String nombre);

    @Query("""
        SELECT p FROM Producto p
        WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', CAST(:nombre AS string), '%')))
        AND (:oferta IS NULL OR p.oferta = :oferta)
        AND (:nuevo IS NULL OR p.nuevo = :nuevo)
        AND (:destacado IS NULL OR p.destacado = :destacado)
    """)
    List<Producto> buscarPorFiltros(
        @Param("nombre") String nombre,
        @Param("oferta") Boolean oferta,
        @Param("nuevo") Boolean nuevo,
        @Param("destacado") Boolean destacado);

}
