package edu.cerp.checkin;

import edu.cerp.checkin.console.MainConsole;
import edu.cerp.checkin.logic.SesionService;
import edu.cerp.checkin.persist.CsvInscripcionRepository;
import edu.cerp.checkin.ui.CheckInGUI;

public class App {
    public static void main(String[] args) {
        boolean usarGui = true;
        for (String a : args) if ("--nogui".equalsIgnoreCase(a)) usarGui = false;

        // === Activar persistencia CSV ===
        var repo = new CsvInscripcionRepository("data/checkin.csv"); // cambia la ruta si querés
        SesionService service = new SesionService(repo);

        // Cargar datos demo SOLO si está vacío
        service.cargarDatosDemo();

        if (usarGui) {
            CheckInGUI.show(service);
        } else {
            MainConsole.run(service);
        }
    }
}
