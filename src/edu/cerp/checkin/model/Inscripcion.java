package edu.cerp.checkin.model;

import java.time.LocalDateTime;

public class Inscripcion {
    private final String nombre;
    private final String documento;
    private final String curso; // "Prog 1" | "Prog 2" | "Base de Datos"
    private final LocalDateTime fechaHora;

    public Inscripcion(String nombre, String documento, String curso, LocalDateTime fechaHora) {
        this.nombre = nombre;
        this.documento = documento;
        this.curso = curso;
        this.fechaHora = fechaHora;
    }

    public String getNombre() { return nombre; }
    public String getDocumento() { return documento; }
    public String getCurso() { return curso; }
    public LocalDateTime getFechaHora() { return fechaHora; }
}