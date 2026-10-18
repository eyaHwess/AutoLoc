package tn.esprit.autoloc.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Table(name = "agence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Agence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;
    private String nom;
    private String ville;
    private String adresse;
    private String telephone;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agence")
    private List<Vehicule> vehicules =  new ArrayList<>();
    @OneToMany(mappedBy = "agence", fetch = FetchType.LAZY)
    private List<Employe> employes = new ArrayList<>();

}
