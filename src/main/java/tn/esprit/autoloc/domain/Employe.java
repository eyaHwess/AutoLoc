package tn.esprit.autoloc.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
@Table(name = "employe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Employe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmploye;
    private String nom;
    private String prenom;
    private RoleEmploye role;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agence")
    private Agence agence;
}
