package edu.cerp.checkin.logic;

import edu.cerp.checkin.model.Inscripcion;
import edu.cerp.checkin.persist.InscripcionRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/** Lógica mínima con opción de persistencia CSV. */
public class SesionService {
    private final List<Inscripcion> inscripciones = new ArrayList<>();
    private final InscripcionRepository repo; // puede ser null (sin persistencia)

    /** Modo memoria (compatibilidad) */
    public SesionService() { this.repo = null; }

    /** Modo persistente con repositorio */
    public SesionService(InscripcionRepository repo) {
        this.repo = repo;
        if (repo != null) {
            try {
                inscripciones.addAll(repo.loadAll());
            } catch (IOException e) {
                System.err.println("⚠ No se pudieron cargar inscripciones desde CSV: " + e.getMessage());
            }
        }
    }

    public void registrar(String nombre, String documento, String curso) {
        if (nombre == null || nombre.isBlank()) nombre = "(sin nombre)";
        if (documento == null) documento = "";
        if (curso == null || curso.isBlank()) curso = "Prog 1";

        Inscripcion ins = new Inscripcion(nombre, documento, curso, LocalDateTime.now());
        inscripciones.add(ins);
        if (repo != null) {
            try { repo.append(ins); }
            catch (IOException e) { System.err.println("⚠ No se pudo guardar en CSV: " + e.getMessage()); }
        }
    }

    public List<Inscripcion> listar() { return List.copyOf(inscripciones); }

    public List<Inscripcion> buscar(String texto) {
        if (texto == null || texto.isBlank()) return listar();
        String q = texto.toLowerCase();
        return inscripciones.stream()
                .filter(i -> i.getNombre().toLowerCase().contains(q)
                        || i.getDocumento().toLowerCase().contains(q)
                        || i.getCurso().toLowerCase().contains(q))
                .toList();
    }

    public String resumen() {
        Map<String, Long> porCurso = inscripciones.stream()
                .collect(Collectors.groupingBy(Inscripcion::getCurso, LinkedHashMap::new, Collectors.counting()));
        StringBuilder sb = new StringBuilder();
        sb.append("Total: ").append(inscripciones.size()).append("\nPor curso:\n");
        for (var e : porCurso.entrySet()) sb.append(" - ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        return sb.toString();
    }

    /** Datos de prueba (solo si está vacío). */
    public void cargarDatosDemo() {
        if (!inscripciones.isEmpty()) return;
        registrar("Ana Pérez", "51234567", "Prog 2");
        registrar("Luis Gómez", "49887766", "Prog 1");
        registrar("Camila Díaz", "53422110", "Base de Datos");
    }
}
