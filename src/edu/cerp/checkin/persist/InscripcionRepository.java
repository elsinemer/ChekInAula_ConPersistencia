package edu.cerp.checkin.persist;

import edu.cerp.checkin.model.Inscripcion;
import java.io.IOException;
import java.util.List;

public interface InscripcionRepository {
    List<Inscripcion> loadAll() throws IOException;
    void append(Inscripcion ins) throws IOException;
    void saveAll(List<Inscripcion> all) throws IOException; // opcional
}
