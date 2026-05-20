package de.htwg.in.schneider.glassfix.backend.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;


import de.htwg.in.schneider.glassfix.backend.model.AnfrageStatus;

@Entity
public class Anfrage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String kategorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kunde_id", nullable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Benutzer kunde;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experte_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Benutzer experte;

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
    private LocalDateTime erstellungsdatum;

    private String beschreibung;
    
    private String fragen;
    
    private String bildUrl;
    
    private String antwort;

    @OneToOne(mappedBy = "anfrage", cascade = CascadeType.ALL, optional = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonIgnore
    private Auskunft auskunft;



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

    public Benutzer getKunde() {
        return kunde;
    }

    public void setKunde(Benutzer kunde) {
        this.kunde = kunde;
    }

    public Benutzer getExperte() {
        return experte;
    }

    public void setExperte(Benutzer experte) {
        if (this.experte != null){
            throw new IllegalStateException();
        }
        this.experte = experte;
    }

    public AnfrageStatus getStatus() {
        return status;
    }

    public void setStatus(AnfrageStatus status) {
        this.status = status;
    }

    public String getErstellungsdatum() {
        return erstellungsdatum.toString();
    }

    public void setErstellungsdatum(LocalDateTime erstellungsdatum) {
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

    public Auskunft getAuskunft(){
        return this.auskunft;
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