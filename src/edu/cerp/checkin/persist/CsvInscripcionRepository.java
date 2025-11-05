package edu.cerp.checkin.persist;

import edu.cerp.checkin.model.Inscripcion;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Persistencia simple en CSV: nombre,documento,curso,fechaHoraISO */
public class CsvInscripcionRepository implements InscripcionRepository {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final Path path;
    private final boolean includeHeader;

    public CsvInscripcionRepository(String filePath) {
        this(filePath, true);
    }

    public CsvInscripcionRepository(String filePath, boolean includeHeader) {
        this.path = Paths.get(filePath);
        this.includeHeader = includeHeader;
        ensureFile();
    }

    private void ensureFile() {
        try {
            if (Files.notExists(path)) {
                Path parent = path.getParent();
                if (parent != null && Files.notExists(parent)) {
                    Files.createDirectories(parent);
                }
                try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                    if (includeHeader) bw.write("nombre,documento,curso,fechaHora\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear archivo CSV: " + path, e);
        }
    }

    @Override
    public synchronized List<Inscripcion> loadAll() throws IOException {
        List<Inscripcion> out = new ArrayList<>();
        if (Files.notExists(path)) return out;

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first && includeHeader) { first = false; continue; }
                if (line.isBlank()) continue;
                String[] cols = parseCsvLine(line);
                if (cols.length < 4) continue;
                String nombre = unescape(cols[0]);
                String documento = unescape(cols[1]);
                String curso = unescape(cols[2]);
                LocalDateTime fh = LocalDateTime.parse(cols[3], FMT);
                out.add(new Inscripcion(nombre, documento, curso, fh));
            }
        }
        return out;
    }

    @Override
    public synchronized void append(Inscripcion ins) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            bw.write(String.join(",",
                    escape(ins.getNombre()),
                    escape(ins.getDocumento()),
                    escape(ins.getCurso()),
                    ins.getFechaHora().format(FMT)
            ));
            bw.write("\n");
        }
    }

    @Override
    public synchronized void saveAll(List<Inscripcion> all) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            if (includeHeader) bw.write("nombre,documento,curso,fechaHora\n");
            for (Inscripcion i : all) {
                bw.write(String.join(",",
                        escape(i.getNombre()),
                        escape(i.getDocumento()),
                        escape(i.getCurso()),
                        i.getFechaHora().format(FMT)
                ));
                bw.write("\n");
            }
        }
    }

    // ==== utilidades CSV ====
    private static String escape(String s) {
        if (s == null) return "";
        boolean needQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String v = s.replace("\"", "\"\"");
        return needQuote ? "\"" + v + "\"" : v;
    }

    private static String unescape(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length()-1).replace("\"\"", "\"");
        }
        return s;
    }

    /** Split CSV básico que respeta comillas. */
    private static String[] parseCsvLine(String line) {
        List<String> cols = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '\"') {
                    if (i+1 < line.length() && line.charAt(i+1) == '\"') { cur.append('\"'); i++; }
                    else inQuotes = false;
                } else cur.append(c);
            } else {
                if (c == ',') { cols.add(cur.toString()); cur.setLength(0); }
                else if (c == '\"') inQuotes = true;
                else cur.append(c);
            }
        }
        cols.add(cur.toString());
        return cols.toArray(new String[0]);
    }
}
