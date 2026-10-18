
package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IVehiculeRepository;

@Service
@RequiredArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;

    @Override
    public Vehicule create(Vehicule vehicule) {
        if (vehicule.getIdVehicule() != null) {
            throw new IllegalArgumentException(
                    "Un nouveau véhicule ne doit pas avoir d'identifiant");
        }

        validateVehicule(vehicule);

        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule findById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vehicule", id));
    }

    @Override
    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    @Override
    public Vehicule update(Long id, Vehicule vehicule) {
        Vehicule existant = findById(id);

        validateVehicule(vehicule);

        existant.setImmatriculation(vehicule.getImmatriculation());
        existant.setMarque(vehicule.getMarque());
        existant.setModele(vehicule.getModele());
        existant.setCategorie(vehicule.getCategorie());
        existant.setTarifJournalier(vehicule.getTarifJournalier());
        existant.setStatut(vehicule.getStatut());

        return vehiculeRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicule", id);
        }

        vehiculeRepository.deleteById(id);
    }

    private void validateVehicule(Vehicule vehicule) {
        if (vehicule.getTarifJournalier() == null
                || vehicule.getTarifJournalier().signum() < 0) {
            throw new IllegalArgumentException(
                    "Le tarif journalier est obligatoire et ne peut pas être négatif");
        }

        if (vehicule.getImmatriculation() == null
                || vehicule.getImmatriculation().isBlank()) {
            throw new IllegalArgumentException(
                    "L'immatriculation est obligatoire");
        }

        if (vehicule.getMarque() == null
                || vehicule.getMarque().isBlank()) {
            throw new IllegalArgumentException(
                    "La marque du véhicule est obligatoire");
        }

        if (vehicule.getModele() == null
                || vehicule.getModele().isBlank()) {
            throw new IllegalArgumentException(
                    "Le modèle du véhicule est obligatoire");
        }

        if (vehicule.getCategorie() == null) {
            throw new IllegalArgumentException(
                    "La catégorie du véhicule est obligatoire");
        }

        if (vehicule.getStatut() == null) {
            throw new IllegalArgumentException(
                    "Le statut du véhicule est obligatoire");
        }
    }
}
