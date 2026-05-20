package de.htwg.in.schneider.glassfix.backend.model;

public enum AnfrageStatus {
    ERSTELLT(1),
    IN_PRUEFUNG(2),
    PRUEFUNG_ABGESCHLOSSEN(3),
    AUSKUNFT_VORHANDEN(4);

    private final int order;

    AnfrageStatus(int order){
        this.order = order;
    }

    public int getOrder(){
        return order;
    }

}