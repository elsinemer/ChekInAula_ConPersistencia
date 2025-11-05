package edu.cerp.checkin.console;

import edu.cerp.checkin.logic.SesionService;
import edu.cerp.checkin.model.Inscripcion;

import java.util.List;
import java.util.Scanner;

public class MainConsole {
    public static void run(SesionService service){
        Scanner sc = new Scanner(System.in);
        int op = -1;
        while (op != 0) {
            System.out.println("\n== Check-in Aula (Consola) ==");
            System.out.println("1) Registrar  2) Listar  3) Buscar  4) Resumen  0) Salir");
            System.out.print("> ");
            String s = sc.nextLine().trim();
            if (s.isEmpty()) continue;
            try { op = Integer.parseInt(s); } catch(Exception e){ op = -1; }
            switch (op) {
                case 1 -> {
                    System.out.print("Nombre: "); String n = sc.nextLine();
                    System.out.print("Documento: "); String d = sc.nextLine();
                    System.out.print("Curso [Prog 1/Prog 2/Base de Datos]: "); String c = sc.nextLine();
                    service.registrar(n,d,c);
                    System.out.println("✔ Registrado");
                }
                case 2 -> listar(service.listar());
                case 3 -> { System.out.print("Buscar: "); String q = sc.nextLine(); listar(service.buscar(q)); }
                case 4 -> System.out.println(service.resumen());
                case 0 -> System.out.println("Adiós");
                default -> System.out.println("Opción inválida");
            }
        }
    }

    private static void listar(List<Inscripcion> ls){
        System.out.println("Nombre | Documento | Curso | Hora");
        for (Inscripcion i: ls) {
            System.out.println(i.getNombre()+" | "+i.getDocumento()+" | "+i.getCurso()+" | "+i.getFechaHora());
        }
    }
}