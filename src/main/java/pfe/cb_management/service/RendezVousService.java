package pfe.cb_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.cb_management.dto.*;
import pfe.cb_management.entity.RendezVous;
import pfe.cb_management.entity.ServiceRendezVous;
import pfe.cb_management.entity.User;
import pfe.cb_management.enums.Role;
import pfe.cb_management.enums.Specialite;
import pfe.cb_management.enums.StatutRendezVous;
import pfe.cb_management.enums.StatutService;
import pfe.cb_management.enums.TypeClient;
import pfe.cb_management.enums.TypeService;
import pfe.cb_management.repository.RendezVousRepository;
import pfe.cb_management.repository.ServiceRendezVousRepository;
import pfe.cb_management.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final ServiceRendezVousRepository serviceRendezVousRepository;
    private final UserRepository userRepository;

    // ── Créer un rendez-vous ──────────────────────────────────
    @Transactional
    public RendezVousResponse creer(RendezVousRequest request, String emailReceptionniste) {
        User receptionniste = userRepository.findByEmail(emailReceptionniste)
                .orElseThrow(() -> new RuntimeException("Réceptionniste introuvable."));

        LocalDateTime dateDebut = request.getDateDebut();
        LocalDateTime dateFin = dateDebut.plusMinutes(request.getDureeMinutes());

        RendezVous rdv = RendezVous.builder()
                .nomClient(request.getNomClient())
                .prenomClient(request.getPrenomClient())
                .telephoneClient(request.getTelephoneClient())
                .typeClient(request.getTypeClient())
                .statutMariee(request.getStatutMariee())
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .dureeMinutes(request.getDureeMinutes())
                .statut(StatutRendezVous.EN_ATTENTE)
                .createdBy(receptionniste)
                .build();

        validateEmployeeAvailability(request, null);

        List<ServiceRendezVous> services = buildServices(request.getServices(), rdv, request.getTypeClient());
        rdv.setServices(services);

        return toResponse(rendezVousRepository.save(rdv));
    }

    private void validateEmployeeAvailability(RendezVousRequest request, Long excludeRdvId) {
        List<ServiceRendezVousRequest> services = request.getServices();

        // ── 1. Conflits intra-requête (deux services du même RDV pour le même employé) ──
        for (int i = 0; i < services.size(); i++) {
            for (int j = i + 1; j < services.size(); j++) {
                var a = services.get(i);
                var b = services.get(j);
                if (a.getEmployeeId() == null || !a.getEmployeeId().equals(b.getEmployeeId())) continue;

                LocalDateTime aStart = a.getDatePrevue() != null ? a.getDatePrevue() : request.getDateDebut();
                int aDuree = a.getDureeService() != null ? a.getDureeService() : request.getDureeMinutes();
                LocalDateTime aEnd = aStart.plusMinutes(aDuree);

                LocalDateTime bStart = b.getDatePrevue() != null ? b.getDatePrevue() : request.getDateDebut();
                int bDuree = b.getDureeService() != null ? b.getDureeService() : request.getDureeMinutes();
                LocalDateTime bEnd = bStart.plusMinutes(bDuree);

                if (aStart.isBefore(bEnd) && bStart.isBefore(aEnd)) {
                    User employee = userRepository.findById(a.getEmployeeId())
                            .orElseThrow(() -> new RuntimeException("Employé introuvable."));
                    throw new RuntimeException("Conflit : l'employé " + employee.getNom() + " " + employee.getPrenom() +
                            " est assigné à deux services qui se chevauchent dans ce rendez-vous (" +
                            aStart + " - " + aEnd + " et " + bStart + " - " + bEnd + ").");
                }
            }
        }

        // ── 2. Conflits avec les services existants en base ──
        var existingServicesCache = new java.util.HashMap<Long, List<ServiceRendezVous>>();

        for (var srv : services) {
            if (srv.getEmployeeId() == null) continue;

            User employee = userRepository.findById(srv.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employé introuvable avec l'id : " + srv.getEmployeeId()));

            LocalDateTime srvStart = srv.getDatePrevue() != null ? srv.getDatePrevue() : request.getDateDebut();
            int duree = srv.getDureeService() != null ? srv.getDureeService() : request.getDureeMinutes();
            LocalDateTime srvEnd = srvStart.plusMinutes(duree);

            List<ServiceRendezVous> existingServices = existingServicesCache.computeIfAbsent(employee.getId(), id ->
                    rendezVousRepository.findServicesForEmployee(id, excludeRdvId));

            for (ServiceRendezVous existing : existingServices) {
                LocalDateTime existingStart = existing.getDatePrevue() != null
                        ? existing.getDatePrevue()
                        : existing.getRendezVous().getDateDebut();

                int existingDuration = existing.getDureeService() != null
                        ? existing.getDureeService()
                        : existing.getRendezVous().getDureeMinutes();
                LocalDateTime existingEnd = existingStart.plusMinutes(existingDuration);

                if (srvStart.isBefore(existingEnd) && existingStart.isBefore(srvEnd)) {
                    throw new RuntimeException("L'employé " + employee.getNom() + " " + employee.getPrenom() +
                            " est déjà occupé du " + existingStart + " au " + existingEnd + ".");
                }
            }
        }
    }

    // ── Lister tous les rendez-vous ───────────────────────────
    public List<RendezVousResponse> listerTous() {
        return rendezVousRepository.findAllByOrderByDateDebutDesc()
                .stream().map(this::toResponse).toList();
    }

    // ── Obtenir un rendez-vous par ID ─────────────────────────
    public RendezVousResponse getById(Long id) {
        return toResponse(findRdv(id));
    }

    // ── Mettre à jour un rendez-vous ──────────────────────────
    @Transactional
    public RendezVousResponse modifier(Long id, RendezVousRequest request) {
        RendezVous rdv = findRdv(id);

        if (rdv.getStatut() == StatutRendezVous.TERMINE || rdv.getStatut() == StatutRendezVous.ANNULE) {
            throw new RuntimeException("Impossible de modifier un rendez-vous " + rdv.getStatut().name().toLowerCase() + ".");
        }

        LocalDateTime dateDebut = request.getDateDebut();
        LocalDateTime dateFin = dateDebut.plusMinutes(request.getDureeMinutes());

        rdv.setNomClient(request.getNomClient());
        validateEmployeeAvailability(request, id);

        rdv.setPrenomClient(request.getPrenomClient());
        rdv.setTelephoneClient(request.getTelephoneClient());
        rdv.setTypeClient(request.getTypeClient());
        rdv.setStatutMariee(request.getStatutMariee());
        rdv.setDateDebut(dateDebut);
        rdv.setDateFin(dateFin);
        rdv.setDureeMinutes(request.getDureeMinutes());

        rdv.getServices().clear();
        rdv.getServices().addAll(buildServices(request.getServices(), rdv, request.getTypeClient()));

        return toResponse(rendezVousRepository.save(rdv));
    }

    // ── Changer le statut ─────────────────────────────────────
    @Transactional
    public RendezVousResponse changerStatut(Long id, StatutRendezVous nouveauStatut) {
        RendezVous rdv = findRdv(id);
        rdv.setStatut(nouveauStatut);
        return toResponse(rendezVousRepository.save(rdv));
    }

    // ── Supprimer un rendez-vous ──────────────────────────────
    @Transactional
    public void supprimer(Long id) {
        if (!rendezVousRepository.existsById(id)) {
            throw new RuntimeException("Rendez-vous introuvable avec l'id : " + id);
        }
        rendezVousRepository.deleteById(id);
    }

    // ── Commencer un rendez-vous (action employé) ─────────────
    @Transactional
    public RendezVousResponse commencerRendezVous(String emailEmployee, Long rdvId) {
        User employee = findUserByEmail(emailEmployee);
        RendezVous rdv = findRdv(rdvId);

        boolean estConcerne = rdv.getServices().stream()
                .anyMatch(s -> s.getEmployee() != null && s.getEmployee().getId().equals(employee.getId()));
        if (!estConcerne) {
            throw new RuntimeException("Ce rendez-vous ne vous est pas assigné.");
        }
        if (rdv.getStatut() == StatutRendezVous.EN_COURS) {
            throw new RuntimeException("Ce rendez-vous est déjà en cours.");
        }
        if (rdv.getStatut() == StatutRendezVous.TERMINE) {
            throw new RuntimeException("Ce rendez-vous est déjà terminé.");
        }
        if (rdv.getStatut() == StatutRendezVous.ANNULE) {
            throw new RuntimeException("Impossible de commencer un rendez-vous annulé.");
        }
        rdv.setStatut(StatutRendezVous.EN_COURS);
        // Passer les services CONFIRME de cet employé à EN_COURS
        rdv.getServices().stream()
                .filter(s -> s.getEmployee() != null && s.getEmployee().getId().equals(employee.getId())
                        && s.getStatut() == StatutService.CONFIRME)
                .forEach(s -> s.setStatut(StatutService.EN_COURS));
        return toResponse(rendezVousRepository.save(rdv));
    }

    // ── Terminer un rendez-vous (action employé) ──────────────
    @Transactional
    public RendezVousResponse terminerRendezVous(String emailEmployee, Long rdvId) {
        User employee = findUserByEmail(emailEmployee);
        RendezVous rdv = findRdv(rdvId);

        boolean estConcerne = rdv.getServices().stream()
                .anyMatch(s -> s.getEmployee() != null && s.getEmployee().getId().equals(employee.getId()));
        if (!estConcerne) {
            throw new RuntimeException("Ce rendez-vous ne vous est pas assigné.");
        }
        if (rdv.getStatut() == StatutRendezVous.ANNULE) {
            throw new RuntimeException("Impossible de terminer un rendez-vous annulé.");
        }
        if (rdv.getStatut() == StatutRendezVous.TERMINE) {
            throw new RuntimeException("Ce rendez-vous est déjà terminé.");
        }
        rdv.setStatut(StatutRendezVous.TERMINE);
        return toResponse(rendezVousRepository.save(rdv));
    }

    // ── Changer le statut d'un service individuel ─────────────
    @Transactional
    public RendezVousResponse changerStatutService(Long serviceId, StatutService nouveauStatut) {
        ServiceRendezVous service = findService(serviceId);
        service.setStatut(nouveauStatut);

        // Mettre à jour le statut du rendez-vous en fonction des statuts des services
        updateRendezVousStatutFromServices(service.getRendezVous());

        return toResponse(rendezVousRepository.save(service.getRendezVous()));
    }

    // ── Commencer un service individuel (action employé) ──────
    @Transactional
    public RendezVousResponse commencerService(String emailEmployee, Long serviceId) {
        User employee = findUserByEmail(emailEmployee);
        ServiceRendezVous service = findService(serviceId);

        if (!service.getEmployee().getId().equals(employee.getId())) {
            throw new RuntimeException("Ce service ne vous est pas assigné.");
        }
        if (service.getStatut() != StatutService.CONFIRME) {
            throw new RuntimeException("Seul un service confirmé peut être commencé.");
        }

        service.setStatut(StatutService.EN_COURS);
        updateRendezVousStatutFromServices(service.getRendezVous());

        return toResponse(rendezVousRepository.save(service.getRendezVous()));
    }

    // ── Terminer un service individuel (action employé) ───────
    @Transactional
    public RendezVousResponse terminerService(String emailEmployee, Long serviceId) {
        User employee = findUserByEmail(emailEmployee);
        ServiceRendezVous service = findService(serviceId);

        if (!service.getEmployee().getId().equals(employee.getId())) {
            throw new RuntimeException("Ce service ne vous est pas assigné.");
        }
        if (service.getStatut() != StatutService.EN_COURS && service.getStatut() != StatutService.CONFIRME) {
            throw new RuntimeException("Seul un service confirmé ou en cours peut être terminé.");
        }

        service.setStatut(StatutService.TERMINE);
        updateRendezVousStatutFromServices(service.getRendezVous());

        return toResponse(rendezVousRepository.save(service.getRendezVous()));
    }

    // ── Méthode utilitaire pour trouver un service ────────────
    private ServiceRendezVous findService(Long serviceId) {
        return serviceRendezVousRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service introuvable avec l'id : " + serviceId));
    }

    // ── Mettre à jour le statut du RDV en fonction des services ─
    private void updateRendezVousStatutFromServices(RendezVous rdv) {
        List<StatutService> serviceStatuses = rdv.getServices().stream()
                .filter(s -> s.getStatut() != StatutService.ANNULE)
                .map(ServiceRendezVous::getStatut)
                .toList();

        // Si tous les services sont annulés
        if (serviceStatuses.isEmpty()) {
            rdv.setStatut(StatutRendezVous.ANNULE);
            return;
        }

        StatutRendezVous newRdvStatus;

        if (serviceStatuses.stream().allMatch(s -> s == StatutService.TERMINE)) {
            // Tous terminés → RDV terminé
            newRdvStatus = StatutRendezVous.TERMINE;
        } else if (serviceStatuses.stream().anyMatch(s -> s == StatutService.EN_COURS)) {
            // Au moins un en cours → RDV en cours
            newRdvStatus = StatutRendezVous.EN_COURS;
        } else if (serviceStatuses.stream().anyMatch(s -> s == StatutService.TERMINE)) {
            // Certains terminés, d'autres encore confirmés → travail commencé → en cours
            newRdvStatus = StatutRendezVous.EN_COURS;
        } else if (serviceStatuses.stream().anyMatch(s -> s == StatutService.CONFIRME)) {
            // Tous confirmés ou en attente, au moins un confirmé
            newRdvStatus = StatutRendezVous.CONFIRME;
        } else {
            // Tous en attente
            newRdvStatus = StatutRendezVous.EN_ATTENTE;
        }

        rdv.setStatut(newRdvStatus);
    }

    // ── Rendez-vous de l'employé connecté ────────────────────
    public List<RendezVousResponse> getMesRendezVous(String emailEmployee) {
        User employee = findUserByEmail(emailEmployee);
        return rendezVousRepository.findByEmployeeId(employee.getId())
                .stream().map(this::toResponse).toList();
    }

    public List<RendezVousResponse> getMesRendezVousParStatut(String emailEmployee, StatutRendezVous statut) {
        User employee = findUserByEmail(emailEmployee);
        return rendezVousRepository.findByEmployeeIdAndStatut(employee.getId(), statut)
                .stream().map(this::toResponse).toList();
    }

    public RendezVousResponse getMesRendezVousById(String emailEmployee, Long rdvId) {
        User employee = findUserByEmail(emailEmployee);
        RendezVous rdv = findRdv(rdvId);
        boolean estConcerne = rdv.getServices().stream()
                .anyMatch(s -> s.getEmployee() != null && s.getEmployee().getId().equals(employee.getId()));
        if (!estConcerne) {
            throw new RuntimeException("Ce rendez-vous ne vous est pas assigné.");
        }
        return toResponse(rdv);
    }

    // ── Employés disponibles ──────────────────────────────────
    public List<UserDto> getEmployesDisponibles(Specialite specialite,
                                                LocalDateTime dateDebut,
                                                Integer dureeMinutes) {
        LocalDateTime dateFin = dateDebut.plusMinutes(dureeMinutes);
        return userRepository.findAvailableEmployees(specialite, dateDebut, dateFin)
                .stream()
                .map(this::toUserDto)
                .toList();
    }

    // ── Employés disponibles par TypeService (spécialité dérivée automatiquement) ──
    public List<UserDto> getEmployesDisponiblesParTypeService(TypeService typeService,
                                                              LocalDateTime dateDebut,
                                                              Integer dureeMinutes) {
        Specialite specialite = typeService.getSpecialite();
        LocalDateTime dateFin = dateDebut.plusMinutes(dureeMinutes);
        return userRepository.findAvailableEmployees(specialite, dateDebut, dateFin)
                .stream()
                .map(this::toUserDto)
                .toList();
    }

    // ── Services groupés par spécialité (pour les dropdowns frontend) ──────────
    public List<TypeServiceGroupeDto> getServicesGroupesParSpecialite() {
        return Arrays.stream(Specialite.values())
                .map(specialite -> TypeServiceGroupeDto.builder()
                        .specialite(specialite)
                        .services(
                            Arrays.stream(TypeService.values())
                                  .filter(ts -> ts.getSpecialite() == specialite)
                                  .collect(Collectors.toList())
                        )
                        .build())
                .toList();
    }

    // ── Lister les employés par spécialité ────────────────────
    public List<UserDto> getEmployesParSpecialite(Specialite specialite) {
        return userRepository.findByRole(Role.EMPLOYEE)
                .stream()
                .filter(u -> u.getSpecialites().contains(specialite) && u.isActivated())
                .map(this::toUserDto)
                .toList();
    }

    // ── Helpers privés ────────────────────────────────────────
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + email));
    }

    private RendezVous findRdv(Long id) {
        return rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable avec l'id : " + id));
    }

    private List<ServiceRendezVous> buildServices(List<ServiceRendezVousRequest> requests,
                                                   RendezVous rdv,
                                                   TypeClient typeClient) {
        return requests.stream().map(req -> {
            User employee = null;

            if (req.getEmployeeId() != null) {
                // Employé fourni : validation complète
                employee = userRepository.findById(req.getEmployeeId())
                        .orElseThrow(() -> new RuntimeException("Employé introuvable avec l'id : " + req.getEmployeeId()));

                if (employee.getRole() != Role.EMPLOYEE) {
                    throw new RuntimeException("L'utilisateur " + req.getEmployeeId() + " n'est pas un employé.");
                }

                Specialite specialiteRequise = req.getTypeService().getSpecialite();
                if (!employee.getSpecialites().contains(specialiteRequise)) {
                    throw new RuntimeException(
                            "L'employé " + employee.getNom() + " " + employee.getPrenom()
                            + " n'a pas la spécialité requise pour le service " + req.getTypeService().name()
                            + " (requis : " + specialiteRequise + ", spécialités employé : " + employee.getSpecialites() + ")."
                    );
                }
            } else if (typeClient != TypeClient.MARIAGE) {
                // Employé absent uniquement autorisé pour les clients MARIAGE
                throw new RuntimeException(
                        "L'employé est obligatoire pour le service " + req.getTypeService().name()
                        + " (client de type " + typeClient + ")."
                );
            }

            return ServiceRendezVous.builder()
                    .rendezVous(rdv)
                    .employee(employee)  // null accepté pour MARIAGE
                    .typeService(req.getTypeService())
                    .statut(StatutService.EN_ATTENTE)
                    .datePrevue(req.getDatePrevue())
                    .dureeService(req.getDureeService())
                    .codeRobe(req.getCodeRobe())
                    .build();
        }).toList();
    }

    public RendezVousResponse toResponse(RendezVous rdv) {
        List<ServiceRendezVousDto> servicesDto = rdv.getServices().stream()
                .map(this::toServiceDto)
                .toList();

        return RendezVousResponse.builder()
                .id(rdv.getId())
                .nomClient(rdv.getNomClient())
                .prenomClient(rdv.getPrenomClient())
                .telephoneClient(rdv.getTelephoneClient())
                .typeClient(rdv.getTypeClient())
                .statutMariee(rdv.getStatutMariee())
                .dateDebut(rdv.getDateDebut())
                .dateFin(rdv.getDateFin())
                .dureeMinutes(rdv.getDureeMinutes())
                .statut(rdv.getStatut())
                .createdById(rdv.getCreatedBy().getId())
                .createdByNom(rdv.getCreatedBy().getNom())
                .createdByPrenom(rdv.getCreatedBy().getPrenom())
                .services(servicesDto)
                .createdAt(rdv.getCreatedAt())
                .updatedAt(rdv.getUpdatedAt())
                .build();
    }

    private ServiceRendezVousDto toServiceDto(ServiceRendezVous srv) {
        ServiceRendezVousDto dto = new ServiceRendezVousDto();
        dto.setId(srv.getId());
        dto.setTypeService(srv.getTypeService());
        dto.setStatut(srv.getStatut());
        dto.setDatePrevue(srv.getDatePrevue());
        dto.setDureeService(srv.getDureeService());
        dto.setCodeRobe(srv.getCodeRobe());
        if (srv.getEmployee() != null) {
            dto.setEmployeeId(srv.getEmployee().getId());
            dto.setEmployeeNom(srv.getEmployee().getNom());
            dto.setEmployeePrenom(srv.getEmployee().getPrenom());
            dto.setEmployeeSpecialite(srv.getTypeService().getSpecialite());
        }
        return dto;
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .role(user.getRole())
                .activated(user.isActivated())
                .specialites(user.getSpecialites())
                .nombresExperiences(user.getNombresExperiences())
                .build();
    }
}
