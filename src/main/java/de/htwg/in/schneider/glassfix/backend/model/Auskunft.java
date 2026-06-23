package de.htwg.in.schneider.glassfix.backend.model;

import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;

import de.htwg.in.schneider.glassfix.backend.model.Anfrage;
import de.htwg.in.schneider.glassfix.backend.model.AuskunftStatus;
import com.fasterxml.jackson.annotation.JsonFormat;



@Entity
public class Auskunft {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "anfrage_id", nullable = false, updatable = false, unique = true)
    private Anfrage anfrage;


    private Double preis;

    private Integer zeitEinschaetzung; // in Stunden

    private List<String> arbeitsschritte;

    @CreationTimestamp
    @Column(name= "erstellungsdatum", updatable = false, nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime erstellungsdatum;

    private boolean istFreigegeben;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuskunftStatus status;


    // Getter und Setter
    public Long getId() {
        return id;
    }

    public Anfrage getAnfrage() {
        return anfrage;
    }

    public void setAnfrage(Anfrage anfrage) {
        this.anfrage = anfrage;
    }

    public Double getPreis() {
        return preis;
    }

    public void setPreis(Double preis) {
        this.preis = preis;
    }

    public Integer getZeitEinschaetzung() {
        return zeitEinschaetzung;
    }

    public void setZeitEinschaetzung(Integer zeitEinschaetzung) {
        this.zeitEinschaetzung = zeitEinschaetzung;
    }

    public List<String> getArbeitsschritte() {
        return arbeitsschritte;
    }

    public void setArbeitsschritte(List<String> arbeitsschritte) {
        this.arbeitsschritte = arbeitsschritte;
    }

    public LocalDateTime getErstellungsdatum() {
        return erstellungsdatum;
    }

    public boolean isIstFreigegeben() {
        return istFreigegeben;
    }

    public void setIstFreigegeben(boolean istFreigegeben) {
        this.istFreigegeben = istFreigegeben;
    }

    public AuskunftStatus getStatus() {
        return status;
    }

    public void setStatus(AuskunftStatus status) {
        this.status = status;
    }



}