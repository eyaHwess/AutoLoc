package tn.esprit.autoloc.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "paiement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaiement;
    private BigDecimal montant;
    private LocalDate datePaiement;
    private ModePaiement modePaiement;
    @ManyToOne(fetch = FetchType.LAZY)
    private Contrat contrat;
}
