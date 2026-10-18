package tn.esprit.autoloc.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Table(name = "equipement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Equipement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipement;
    private String libelle;
    @ManyToMany(mappedBy = "equipements", fetch = FetchType.LAZY)
    private Set<Vehicule> vehicules = new HashSet<>();
}
