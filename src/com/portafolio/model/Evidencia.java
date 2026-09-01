package com.portafolio.model;

public class Evidencia {
    private String id;
    private String semana;
    private String descripcion;
    private String pdfUrl;

    public Evidencia(String id, String semana, String descripcion, String pdfUrl) {
        this.id = id;
        this.semana = semana;
        this.descripcion = descripcion;
        this.pdfUrl = pdfUrl;
    }

    public String getId() { return id; }
    public String getSemana() { return semana; }
    public String getDescripcion() { return descripcion; }
    public String getPdfUrl() { return pdfUrl; }
}