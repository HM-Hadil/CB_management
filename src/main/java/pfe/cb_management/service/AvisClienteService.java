package pfe.cb_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.cb_management.dto.AvisClienteDto;
import pfe.cb_management.dto.AvisClienteRequest;
import pfe.cb_management.entity.AvisCliente;
import pfe.cb_management.entity.RendezVous;
import pfe.cb_management.enums.StatutRendezVous;
import pfe.cb_management.repository.AvisClienteRepository;
import pfe.cb_management.repository.RendezVousRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvisClienteService {

    private final AvisClienteRepository avisRepository;
    private final RendezVousRepository rendezVousRepository;

    public List<AvisClienteDto> listerTous() {
        return avisRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public AvisClienteDto creer(Long rendezVousId, AvisClienteRequest request) {
        RendezVous rdv = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable."));

        if (rdv.getStatut() != StatutRendezVous.TERMINE) {
            throw new RuntimeException("L'avis ne peut être ajouté que pour un rendez-vous terminé.");
        }

        if (avisRepository.existsByRendezVousId(rendezVousId)) {
            throw new RuntimeException("Un avis existe déjà pour ce rendez-vous.");
        }

        if (request.getNote() == null || request.getNote() < 1 || request.getNote() > 5) {
            throw new RuntimeException("La note doit être comprise entre 1 et 5.");
        }

        AvisCliente avis = AvisCliente.builder()
                .rendezVous(rdv)
                .nomClient(rdv.getNomClient())
                .prenomClient(rdv.getPrenomClient())
                .telephoneClient(rdv.getTelephoneClient())
                .note(request.getNote())
                .commentaire(request.getCommentaire())
                .build();

        return toDto(avisRepository.save(avis));
    }

    @Transactional
    public void supprimer(Long id) {
        if (!avisRepository.existsById(id)) {
            throw new RuntimeException("Avis introuvable.");
        }
        avisRepository.deleteById(id);
    }

    /** Vérifie si un avis existe déjà pour ce rendez-vous */
    public boolean avisExistePour(Long rendezVousId) {
        return avisRepository.existsByRendezVousId(rendezVousId);
    }

    private AvisClienteDto toDto(AvisCliente a) {
        return AvisClienteDto.builder()
                .id(a.getId())
                .rendezVousId(a.getRendezVous().getId())
                .nomClient(a.getNomClient())
                .prenomClient(a.getPrenomClient())
                .telephoneClient(a.getTelephoneClient())
                .note(a.getNote())
                .commentaire(a.getCommentaire())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
