package pfe.cb_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.cb_management.dto.ProduitStockDto;
import pfe.cb_management.dto.ProduitStockRequest;
import pfe.cb_management.entity.ProduitStock;
import pfe.cb_management.repository.ProduitStockRepository;

import java.util.List;

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
                .prixUnitaire(request.getPrixUnitaire())
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
        produit.setPrixUnitaire(request.getPrixUnitaire());
        return toDto(produitStockRepository.save(produit));
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
                .prixUnitaire(p.getPrixUnitaire())
                .enAlerte(p.getQuantite() <= p.getQuantiteMinimum())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
