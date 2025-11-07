package views;

import controllers.CursoController;
import model.Curso;
import model.Docente;
import model.PeriodoAcademico;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class GestionCursos extends JFrame {

    private CursoController controller;

    // Componentes de la interfaz
    private JTable tablaCursos;
    private DefaultTableModel modeloTabla;

    private JTextField txtNombreCurso;
    private JComboBox<PeriodoAcademico> cboPeriodo;
    private JComboBox<Docente> cboDocente;
    private JTextArea txtDescripcion;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnBuscar;
    private JButton btnEstadisticas;
    private JTextField txtBuscar;

    private int cursoIdSeleccionado = -1;

    public GestionCursos() {
        this.controller = new CursoController(this);
        initComponents();
        cargarComboBoxes();
        controller.cargarCursos();
    }

    private void initComponents() {
        setTitle("Gestión de Cursos - UniAJC");
        setSize(1200, 700);
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
        panel.setBackground(new Color(155, 89, 182));
        JLabel lblTitulo = new JLabel("📚 GESTIÓN DE CURSOS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo);
        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Curso"));
        panel.setPreferredSize(new Dimension(400, 0));

        // Nombre del Curso
        panel.add(crearCampo("Nombre del Curso:",
                txtNombreCurso = new JTextField(20)));

        // Periodo Académico
        JPanel panelPeriodo = new JPanel(new BorderLayout(5, 5));
        panelPeriodo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelPeriodo.add(new JLabel("Periodo Académico:"), BorderLayout.NORTH);
        cboPeriodo = new JComboBox<>();
        cboPeriodo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PeriodoAcademico) {
                    PeriodoAcademico p = (PeriodoAcademico) value;
                    setText(p.getNombrePeriodo());
                }
                return this;
            }
        });
        panelPeriodo.add(cboPeriodo, BorderLayout.CENTER);
        panel.add(panelPeriodo);

        // Docente
        JPanel panelDocente = new JPanel(new BorderLayout(5, 5));
        panelDocente.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelDocente.add(new JLabel("Docente:"), BorderLayout.NORTH);
        cboDocente = new JComboBox<>();
        cboDocente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Docente) {
                    Docente d = (Docente) value;
                    setText(d.getNombreDocente());
                }
                return this;
            }
        });
        panelDocente.add(cboDocente, BorderLayout.CENTER);
        panel.add(panelDocente);

        // Descripción
        JPanel panelDescripcion = new JPanel(new BorderLayout());
        panelDescripcion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelDescripcion.add(new JLabel("Descripción del Curso:"), BorderLayout.NORTH);
        txtDescripcion = new JTextArea(6, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panelDescripcion.add(scrollDesc, BorderLayout.CENTER);
        panel.add(panelDescripcion);

        // Botones
        panel.add(crearPanelBotones());

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarCurso());
        btnActualizar.addActionListener(e -> actualizarCurso());
        btnEliminar.addActionListener(e -> eliminarCurso());

        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Cursos"));

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarCurso());
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);

        JButton btnRefrescar = new JButton("Todos");
        btnRefrescar.addActionListener(e -> controller.cargarCursos());
        panelBusqueda.add(btnRefrescar);

        JButton btnPorPeriodo = new JButton("Filtrar por Periodo");
        btnPorPeriodo.addActionListener(e -> filtrarPorPeriodo());
        panelBusqueda.add(btnPorPeriodo);

        btnEstadisticas = new JButton("📊 Estadísticas");
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas());
        panelBusqueda.add(btnEstadisticas);

        panel.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre del Curso", "Periodo", "Docente", "Descripción"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCursos = new JTable(modeloTabla);
        tablaCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCursos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarCursoSeleccionado();
            }
        });

        // Ajustar ancho de columnas
        tablaCursos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaCursos.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaCursos.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaCursos.getColumnModel().getColumn(3).setPreferredWidth(200);
        tablaCursos.getColumnModel().getColumn(4).setPreferredWidth(300);

        JScrollPane scrollTabla = new JScrollPane(tablaCursos);
        panel.add(scrollTabla, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearCampo(String etiqueta, JComponent componente) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(new JLabel(etiqueta), BorderLayout.NORTH);
        panel.add(componente, BorderLayout.CENTER);
        return panel;
    }


    private void cargarComboBoxes() {
        // Cargar periodos
        List<PeriodoAcademico> periodos = controller.cargarPeriodos();
        cboPeriodo.removeAllItems();
        for (PeriodoAcademico p : periodos) {
            cboPeriodo.addItem(p);
        }

        // Cargar docentes
        List<Docente> docentes = controller.cargarDocentes();
        cboDocente.removeAllItems();
        for (Docente d : docentes) {
            cboDocente.addItem(d);
        }
    }

    // ---------------------- MÉTODOS PÚBLICOS ------------------------------

    public void actualizarTabla(List<Curso> cursos) {
        modeloTabla.setRowCount(0);

        for (Curso c : cursos) {
            String descripcion = c.getDescripcionCurso();
            if (descripcion != null && descripcion.length() > 50) {
                descripcion = descripcion.substring(0, 47) + "...";
            }

            Object[] fila = {
                    c.getCursoId(),
                    c.getNombreCurso(),
                    c.getNombrePeriodo() != null ? c.getNombrePeriodo() : "N/A",
                    c.getNombreDocente() != null ? c.getNombreDocente() : "N/A",
                    descripcion != null ? descripcion : ""
            };
            modeloTabla.addRow(fila);
        }
    }

    public void limpiarFormulario() {
        cursoIdSeleccionado = -1;
        txtNombreCurso.setText("");
        txtDescripcion.setText("");
        if (cboPeriodo.getItemCount() > 0) {
            cboPeriodo.setSelectedIndex(0);
        }
        if (cboDocente.getItemCount() > 0) {
            cboDocente.setSelectedIndex(0);
        }
        tablaCursos.clearSelection();
    }

    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }

    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(
                this, mensaje, titulo, JOptionPane.YES_NO_OPTION
        );
    }

    // ========== MÉTODOS PRIVADOS ==========

    private void cargarCursoSeleccionado() {
        int fila = tablaCursos.getSelectedRow();
        if (fila >= 0) {
            cursoIdSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
            Curso c = controller.obtenerCurso(cursoIdSeleccionado);

            if (c != null) {
                txtNombreCurso.setText(c.getNombreCurso());
                txtDescripcion.setText(c.getDescripcionCurso());

                // Seleccionar periodo
                for (int i = 0; i < cboPeriodo.getItemCount(); i++) {
                    PeriodoAcademico p = cboPeriodo.getItemAt(i);
                    if (p.getPeriodoAcademicoId() == c.getPeriodoAcademicoId()) {
                        cboPeriodo.setSelectedIndex(i);
                        break;
                    }
                }

                // Seleccionar docente
                for (int i = 0; i < cboDocente.getItemCount(); i++) {
                    Docente d = cboDocente.getItemAt(i);
                    if (d.getDocenteId() == c.getDocenteId()) {
                        cboDocente.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void guardarCurso() {
        PeriodoAcademico periodo = (PeriodoAcademico) cboPeriodo.getSelectedItem();
        Docente docente = (Docente) cboDocente.getSelectedItem();

        if (periodo == null || docente == null) {
            mostrarMensaje("Debe seleccionar un periodo y un docente",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.crearCurso(
                txtNombreCurso.getText().trim(),
                periodo.getPeriodoAcademicoId(),
                docente.getDocenteId(),
                txtDescripcion.getText().trim()
        );
    }

    private void actualizarCurso() {
        PeriodoAcademico periodo = (PeriodoAcademico) cboPeriodo.getSelectedItem();
        Docente docente = (Docente) cboDocente.getSelectedItem();

        if (periodo == null || docente == null) {
            mostrarMensaje("Debe seleccionar un periodo y un docente",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.actualizarCurso(
                cursoIdSeleccionado,
                txtNombreCurso.getText().trim(),
                periodo.getPeriodoAcademicoId(),
                docente.getDocenteId(),
                txtDescripcion.getText().trim()
        );
    }

    private void eliminarCurso() {
        controller.eliminarCurso(cursoIdSeleccionado);
    }

    private void buscarCurso() {
        String criterio = txtBuscar.getText().trim();
        controller.buscarCursos(criterio);
    }

    private void filtrarPorPeriodo() {
        PeriodoAcademico periodo = (PeriodoAcademico) cboPeriodo.getSelectedItem();
        if (periodo != null) {
            controller.cargarCursosPorPeriodo(periodo.getPeriodoAcademicoId());
        }
    }

    private void mostrarEstadisticas() {
        String estadisticas = controller.obtenerEstadisticas();
        JOptionPane.showMessageDialog(
                this,
                estadisticas,
                "Estadísticas de Cursos",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GestionCursos().setVisible(true);
        });
    }
}
