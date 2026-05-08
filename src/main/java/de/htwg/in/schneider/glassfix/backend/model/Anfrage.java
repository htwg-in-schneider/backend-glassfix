package de.htwg.in.schneider.glassfix.backend.model;

public class Anfrage {
    private int id;
    private String kategorie;
    private String kunde;
    private String experte;
    private AnfrageStatus status;
    private String erstellungsdatum;
    private String beschreibung;
    private String fragen;
    private String bildUrl;
    private String antwort;

    public Anfrage() {
        erstellungsdatum = java.time.LocalDate.now().toString();
        status = AnfrageStatus.ERSTELLT;
    }

    // Getter und Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
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



}