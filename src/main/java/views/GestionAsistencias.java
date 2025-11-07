package views;

import controllers.AsistenciaController;
import dao.AsistenciaDAO;
import model.Asistencia;
import model.Curso;
import model.Estudiante;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class GestionAsistencias extends JFrame {
    private AsistenciaController controller;

    // Componentes
    private JTable tablaAsistencias;
    private DefaultTableModel modeloTabla;

    private JComboBox<Curso> cboCurso;
    private JComboBox<Estudiante> cboEstudiante;
    private JDateChooser dateFechaClase;
    private JComboBox<String> cboEstado;
    private JTextArea txtNovedades;

    private JButton btnNuevo;
    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnFiltrarFecha;
    private JButton btnEstadisticas;
    private JButton btnReporte;

    private int asistenciaIdSeleccionada = -1;

    public GestionAsistencias() {
        this.controller = new AsistenciaController(this);
        initComponents();
        cargarComboBoxes();
        controller.cargarAsistencias();
    }

    private void initComponents() {
        setTitle("Control de Asistencias - UniAJC");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(crearPanelTitulo(), BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelCentral.add(crearPanelFormulario(), BorderLayout.WEST);
        panelCentral.add(crearPanelTabla(), BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(231, 76, 60));
        JLabel lblTitulo = new JLabel("✓ CONTROL DE ASISTENCIAS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo);
        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Registro de Asistencia"));
        panel.setPreferredSize(new Dimension(400, 0));

        // Curso
        JPanel panelCurso = new JPanel(new BorderLayout(5, 5));
        panelCurso.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelCurso.add(new JLabel("Curso:"), BorderLayout.NORTH);
        cboCurso = new JComboBox<>();
        cboCurso.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Curso) {
                    Curso c = (Curso) value;
                    setText(c.getNombreCurso() + " - " + c.getNombrePeriodo());
                }
                return this;
            }
        });
        cboCurso.addActionListener(e -> filtrarPorCurso());
        panelCurso.add(cboCurso, BorderLayout.CENTER);
        panel.add(panelCurso);

        // Estudiante
        JPanel panelEstudiante = new JPanel(new BorderLayout(5, 5));
        panelEstudiante.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelEstudiante.add(new JLabel("Estudiante:"), BorderLayout.NORTH);
        cboEstudiante = new JComboBox<>();
        cboEstudiante.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Estudiante) {
                    Estudiante e = (Estudiante) value;
                    setText(e.getNombre() + " (" + e.getIdentificacion() + ")");
                }
                return this;
            }
        });
        panelEstudiante.add(cboEstudiante, BorderLayout.CENTER);
        panel.add(panelEstudiante);

        // Fecha de Clase
        JPanel panelFecha = new JPanel(new BorderLayout(5, 5));
        panelFecha.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelFecha.add(new JLabel("Fecha de Clase:"), BorderLayout.NORTH);
        dateFechaClase = new JDateChooser();
        dateFechaClase.setDateFormatString("yyyy-MM-dd");
        dateFechaClase.setDate(new java.util.Date()); // Fecha actual por defecto
        panelFecha.add(dateFechaClase, BorderLayout.CENTER);
        panel.add(panelFecha);

        // Estado de Asistencia
        JPanel panelEstado = new JPanel(new BorderLayout(5, 5));
        panelEstado.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelEstado.add(new JLabel("Estado:"), BorderLayout.NORTH);
        cboEstado = new JComboBox<>(new String[]{"presente", "ausente", "tardanza"});
        panelEstado.add(cboEstado, BorderLayout.CENTER);
        panel.add(panelEstado);

        // Leyenda de estados
        JPanel panelLeyenda = new JPanel();
        panelLeyenda.setLayout(new BoxLayout(panelLeyenda, BoxLayout.Y_AXIS));
        panelLeyenda.setBorder(BorderFactory.createTitledBorder("Leyenda"));
        JLabel lblPresente = new JLabel("✓ Presente");
        lblPresente.setForeground(new Color(46, 204, 113));
        JLabel lblAusente = new JLabel("✗ Ausente");
        lblAusente.setForeground(new Color(231, 76, 60));
        JLabel lblTardanza = new JLabel("⚠ Tardanza");
        lblTardanza.setForeground(new Color(241, 196, 15));
        panelLeyenda.add(lblPresente);
        panelLeyenda.add(lblAusente);
        panelLeyenda.add(lblTardanza);
        panel.add(panelLeyenda);

        // Novedades
        JPanel panelNovedades = new JPanel(new BorderLayout());
        panelNovedades.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelNovedades.add(new JLabel("Novedades:"), BorderLayout.NORTH);
        txtNovedades = new JTextArea(3, 20);
        txtNovedades.setLineWrap(true);
        txtNovedades.setWrapStyleWord(true);
        JScrollPane scrollNov = new JScrollPane(txtNovedades);
        panelNovedades.add(scrollNov, BorderLayout.CENTER);
        panel.add(panelNovedades);

        // Botones
        panel.add(crearPanelBotones());

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        btnNuevo = new JButton("Nuevo");
        btnRegistrar = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnRegistrar.addActionListener(e -> registrarAsistencia());
        btnActualizar.addActionListener(e -> actualizarAsistencia());
        btnEliminar.addActionListener(e -> eliminarAsistencia());

        panel.add(btnNuevo);
        panel.add(btnRegistrar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Registros de Asistencia"));

        // Panel de acciones
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnTodos = new JButton("Todos");
        btnTodos.addActionListener(e -> controller.cargarAsistencias());
        panelAcciones.add(btnTodos);

        btnFiltrarFecha = new JButton("Filtrar por Fecha");
        btnFiltrarFecha.addActionListener(e -> filtrarPorFecha());
        panelAcciones.add(btnFiltrarFecha);

        btnEstadisticas = new JButton("📊 Estadísticas Estudiante");
        btnEstadisticas.addActionListener(e -> mostrarEstadisticasEstudiante());
        panelAcciones.add(btnEstadisticas);

        btnReporte = new JButton("📋 Reporte del Curso");
        btnReporte.addActionListener(e -> mostrarReporteCurso());
        panelAcciones.add(btnReporte);

        JButton btnEstadisticasGenerales = new JButton("📈 Estadísticas Generales");
        btnEstadisticasGenerales.addActionListener(e -> mostrarEstadisticasGenerales());
        panelAcciones.add(btnEstadisticasGenerales);

        panel.add(panelAcciones, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Estudiante", "Identificación", "Curso",
                "Fecha", "Estado", "Novedades"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaAsistencias = new JTable(modeloTabla);
        tablaAsistencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAsistencias.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarAsistenciaSeleccionada();
            }
        });

        // Renderizador personalizado para la columna Estado
        tablaAsistencias.getColumnModel().getColumn(5).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                                                                   boolean isSelected, boolean hasFocus, int row, int column) {
                        Component c = super.getTableCellRendererComponent(
                                table, value, isSelected, hasFocus, row, column);

                        if (value != null) {
                            String estado = value.toString();
                            if (estado.contains("Presente")) {
                                c.setForeground(new Color(46, 204, 113));
                            } else if (estado.contains("Ausente")) {
                                c.setForeground(new Color(231, 76, 60));
                            } else if (estado.contains("Tardanza")) {
                                c.setForeground(new Color(241, 196, 15));
                            }
                            setFont(getFont().deriveFont(Font.BOLD));
                        }
                        return c;
                    }
                });

        // Ajustar anchos
        tablaAsistencias.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaAsistencias.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaAsistencias.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaAsistencias.getColumnModel().getColumn(3).setPreferredWidth(200);
        tablaAsistencias.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaAsistencias.getColumnModel().getColumn(5).setPreferredWidth(100);
        tablaAsistencias.getColumnModel().getColumn(6).setPreferredWidth(250);

        JScrollPane scrollTabla = new JScrollPane(tablaAsistencias);
        panel.add(scrollTabla, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Cargar ComboBoxes
     */
    private void cargarComboBoxes() {
        // Cargar cursos
        List<Curso> cursos = controller.cargarCursos();
        cboCurso.removeAllItems();
        cboCurso.addItem(null); // Opción vacía
        for (Curso c : cursos) {
            cboCurso.addItem(c);
        }

        // Cargar estudiantes
        List<Estudiante> estudiantes = controller.cargarEstudiantes();
        cboEstudiante.removeAllItems();
        cboEstudiante.addItem(null); // Opción vacía
        for (Estudiante e : estudiantes) {
            cboEstudiante.addItem(e);
        }
    }

    // ========== MÉTODOS PÚBLICOS ==========

    public void actualizarTabla(List<Asistencia> asistencias) {
        modeloTabla.setRowCount(0);

        for (Asistencia a : asistencias) {
            String estado = a.getEstadoSimbolo() + " " +
                    a.getEstadoAsistencia().substring(0, 1).toUpperCase() +
                    a.getEstadoAsistencia().substring(1);

            Object[] fila = {
                    a.getAsistenciaId(),
                    a.getNombreEstudiante(),
                    a.getIdentificacionEstudiante(),
                    a.getNombreCurso(),
                    a.getFechaClase(),
                    estado,
                    a.getNovedades() != null ? a.getNovedades() : ""
            };
            modeloTabla.addRow(fila);
        }
    }

    public void limpiarFormulario() {
        asistenciaIdSeleccionada = -1;
        if (cboEstudiante.getItemCount() > 0) {
            cboEstudiante.setSelectedIndex(0);
        }
        dateFechaClase.setDate(new java.util.Date());
        cboEstado.setSelectedIndex(0);
        txtNovedades.setText("");
        tablaAsistencias.clearSelection();
    }

    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }

    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(
                this, mensaje, titulo, JOptionPane.YES_NO_OPTION
        );
    }

    /**
     * Mostrar reporte de asistencias
     */
    public void mostrarReporteAsistencias(List<AsistenciaDAO.AsistenciaEstadistica> estadisticas) {
        // Crear diálogo para mostrar el reporte
        JDialog dialog = new JDialog(this, "Reporte de Asistencias por Curso", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Tabla de reporte
        String[] columnas = {"Estudiante", "Total Clases", "Presentes",
                "Tardanzas", "Ausencias", "% Asistencia", "Cumple Mínimo"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (AsistenciaDAO.AsistenciaEstadistica stat : estadisticas) {
            Object[] fila = {
                    stat.nombreEstudiante,
                    stat.totalClases,
                    stat.clasesPresentes,
                    stat.tardanzas,
                    stat.ausencias,
                    String.format("%.2f%%", stat.porcentajeAsistencia),
                    stat.cumpleMinimo
            };
            modelo.addRow(fila);
        }

        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        dialog.add(scroll, BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());
        JPanel panelBtn = new JPanel();
        panelBtn.add(btnCerrar);
        dialog.add(panelBtn, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // ========== MÉTODOS PRIVADOS ==========

    private void cargarAsistenciaSeleccionada() {
        int fila = tablaAsistencias.getSelectedRow();
        if (fila >= 0) {
            asistenciaIdSeleccionada = (int) modeloTabla.getValueAt(fila, 0);

            String estadoStr = modeloTabla.getValueAt(fila, 5).toString();
            if (estadoStr.contains("Presente")) {
                cboEstado.setSelectedItem("presente");
            } else if (estadoStr.contains("Ausente")) {
                cboEstado.setSelectedItem("ausente");
            } else if (estadoStr.contains("Tardanza")) {
                cboEstado.setSelectedItem("tardanza");
            }

            txtNovedades.setText(modeloTabla.getValueAt(fila, 6).toString());
        }
    }

    private void registrarAsistencia() {
        Curso curso = (Curso) cboCurso.getSelectedItem();
        Estudiante estudiante = (Estudiante) cboEstudiante.getSelectedItem();

        if (curso == null || estudiante == null) {
            mostrarMensaje("Debe seleccionar un curso y un estudiante",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dateFechaClase.getDate() == null) {
            mostrarMensaje("Debe seleccionar una fecha",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.registrarAsistencia(
                estudiante.getEstudianteId(),
                curso.getCursoId(),
                new Date(dateFechaClase.getDate().getTime()),
                cboEstado.getSelectedItem().toString(),
                txtNovedades.getText().trim()
        );
    }

    private void actualizarAsistencia() {
        controller.actualizarAsistencia(
                asistenciaIdSeleccionada,
                cboEstado.getSelectedItem().toString(),
                txtNovedades.getText().trim()
        );
    }

    private void eliminarAsistencia() {
        controller.eliminarAsistencia(asistenciaIdSeleccionada);
    }

    private void filtrarPorCurso() {
        Curso curso = (Curso) cboCurso.getSelectedItem();
        if (curso != null) {
            controller.cargarAsistenciasPorCurso(curso.getCursoId());
        } else {
            controller.cargarAsistencias();
        }
    }

    private void filtrarPorFecha() {
        Curso curso = (Curso) cboCurso.getSelectedItem();
        if (curso == null) {
            mostrarMensaje("Debe seleccionar un curso primero",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dateFechaClase.getDate() == null) {
            mostrarMensaje("Debe seleccionar una fecha",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.cargarAsistenciasPorFecha(
                curso.getCursoId(),
                new Date(dateFechaClase.getDate().getTime())
        );
    }

    private void mostrarEstadisticasEstudiante() {
        Curso curso = (Curso) cboCurso.getSelectedItem();
        Estudiante estudiante = (Estudiante) cboEstudiante.getSelectedItem();

        if (curso == null || estudiante == null) {
            mostrarMensaje("Debe seleccionar un curso y un estudiante",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.mostrarEstadisticasEstudiante(
                estudiante.getEstudianteId(),
                curso.getCursoId()
        );
    }

    private void mostrarReporteCurso() {
        Curso curso = (Curso) cboCurso.getSelectedItem();
        if (curso == null) {
            mostrarMensaje("Debe seleccionar un curso",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.mostrarReporteAsistenciasCurso(curso.getCursoId());
    }

    private void mostrarEstadisticasGenerales() {
        String estadisticas = controller.obtenerEstadisticas();
        mostrarMensaje(estadisticas, "Estadísticas Generales",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GestionAsistencias().setVisible(true);
        });
    }
}
