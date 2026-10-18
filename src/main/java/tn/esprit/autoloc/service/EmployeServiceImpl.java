
package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEmployeRepository;

@Service
@RequiredArgsConstructor
public class EmployeServiceImpl implements IEmployeService {

    private final IEmployeRepository employeRepository;

    @Override
    public Employe create(Employe employe) {
        if (employe.getIdEmploye() != null) {
            throw new IllegalArgumentException(
                    "Un nouvel employé ne doit pas avoir d'identifiant");
        }

        validateEmploye(employe);

        return employeRepository.save(employe);
    }

    @Override
    public Employe findById(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employe", id));
    }

    @Override
    public List<Employe> findAll() {
        return employeRepository.findAll();
    }

    @Override
    public Employe update(Long id, Employe employe) {
        Employe existant = findById(id);

        validateEmploye(employe);

        existant.setNom(employe.getNom());
        existant.setPrenom(employe.getPrenom());
        existant.setRole(employe.getRole());

        return employeRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!employeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employe", id);
        }

        employeRepository.deleteById(id);
    }

    private void validateEmploye(Employe employe) {
        if (employe.getNom() == null || employe.getNom().isBlank()) {
            throw new IllegalArgumentException(
                    "Le nom de l'employé est obligatoire");
        }

        if (employe.getRole() == null) {
            throw new IllegalArgumentException(
                    "Le rôle de l'employé est obligatoire");
        }
    }
}
