package pfe.cb_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pfe.cb_management.entity.ProduitStock;
import pfe.cb_management.enums.CategorieStock;

import java.util.Collection;
import java.util.List;

public interface ProduitStockRepository extends JpaRepository<ProduitStock, Long> {

    /** Produits dont la quantité est inférieure ou égale à la quantité minimum */
    @Query("SELECT p FROM ProduitStock p WHERE p.quantite <= p.quantiteMinimum")
    List<ProduitStock> findProduitsEnAlerte();

    /** Produits filtrés par catégories (correspondant aux spécialités de l'employé) */
    List<ProduitStock> findByCategorieIn(Collection<CategorieStock> categories);
}
