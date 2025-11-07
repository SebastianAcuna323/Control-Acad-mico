package views;


import controllers.CalificacionController;
import dao.CalificacionDAO;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class GestionCalificaciones extends JFrame {
    private CalificacionController controller;

    // Tabs principales
    private JTabbedPane tabbedPane;

    // Tab 1: Estructura de Evaluación
    private JComboBox<Curso> cboCursoEstructura;
    private JList<CorteEvaluacion> listCortes;
    private DefaultListModel<CorteEvaluacion> modeloCortes;
    private JTable tablaComponentes;
    private DefaultTableModel modeloComponentes;
    private JTextField txtNombreComponente;
    private JSpinner spinPorcentaje;

    // Tab 2: Registro de Calificaciones
    private JComboBox<Curso> cboCursoCalif;
    private JComboBox<Estudiante> cboEstudiante;
    private JComboBox<ComponenteEvaluacion> cboComponente;
    private JSpinner spinNota;
    private JTextArea txtComentarios;
    private JTable tablaCalificaciones;
    private DefaultTableModel modeloCalificaciones;

    private int calificacionIdSeleccionada = -1;
    private int corteSeleccionadoId = -1;

    public GestionCalificaciones() {
        this.controller = new CalificacionController(this);
        initComponents();
        cargarCursosIniciales();
    }

    private void initComponents() {
        setTitle("Gestión de Calificaciones - UniAJC");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(crearPanelTitulo(), BorderLayout.NORTH);

        // Crear tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📋 Estructura de Evaluación", crearTabEstructura());
        tabbedPane.addTab("📝 Registro de Calificaciones", crearTabCalificaciones());
        tabbedPane.addTab("📊 Reportes y Consultas", crearTabReportes());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(230, 126, 34));
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("📝 GESTIÓN DE CALIFICACIONES");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Sistema de 3 Cortes: 30% - 30% - 40%");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(Color.WHITE);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblSubtitulo);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // ========== TAB 1: ESTRUCTURA DE EVALUACIÓN ==========

    private JPanel crearTabEstructura() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior: Selección de curso
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.add(new JLabel("Curso:"));
        cboCursoEstructura = new JComboBox<>();
        cboCursoEstructura.setPreferredSize(new Dimension(400, 25));
        cboCursoEstructura.setRenderer(crearRendererCurso());
        cboCursoEstructura.addActionListener(e -> cargarEstructuraCurso());
        panelSuperior.add(cboCursoEstructura);

        JButton btnCrearEstructura = new JButton("Crear Estructura Automática (3 Cortes)");
        btnCrearEstructura.addActionListener(e -> crearEstructuraAutomatica());
        panelSuperior.add(btnCrearEstructura);

        panel.add(panelSuperior, BorderLayout.NORTH);

        // Panel central: Split entre cortes y componentes
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(crearPanelCortes());
        splitPane.setRightComponent(crearPanelComponentes());
        splitPane.setDividerLocation(300);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelCortes() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Cortes de Evaluación"));

        modeloCortes = new DefaultListModel<>();
        listCortes = new JList<>(modeloCortes);
        listCortes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listCortes.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CorteEvaluacion) {
                    CorteEvaluacion corte = (CorteEvaluacion) value;
                    setText(corte.getNombreCorte() + " - " + corte.getPorcentaje() + "%");
                }
                return this;
            }
        });
        listCortes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarComponentesCorteSeleccionado();
            }
        });

        JScrollPane scrollCortes = new JScrollPane(listCortes);
        panel.add(scrollCortes, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelComponentes() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Componentes de Evaluación"));

        // Formulario para agregar componentes
        JPanel panelForm = new JPanel(new GridLayout(3, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panelForm.add(new JLabel("Nombre:"));
        txtNombreComponente = new JTextField();
        panelForm.add(txtNombreComponente);

        panelForm.add(new JLabel("Porcentaje:"));
        spinPorcentaje = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 5.0));
        panelForm.add(spinPorcentaje);

        JButton btnAgregarComponente = new JButton("Agregar Componente");
        btnAgregarComponente.addActionListener(e -> agregarComponente());
        panelForm.add(new JLabel());
        panelForm.add(btnAgregarComponente);

        panel.add(panelForm, BorderLayout.NORTH);

        // Tabla de componentes
        String[] columnas = {"ID", "Nombre", "% Corte", "% Total"};
        modeloComponentes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaComponentes = new JTable(modeloComponentes);
        JScrollPane scrollComp = new JScrollPane(tablaComponentes);
        panel.add(scrollComp, BorderLayout.CENTER);

        return panel;
    }

    // ========== TAB 2: REGISTRO DE CALIFICACIONES ==========

    private JPanel crearTabCalificaciones() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel izquierdo: Formulario
        JPanel panelForm = crearFormularioCalificaciones();
        panel.add(panelForm, BorderLayout.WEST);

        // Panel derecho: Tabla de calificaciones
        JPanel panelTabla = crearTablaCalificaciones();
        panel.add(panelTabla, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearFormularioCalificaciones() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Registrar Calificación"));
        panel.setPreferredSize(new Dimension(400, 0));

        // Curso
        JPanel pCurso = new JPanel(new BorderLayout(5, 5));
        pCurso.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pCurso.add(new JLabel("Curso:"), BorderLayout.NORTH);
        cboCursoCalif = new JComboBox<>();
        cboCursoCalif.setRenderer(crearRendererCurso());
        cboCursoCalif.addActionListener(e -> cargarComponentesCurso());
        pCurso.add(cboCursoCalif, BorderLayout.CENTER);
        panel.add(pCurso);

        // Estudiante
        JPanel pEst = new JPanel(new BorderLayout(5, 5));
        pEst.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pEst.add(new JLabel("Estudiante:"), BorderLayout.NORTH);
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
        pEst.add(cboEstudiante, BorderLayout.CENTER);
        panel.add(pEst);

        // Componente
        JPanel pComp = new JPanel(new BorderLayout(5, 5));
        pComp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pComp.add(new JLabel("Componente:"), BorderLayout.NORTH);
        cboComponente = new JComboBox<>();
        cboComponente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ComponenteEvaluacion) {
                    ComponenteEvaluacion c = (ComponenteEvaluacion) value;
                    setText(c.getNombreCorte() + " - " + c.getNombreComponente() +
                            " (" + c.getPorcentaje() + "%)");
                }
                return this;
            }
        });
        pComp.add(cboComponente, BorderLayout.CENTER);
        panel.add(pComp);

        // Nota
        JPanel pNota = new JPanel(new BorderLayout(5, 5));
        pNota.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pNota.add(new JLabel("Nota (0.0 - 5.0):"), BorderLayout.NORTH);
        spinNota = new JSpinner(new SpinnerNumberModel(3.0, 0.0, 5.0, 0.1));
        ((JSpinner.DefaultEditor)spinNota.getEditor()).getTextField().setColumns(5);
        pNota.add(spinNota, BorderLayout.CENTER);
        panel.add(pNota);

        // Comentarios
        JPanel pCom = new JPanel(new BorderLayout(5, 5));
        pCom.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pCom.add(new JLabel("Comentarios:"), BorderLayout.NORTH);
        txtComentarios = new JTextArea(4, 20);
        txtComentarios.setLineWrap(true);
        pCom.add(new JScrollPane(txtComentarios), BorderLayout.CENTER);
        panel.add(pCom);

        // Botones
        JPanel pBotones = new JPanel(new GridLayout(2, 2, 5, 5));
        pBotones.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        JButton btnNuevo = new JButton("Nuevo");
        btnNuevo.addActionListener(e -> limpiarFormularioCalificacion());
        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.addActionListener(e -> registrarCalificacion());
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizarCalificacion());
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarCalificacion());

        pBotones.add(btnNuevo);
        pBotones.add(btnRegistrar);
        pBotones.add(btnActualizar);
        pBotones.add(btnEliminar);
        panel.add(pBotones);

        return panel;
    }

    private JPanel crearTablaCalificaciones() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Calificaciones Registradas"));

        // Botones de acciones
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnVerTodas = new JButton("Ver Todas");
        btnVerTodas.addActionListener(e -> verTodasCalificaciones());
        JButton btnVerEstudiante = new JButton("Ver por Estudiante");
        btnVerEstudiante.addActionListener(e -> verPorEstudiante());
        JButton btnNotaFinal = new JButton("📊 Calcular Nota Final");
        btnNotaFinal.addActionListener(e -> mostrarNotaFinal());

        panelAcciones.add(btnVerTodas);
        panelAcciones.add(btnVerEstudiante);
        panelAcciones.add(btnNotaFinal);

        panel.add(panelAcciones, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Estudiante", "Curso", "Corte", "Componente",
                "% Comp", "Nota", "Concepto", "Aporte"};
        modeloCalificaciones = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCalificaciones = new JTable(modeloCalificaciones);
        tablaCalificaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCalificaciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarCalificacionSeleccionada();
            }
        });

        // Renderer de colores para la nota
        tablaCalificaciones.getColumnModel().getColumn(6).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                                                                   boolean isSelected, boolean hasFocus, int row, int column) {
                        Component c = super.getTableCellRendererComponent(
                                table, value, isSelected, hasFocus, row, column);

                        if (value != null) {
                            try {
                                double nota = Double.parseDouble(value.toString());
                                if (nota >= 4.5) c.setForeground(new Color(39, 174, 96));
                                else if (nota >= 4.0) c.setForeground(new Color(46, 204, 113));
                                else if (nota >= 3.5) c.setForeground(new Color(52, 152, 219));
                                else if (nota >= 3.0) c.setForeground(new Color(241, 196, 15));
                                else if (nota >= 2.0) c.setForeground(new Color(230, 126, 34));
                                else c.setForeground(new Color(231, 76, 60));
                                setFont(getFont().deriveFont(Font.BOLD));
                            } catch (Exception ex) {}
                        }
                        return c;
                    }
                });

        JScrollPane scroll = new JScrollPane(tablaCalificaciones);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

