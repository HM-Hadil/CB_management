package pfe.cb_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.cb_management.dto.PresenceResponse;
import pfe.cb_management.entity.Presence;
import pfe.cb_management.entity.User;
import pfe.cb_management.enums.Role;
import pfe.cb_management.enums.StatutPresence;
import pfe.cb_management.repository.PresenceRepository;
import pfe.cb_management.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private final PresenceRepository presenceRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // ── GET all employees presence for a date ──────────────────────────────
    public List<PresenceResponse> getPresenceForDate(LocalDate date) {
        List<User> employees = userRepository.findByRole(Role.EMPLOYEE)
                .stream().filter(User::isActivated).collect(Collectors.toList());

        List<Presence> presences = presenceRepository.findByDate(date);
        Map<Long, Presence> presenceMap = presences.stream()
                .collect(Collectors.toMap(p -> p.getEmployee().getId(), p -> p));

        return employees.stream().map(emp -> {
            Presence p = presenceMap.get(emp.getId());
            if (p != null) {
                return toResponse(p, emp);
            }
            return PresenceResponse.builder()
                    .employeeId(emp.getId())
                    .employeeNom(emp.getNom())
                    .employeePrenom(emp.getPrenom())
                    .employeeSpecialite(emp.getSpecialite() != null ? emp.getSpecialite().name() : null)
                    .date(date.toString())
                    .statut(StatutPresence.ABSENT)
                    .build();
        }).collect(Collectors.toList());
    }

    // ── MARK arrival (always today) ────────────────────────────────────────
    @Transactional
    public PresenceResponse marquerArrivee(Long employeeId) {
        LocalDate today = LocalDate.now();
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));

        Optional<Presence> existing = presenceRepository.findByEmployeeIdAndDate(employeeId, today);
        if (existing.isPresent() && existing.get().getHeureArrivee() != null) {
            throw new RuntimeException("Arrivée déjà marquée pour cet employé aujourd'hui.");
        }

        LocalTime now = LocalTime.now();
        Presence presence = existing.orElse(
                Presence.builder().employee(employee).date(today).build()
        );
        presence.setHeureArrivee(now);
        presence.setStatut(StatutPresence.PRESENT);

        return toResponse(presenceRepository.save(presence), employee);
    }

    // ── MARK departure (always today) ──────────────────────────────────────
    @Transactional
    public PresenceResponse marquerDepart(Long employeeId) {
        LocalDate today = LocalDate.now();
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));

        Presence presence = presenceRepository.findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() -> new RuntimeException("Aucune arrivée enregistrée pour aujourd'hui."));

        if (presence.getHeureArrivee() == null) {
            throw new RuntimeException("Arrivée non encore marquée pour cet employé.");
        }
        if (presence.getHeureDepart() != null) {
            throw new RuntimeException("Départ déjà marqué pour cet employé aujourd'hui.");
        }

        presence.setHeureDepart(LocalTime.now());
        // status stays PRESENT — receptionist must click "Terminer" to finalize

        return toResponse(presenceRepository.save(presence), employee);
    }

    // ── TERMINATE workday ──────────────────────────────────────────────────
    @Transactional
    public PresenceResponse terminer(Long employeeId) {
        LocalDate today = LocalDate.now();
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));

        Presence presence = presenceRepository.findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() -> new RuntimeException("Aucune présence enregistrée pour aujourd'hui."));

        if (presence.getHeureDepart() == null) {
            throw new RuntimeException("Veuillez d'abord marquer le départ avant de terminer.");
        }
        if (presence.getStatut() == StatutPresence.TERMINE) {
            throw new RuntimeException("La journée est déjà terminée pour cet employé.");
        }

        presence.setStatut(StatutPresence.TERMINE);

        return toResponse(presenceRepository.save(presence), employee);
    }

    // ── Helper ─────────────────────────────────────────────────────────────
    private PresenceResponse toResponse(Presence p, User employee) {
        Double heuresTravaillees = null;
        if (p.getHeureArrivee() != null && p.getHeureDepart() != null) {
            long minutes = Duration.between(p.getHeureArrivee(), p.getHeureDepart()).toMinutes();
            heuresTravaillees = Math.round(minutes / 60.0 * 100.0) / 100.0;
        }
        return PresenceResponse.builder()
                .id(p.getId())
                .employeeId(employee.getId())
                .employeeNom(employee.getNom())
                .employeePrenom(employee.getPrenom())
                .employeeSpecialite(employee.getSpecialite() != null ? employee.getSpecialite().name() : null)
                .date(p.getDate().toString())
                .heureArrivee(p.getHeureArrivee() != null ? p.getHeureArrivee().format(TIME_FMT) : null)
                .heureDepart(p.getHeureDepart() != null ? p.getHeureDepart().format(TIME_FMT) : null)
                .statut(p.getStatut())
                .heuresTravaillees(heuresTravaillees)
                .build();
    }
}
