package de.htwg.in.schneider.glassfix.backend.model;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class Anfrage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String kategorie;

    @Column(nullable = false)
    private String kunde;

    private String experte;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnfrageStatus status;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = AnfrageStatus.ERSTELLT;
        }
    }

    @CreationTimestamp
    @Column(name= "erstellungsdatum", updatable = false, nullable = false)
    private String erstellungsdatum;

    private String beschreibung;
    
    private String fragen;
    
    private String bildUrl;
    
    private String antwort;



    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public String getKunde() {
        return kunde;
    }

    public void setKunde(String kunde) {
        this.kunde = kunde;
    }

    public String getExperte() {
        return experte;
    }

    public void setExperte(String experte) {
        this.experte = experte;
    }

    public AnfrageStatus getStatus() {
        return status;
    }

    public void setStatus(AnfrageStatus status) {
        this.status = status;
    }

    public String getErstellungsdatum() {
        return erstellungsdatum;
    }

    public void setErstellungsdatum(String erstellungsdatum) {
        this.erstellungsdatum = erstellungsdatum;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getFragen() {
        return fragen;
    }

    public void setFragen(String fragen) {
        this.fragen = fragen;
    }

    public String getBildUrl() {
        return bildUrl;
    }

    public void setBildUrl(String bildUrl) {
        this.bildUrl = bildUrl;
    }

    public String getAntwort() {
        return antwort;
    }

    public void setAntwort(String antwort) {
        this.antwort = antwort;
    }

    @Override
    public String toString() {
        return "Anfrage{" +
                "id=" + id +
                ", kategorie=" + kategorie +
                ", kunde=" + kunde +
                ", experte=" + experte +
                ", status=" + status +
                ", erstellungsdatum=" + erstellungsdatum +
                ", beschreibung=" + beschreibung +
                ", fragen=" + fragen +
                ", bildUrl=" + bildUrl +
                ", antwort=" + antwort +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Anfrage anfrage = (Anfrage) o;
        return id != null && id.equals(anfrage.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }



}