// Continúa en parte
// CONTINUACIÓN DE GestionCalificaciones.java
// Agregar estos métodos a la clase

    // ========== TAB 3: REPORTES ==========

    private JPanel crearTabReportes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelOpciones = new JPanel();
        panelOpciones.setLayout(new GridLayout(4, 2, 20, 20));

        // Selección de curso
        panelOpciones.add(new JLabel("Seleccionar Curso:"));
        JComboBox<Curso> cboCursoReporte = new JComboBox<>();
        cboCursoReporte.setRenderer(crearRendererCurso());
        panelOpciones.add(cboCursoReporte);

        // Botones de reportes
        JButton btnReporteCompleto = new JButton("📋 Reporte Completo del Curso");
        btnReporteCompleto.setPreferredSize(new Dimension(300, 50));
        btnReporteCompleto.addActionListener(e -> {
            Curso curso = (Curso) cboCursoReporte.getSelectedItem();
            if (curso != null) {
                controller.mostrarReporteCurso(curso.getCursoId());
            }
        });
        panelOpciones.add(btnReporteCompleto);

        JButton btnRanking = new JButton("🏆 Ranking de Estudiantes");
        btnRanking.setPreferredSize(new Dimension(300, 50));
        btnRanking.addActionListener(e -> {
            Curso curso = (Curso) cboCursoReporte.getSelectedItem();
            if (curso != null) {
                controller.mostrarRanking(curso.getCursoId());
            }
        });
        panelOpciones.add(btnRanking);

        JButton btnEstadisticas = new JButton("📊 Estadísticas del Curso");
        btnEstadisticas.setPreferredSize(new Dimension(300, 50));
        btnEstadisticas.addActionListener(e -> {
            Curso curso = (Curso) cboCursoReporte.getSelectedItem();
            if (curso != null) {
                String stats = controller.obtenerEstadisticas(curso.getCursoId());
                mostrarMensaje(stats, "Estadísticas", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panelOpciones.add(btnEstadisticas);

        panel.add(panelOpciones, BorderLayout.NORTH);

        // Cargar cursos al tab
        List<Curso> cursos = controller.cargarCursos();
        for (Curso c : cursos) {
            cboCursoReporte.addItem(c);
        }

        return panel;
    }

    // ========== MÉTODOS AUXILIARES ==========

    private DefaultListCellRenderer crearRendererCurso() {
        return new DefaultListCellRenderer() {
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
        };
    }

    private void cargarCursosIniciales() {
        List<Curso> cursos = controller.cargarCursos();
        cboCursoEstructura.removeAllItems();
        cboCursoCalif.removeAllItems();

        for (Curso c : cursos) {
            cboCursoEstructura.addItem(c);
            cboCursoCalif.addItem(c);
        }

        // Cargar estudiantes
        List<Estudiante> estudiantes = controller.cargarEstudiantes();
        for (Estudiante e : estudiantes) {
            cboEstudiante.addItem(e);
        }
    }

    // ========== MÉTODOS TAB ESTRUCTURA ==========

    private void crearEstructuraAutomatica() {
        Curso curso = (Curso) cboCursoEstructura.getSelectedItem();
        if (curso == null) {
            mostrarMensaje("Debe seleccionar un curso", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        controller.crearEstructuraEvaluacion(curso.getCursoId(), curso.getPeriodoAcademicoId());
    }

    private void cargarEstructuraCurso() {
        Curso curso = (Curso) cboCursoEstructura.getSelectedItem();
        if (curso != null) {
            controller.cargarCortesDelCurso(curso.getCursoId());
        }
    }

    private void cargarComponentesCorteSeleccionado() {
        CorteEvaluacion corte = listCortes.getSelectedValue();
        if (corte != null) {
            corteSeleccionadoId = corte.getCorteEvaluacionId();
            controller.cargarComponentesDelCorte(corteSeleccionadoId);
        }
    }

    private void agregarComponente() {
        if (corteSeleccionadoId <= 0) {
            mostrarMensaje("Debe seleccionar un corte primero", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        controller.agregarComponente(
                corteSeleccionadoId,
                txtNombreComponente.getText().trim(),
                (Double) spinPorcentaje.getValue()
        );
    }

    public void actualizarCortes(List<CorteEvaluacion> cortes) {
        modeloCortes.clear();
        for (CorteEvaluacion corte : cortes) {
            modeloCortes.addElement(corte);
        }
    }

    public void actualizarComponentes(List<ComponenteEvaluacion> componentes) {
        modeloComponentes.setRowCount(0);
        double sumaTotal = 0;

        for (ComponenteEvaluacion comp : componentes) {
            Object[] fila = {
                    comp.getComponenteEvaluacionId(),
                    comp.getNombreComponente(),
                    String.format("%.1f%%", comp.getPorcentaje()),
                    String.format("%.2f%%", comp.calcularPorcentajeSobreTotal())
            };
            modeloComponentes.addRow(fila);
            sumaTotal += comp.getPorcentaje();
        }

        // Limpiar formulario
        txtNombreComponente.setText("");
        spinPorcentaje.setValue(0.0);

        // Mostrar suma de porcentajes
        if (sumaTotal > 0) {
            String mensaje = String.format("Suma de porcentajes: %.1f%% / 100%%", sumaTotal);
            if (Math.abs(sumaTotal - 100) < 0.01) {
                mensaje += " ✓";
            }
            modeloComponentes.setRowCount(modeloComponentes.getRowCount());
        }
    }

    // ========== MÉTODOS TAB CALIFICACIONES ==========

    private void cargarComponentesCurso() {
        Curso curso = (Curso) cboCursoCalif.getSelectedItem();
        if (curso != null) {
            List<ComponenteEvaluacion> componentes =
                    controller.calificacionDAO.listarComponentesPorCurso(curso.getCursoId());
            cboComponente.removeAllItems();
            for (ComponenteEvaluacion comp : componentes) {
                cboComponente.addItem(comp);
            }

            // Cargar calificaciones
            controller.cargarCalificacionesCurso(curso.getCursoId());
        }
    }

    private void registrarCalificacion() {
        Estudiante estudiante = (Estudiante) cboEstudiante.getSelectedItem();
        ComponenteEvaluacion componente = (ComponenteEvaluacion) cboComponente.getSelectedItem();

        if (estudiante == null || componente == null) {
            mostrarMensaje("Debe seleccionar un estudiante y un componente",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.registrarCalificacion(
                estudiante.getEstudianteId(),
                componente.getComponenteEvaluacionId(),
                (Double) spinNota.getValue(),
                txtComentarios.getText().trim()
        );
    }

    private void actualizarCalificacion() {
        controller.actualizarCalificacion(
                calificacionIdSeleccionada,
                (Double) spinNota.getValue(),
                txtComentarios.getText().trim()
        );
    }

    private void eliminarCalificacion() {
        controller.eliminarCalificacion(calificacionIdSeleccionada);
    }

    private void verTodasCalificaciones() {
        Curso curso = (Curso) cboCursoCalif.getSelectedItem();
        if (curso != null) {
            controller.cargarCalificacionesCurso(curso.getCursoId());
        }
    }

    private void verPorEstudiante() {
        Curso curso = (Curso) cboCursoCalif.getSelectedItem();
        Estudiante estudiante = (Estudiante) cboEstudiante.getSelectedItem();

        if (curso != null && estudiante != null) {
            controller.cargarCalificacionesEstudiante(
                    estudiante.getEstudianteId(),
                    curso.getCursoId()
            );
        }
    }

    private void mostrarNotaFinal() {
        Curso curso = (Curso) cboCursoCalif.getSelectedItem();
        Estudiante estudiante = (Estudiante) cboEstudiante.getSelectedItem();

        if (curso != null && estudiante != null) {
            controller.mostrarNotaFinal(
                    estudiante.getEstudianteId(),
                    curso.getCursoId()
            );
        }
    }

    private void cargarCalificacionSeleccionada() {
        int fila = tablaCalificaciones.getSelectedRow();
        if (fila >= 0) {
            calificacionIdSeleccionada = (int) modeloCalificaciones.getValueAt(fila, 0);
            double nota = Double.parseDouble(modeloCalificaciones.getValueAt(fila, 6).toString());
            spinNota.setValue(nota);
        }
    }

    public void actualizarTablaCalificaciones(List<Calificacion> calificaciones) {
        modeloCalificaciones.setRowCount(0);

        for (Calificacion cal : calificaciones) {
            Object[] fila = {
                    cal.getCalificacionId(),
                    cal.getNombreEstudiante(),
                    cal.getNombreCurso(),
                    cal.getNombreCorte(),
                    cal.getNombreComponente(),
                    String.format("%.1f%%", cal.getPorcentajeComponente()),
                    String.format("%.2f", cal.getNota()),
                    cal.getConceptoNota(),
                    String.format("%.3f", cal.calcularAporteNotaFinal())
            };
            modeloCalificaciones.addRow(fila);
        }
    }

    public void limpiarFormularioCalificacion() {
        calificacionIdSeleccionada = -1;
        spinNota.setValue(3.0);
        txtComentarios.setText("");
        tablaCalificaciones.clearSelection();
    }

    // ========== MÉTODOS DE DIÁLOGO ==========

    public void mostrarReporteNotas(List<CalificacionDAO.NotaFinal> notas) {
        JDialog dialog = new JDialog(this, "Reporte de Notas Finales", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        String[] columnas = {"Estudiante", "Identificación", "Nota Final", "Concepto", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        for (CalificacionDAO.NotaFinal nf : notas) {
            Object[] fila = {
                    nf.nombreEstudiante,
                    nf.identificacion,
                    String.format("%.2f", nf.notaFinal),
                    nf.conceptoFinal,
                    nf.estado
            };
            modelo.addRow(fila);
        }

        JTable tabla = new JTable(modelo);

        // Colorear estados
        tabla.getColumnModel().getColumn(4).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                                                                   boolean isSelected, boolean hasFocus, int row, int column) {
                        Component c = super.getTableCellRendererComponent(
                                table, value, isSelected, hasFocus, row, column);
                        if ("Aprobado".equals(value)) {
                            c.setForeground(new Color(39, 174, 96));
                        } else {
                            c.setForeground(new Color(231, 76, 60));
                        }
                        setFont(getFont().deriveFont(Font.BOLD));
                        return c;
                    }
                });

        dialog.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());
        JPanel panelBtn = new JPanel();
        panelBtn.add(btnCerrar);
        dialog.add(panelBtn, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public void mostrarRanking(List<CalificacionDAO.NotaFinal> ranking) {
        JDialog dialog = new JDialog(this, "Ranking de Estudiantes", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        String[] columnas = {"Puesto", "Estudiante", "Nota Final", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        for (CalificacionDAO.NotaFinal nf : ranking) {
            String puesto = nf.ranking + "°";
            if (nf.ranking == 1) puesto = "🥇 1°";
            else if (nf.ranking == 2) puesto = "🥈 2°";
            else if (nf.ranking == 3) puesto = "🥉 3°";

            Object[] fila = {
                    puesto,
                    nf.nombreEstudiante,
                    String.format("%.2f", nf.notaFinal),
                    nf.estado
            };
            modelo.addRow(fila);
        }

        JTable tabla = new JTable(modelo);
        dialog.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());
        JPanel panelBtn = new JPanel();
        panelBtn.add(btnCerrar);
        dialog.add(panelBtn, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // ========== MÉTODOS PÚBLICOS ==========

    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }

    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(
                this, mensaje, titulo, JOptionPane.YES_NO_OPTION
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GestionCalificaciones().setVisible(true);
        });
    }
}
