package edu.cerp.checkin.ui;

import edu.cerp.checkin.logic.SesionService;
import edu.cerp.checkin.model.Inscripcion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

public class CheckInGUI {

    public static void show(SesionService service) {
        SwingUtilities.invokeLater(() -> {
            // 1) Look & Feel (Nimbus)
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignore) {}

            // 2) Ventana
            final JFrame ventana = new JFrame("Check-in Aula");
            ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ventana.setSize(640, 480);
            ventana.setLocationRelativeTo(null);

            // 3) Contenedor
            JPanel root = new JPanel(new BorderLayout());
            root.setBorder(new EmptyBorder(16, 16, 16, 16));
            ventana.setContentPane(root);

            // 4) Header
            JPanel header = new JPanel(new BorderLayout());
            JLabel titulo = new JLabel("Registro de Inscripciones 2025");
            titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
            JLabel subtitulo = new JLabel("Completa los datos y usa los botones");
            subtitulo.setFont(subtitulo.getFont().deriveFont(Font.PLAIN, 12f));
            header.add(titulo, BorderLayout.NORTH);
            header.add(subtitulo, BorderLayout.SOUTH);
            header.setBorder(new EmptyBorder(8, 0, 12, 0));

            // ===== Formulario =====
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 0, 10, 10);
            gbc.anchor = GridBagConstraints.EAST;

            JLabel nombreL = new JLabel("Nombre:");
            JLabel documentoL = new JLabel("Documento:");
            JLabel cursoL = new JLabel("Curso:");
            JLabel fechaL = new JLabel("Fecha:");

            final JTextField nombre = new JTextField(22);
            nombre.setToolTipText("Nombre y apellido del estudiante");

            // documento: usar tmp para ser 'effectively final'
            JFormattedTextField documentoTmp;
            try {
                MaskFormatter ciMask = new MaskFormatter("#######-#");
                ciMask.setPlaceholderCharacter('_');
                documentoTmp = new JFormattedTextField(ciMask);
                documentoTmp.setColumns(22);
                documentoTmp.setToolTipText("Cédula de identidad (formato #######-#)");
            } catch (ParseException e) {
                documentoTmp = new JFormattedTextField();
                documentoTmp.setColumns(22);
            }
            final JFormattedTextField documento = documentoTmp;

            final JComboBox<String> curso = new JComboBox<>(new String[]{
                    "Prog 1", "Prog 2", "Base de datos", "IA", "Otro…"
            });
            curso.setEditable(false);
            curso.setToolTipText("Selecciona el grupo/curso");

            DateFormatter dateFormatter = new DateFormatter(new SimpleDateFormat("dd/MM/yyyy"));
            final JFormattedTextField fecha = new JFormattedTextField(dateFormatter);
            fecha.setColumns(22);
            fecha.setValue(java.sql.Date.valueOf(LocalDate.now()));
            fecha.setToolTipText("Fecha del check-in (dd/MM/yyyy)");

            nombreL.setDisplayedMnemonic('N'); nombreL.setLabelFor(nombre);
            documentoL.setDisplayedMnemonic('D'); documentoL.setLabelFor(documento);
            cursoL.setDisplayedMnemonic('C'); cursoL.setLabelFor(curso);
            fechaL.setDisplayedMnemonic('F'); fechaL.setLabelFor(fecha);

            int row = 0;
            addRow(form, gbc, row++, nombreL, nombre);
            addRow(form, gbc, row++, documentoL, documento);
            addRow(form, gbc, row++, cursoL, curso);
            addRow(form, gbc, row++, fechaL, fecha);

            // 5) Toolbar superior SOLO con Listar, Buscar y Ayuda
            Action actionListar = new AbstractAction("Listar") {
                @Override public void actionPerformed(ActionEvent e) {
                    List<Inscripcion> data = service.listar();
                    showInscripcionesDialog(ventana, "Todas las inscripciones", data);
                }
            };
            Action actionBuscar = new AbstractAction("Buscar") {
                @Override public void actionPerformed(ActionEvent e) {
                    String q = JOptionPane.showInputDialog(ventana,
                            "Texto a buscar (nombre, documento o curso):", "Buscar", JOptionPane.QUESTION_MESSAGE);
                    if (q != null) {
                        List<Inscripcion> data = service.buscar(q.trim());
                        showInscripcionesDialog(ventana, "Resultados para: " + q.trim(), data);
                    }
                }
            };
            Action actionAyuda = new AbstractAction("?") {
                @Override public void actionPerformed(ActionEvent e) {
                    showHelpDialog(ventana);
                }
            };

            JToolBar toolbar = new JToolBar();
            toolbar.setFloatable(false);
            toolbar.setBorder(new EmptyBorder(0, 0, 8, 0));
            // 👇 cambio clave: layout que respeta el preferredSize (no comprime)
            toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));

            JButton tbListar = new JButton(actionListar);
            JButton tbBuscar = new JButton(actionBuscar);
            JButton tbHelp = new JButton(actionAyuda);
            tbHelp.setToolTipText("Ayuda (F1)");

            for (JButton b : new JButton[]{tbListar, tbBuscar, tbHelp}) {
                b.setFocusable(false);
                b.setBorder(new EmptyBorder(6, 12, 6, 12));
                b.setFont(b.getFont().deriveFont(Font.BOLD, 13f));
                lockButtonSize(b); // 👈 evita que se corten las palabras
            }

            toolbar.add(tbListar);
            toolbar.add(tbBuscar);
            toolbar.add(tbHelp);

            // 6) Pie — Cancelar + Registrar (aquí va Registrar)
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            JButton btnCancelar = new JButton("Cancelar");
            JButton btnRegistrar = new JButton("Registrar");
            btnRegistrar.setMnemonic('R');
            // 👇 evitar que se achiquen en ventanas angostas
            lockButtonSize(btnCancelar);
            lockButtonSize(btnRegistrar);

            bottom.add(btnCancelar);
            bottom.add(btnRegistrar);
            bottom.setBorder(new EmptyBorder(12, 0, 0, 0));

            // Acción Registrar (reutilizable por Enter / Ctrl+R / botón)
            Action actionRegistrar = new AbstractAction("Registrar") {
                @Override public void actionPerformed(ActionEvent e) {
                    doRegistrar(service, ventana, nombre, documento, curso);
                }
            };
            btnRegistrar.addActionListener(actionRegistrar);

            // 7) Atajos: Ctrl+R/L/B, F1, Esc, Enter (default button)
            int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
            JRootPane rootPane = ventana.getRootPane();

            rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_R, mask), "registrar");
            rootPane.getActionMap().put("registrar", actionRegistrar);

            rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_L, mask), "listar");
            rootPane.getActionMap().put("listar", actionListar);

            rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_B, mask), "buscar");
            rootPane.getActionMap().put("buscar", actionBuscar);

            rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "ayuda");
            rootPane.getActionMap().put("ayuda", actionAyuda);

            rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cerrar");
            rootPane.getActionMap().put("cerrar", new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) { ventana.dispose(); }
            });

            // Enter = Registrar
            ventana.getRootPane().setDefaultButton(btnRegistrar);

            // 8) Ensamblado
            JPanel north = new JPanel(new BorderLayout());
            north.add(toolbar, BorderLayout.NORTH);
            north.add(header, BorderLayout.SOUTH);

            root.add(north, BorderLayout.NORTH);
            root.add(form, BorderLayout.CENTER);
            root.add(bottom, BorderLayout.SOUTH);

            // 9) Listeners finales
            btnCancelar.addActionListener(e -> ventana.dispose());
            nombre.requestFocusInWindow();

            ventana.setVisible(true);
        });
    }

    private static void addRow(JPanel panel, GridBagConstraints gbc, int row, JComponent label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }

    // ==== Lógica de registro ====
    private static void doRegistrar(SesionService service, JFrame ventana,
                                    JTextField nombre, JFormattedTextField documento, JComboBox<String> curso) {
        String n = nombre.getText().trim();
        String d = documento.getText() == null ? "" : documento.getText().trim();
        String c = (String) (curso.getSelectedItem() == null ? "" : curso.getSelectedItem());

        if (n.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "El campo Nombre es obligatorio.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            nombre.requestFocusInWindow();
            return;
        }

        service.registrar(n, d, c);
        JOptionPane.showMessageDialog(ventana,
                "✔ Registrado\nNombre: " + n + "\nDocumento: " + d + "\nCurso: " + c,
                "Check-in", JOptionPane.INFORMATION_MESSAGE);

        // limpiar
        nombre.setText("");
        documento.setValue(null);
        curso.setSelectedIndex(0);
        nombre.requestFocusInWindow();
    }

    // ==== Helpers UI ====
    private static void showInscripcionesDialog(Window owner, String titulo, List<Inscripcion> data) {
        JDialog dlg = new JDialog(owner, titulo, Dialog.ModalityType.MODELESS);
        JTable table = new JTable(toTableModel(data));
        table.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(table);
        dlg.getContentPane().add(sp, BorderLayout.CENTER);
        dlg.setSize(700, 380);
        dlg.setLocationRelativeTo(owner);
        dlg.setVisible(true);
    }

    private static DefaultTableModel toTableModel(List<Inscripcion> data) {
        String[] cols = {"Nombre", "Documento", "Curso", "Fecha/Hora"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        if (data != null) {
            for (Inscripcion i : data) {
                model.addRow(new Object[]{
                        i.getNombre(),
                        i.getDocumento(),
                        i.getCurso(),
                        i.getFechaHora()
                });
            }
        }
        return model;
    }

    private static void showHelpDialog(Window owner) {
        String html = """
                <html>
                <h2>Ayuda</h2>
                <p><b>Acciones</b></p>
                <ul>
                  <li><b>Registrar</b>: completa Nombre, Documento y Curso. Presiona <b>Enter</b> o el botón <b>Registrar</b> (abajo).</li>
                  <li><b>Listar</b>: muestra todas las inscripciones en una tabla.</li>
                  <li><b>Buscar</b>: filtra por nombre, documento o curso.</li>
                </ul>
                <p><b>Atajos</b></p>
                <ul>
                  <li><b>Ctrl+R</b>: Registrar</li>
                  <li><b>Ctrl+L</b>: Listar</li>
                  <li><b>Ctrl+B</b>: Buscar</li>
                  <li><b>Enter</b>: Registrar (botón por defecto)</li>
                  <li><b>F1</b>: Ayuda</li>
                  <li><b>Esc</b>: Cerrar ventana</li>
                </ul>
                <p><b>Tips</b></p>
                <ul>
                  <li>El campo <b>Nombre</b> es obligatorio.</li>
                  <li>Documento acepta el formato <code>######-#</code>.</li>
                  <li>La tabla de resultados es de solo lectura.</li>
                </ul>
                <p><b> ¡Gracias! Creado por Elsi Nemer 2025</b></p>
                </html>
                """;
        JOptionPane.showMessageDialog(owner, new JLabel(html),
                "Ayuda", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==== NUEVO: evita que los botones se encojan y corten texto ====
    private static void lockButtonSize(AbstractButton b) {
        Dimension d = b.getPreferredSize();
        b.setPreferredSize(d);
        b.setMinimumSize(d);
        b.setMaximumSize(d);
    }
}
