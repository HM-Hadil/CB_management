package pfe.cb_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.cb_management.dto.ProduitStockDto;
import pfe.cb_management.dto.ProduitStockRequest;
import pfe.cb_management.dto.UtilisationProduitRequest;
import pfe.cb_management.entity.ProduitStock;
import pfe.cb_management.enums.CategorieStock;
import pfe.cb_management.enums.Specialite;
import pfe.cb_management.repository.ProduitStockRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProduitStockRepository produitStockRepository;

    public List<ProduitStockDto> getAllProduits() {
        return produitStockRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<ProduitStockDto> getProduitsEnAlerte() {
        return produitStockRepository.findProduitsEnAlerte()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ProduitStockDto creer(ProduitStockRequest request) {
        ProduitStock produit = ProduitStock.builder()
                .nom(request.getNom())
                .categorie(request.getCategorie())
                .quantite(request.getQuantite())
                .quantiteMinimum(request.getQuantiteMinimum())
                .unite(request.getUnite())
                .nomFournisseur(request.getNomFournisseur())
                .reference(request.getReference())
                .build();
        return toDto(produitStockRepository.save(produit));
    }

    @Transactional
    public ProduitStockDto modifier(Long id, ProduitStockRequest request) {
        ProduitStock produit = produitStockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + id));
        produit.setNom(request.getNom());
        produit.setCategorie(request.getCategorie());
        produit.setQuantite(request.getQuantite());
        produit.setQuantiteMinimum(request.getQuantiteMinimum());
        produit.setUnite(request.getUnite());
        produit.setNomFournisseur(request.getNomFournisseur());
        produit.setReference(request.getReference());
        return toDto(produitStockRepository.save(produit));
    }

    @Transactional
    public ProduitStockDto decrementeQuantite(Long id) {
        ProduitStock produit = produitStockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + id));
        if (produit.getQuantite() <= 0) {
            throw new RuntimeException("La quantité est déjà à 0.");
        }
        produit.setQuantite(produit.getQuantite() - 1);
        return toDto(produitStockRepository.save(produit));
    }

    public List<ProduitStockDto> getProduitsBySpecialites(Set<Specialite> specialites) {
        Set<CategorieStock> categories = specialites.stream()
                .map(s -> CategorieStock.valueOf(s.name()))
                .collect(Collectors.toSet());
        return produitStockRepository.findByCategorieIn(categories)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public List<ProduitStockDto> utiliserProduits(List<UtilisationProduitRequest> utilisations) {
        List<ProduitStockDto> results = new ArrayList<>();
        for (UtilisationProduitRequest u : utilisations) {
            ProduitStock p = produitStockRepository.findById(u.getProduitId())
                    .orElseThrow(() -> new RuntimeException("Produit introuvable : " + u.getProduitId()));
            if (p.getQuantite() < u.getQuantite()) {
                throw new RuntimeException("Stock insuffisant pour : " + p.getNom()
                        + " (disponible: " + p.getQuantite() + ")");
            }
            p.setQuantite(p.getQuantite() - u.getQuantite());
            results.add(toDto(produitStockRepository.save(p)));
        }
        return results;
    }

    @Transactional
    public void supprimer(Long id) {
        if (!produitStockRepository.existsById(id)) {
            throw new RuntimeException("Produit introuvable : " + id);
        }
        produitStockRepository.deleteById(id);
    }

    private ProduitStockDto toDto(ProduitStock p) {
        return ProduitStockDto.builder()
                .id(p.getId())
                .nom(p.getNom())
                .categorie(p.getCategorie())
                .quantite(p.getQuantite())
                .quantiteMinimum(p.getQuantiteMinimum())
                .unite(p.getUnite())
                .nomFournisseur(p.getNomFournisseur())
                .reference(p.getReference())
                .enAlerte(p.getQuantite() <= p.getQuantiteMinimum())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
