package pfe.cb_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pfe.cb_management.dto.StatsPresenceEmployeeDto;
import pfe.cb_management.dto.StatsRdvEmployeeDto;
import pfe.cb_management.repository.PresenceRepository;
import pfe.cb_management.repository.RendezVousRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final RendezVousRepository rendezVousRepository;
    private final PresenceRepository   presenceRepository;

    public List<StatsRdvEmployeeDto> getRdvParEmployeeParMois(int annee) {
        return rendezVousRepository.countRdvParEmployeeParMois(annee)
                .stream()
                .map(row -> StatsRdvEmployeeDto.builder()
                        .employeeId((Long) row[0])
                        .employeeNom((String) row[1])
                        .employeePrenom((String) row[2])
                        .mois(((Number) row[3]).intValue())
                        .count(((Number) row[4]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    public List<StatsPresenceEmployeeDto> getPresenceParEmployeeParMois(int mois, int annee) {
        return presenceRepository.countPresenceParEmployeeParMois(mois, annee)
                .stream()
                .map(row -> StatsPresenceEmployeeDto.builder()
                        .employeeId((Long) row[0])
                        .employeeNom((String) row[1])
                        .employeePrenom((String) row[2])
                        .joursPresent(((Number) row[3]).longValue())
                        .build())
                .collect(Collectors.toList());
    }
}
