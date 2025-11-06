package views;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import controllers.PeriodoAcademicoController;
import model.PeriodoAcademico;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class GestionPeriodos extends JFrame {

    private PeriodoAcademicoController controller;

    // Componentes
    private JTable tablaPeriodos;
    private DefaultTableModel modeloTabla;

    private JTextField txtNombrePeriodo;
    private JDateChooser dateInicio;
    private JDateChooser dateFin;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnBuscar;
    private JButton btnEstadisticas;
    private JTextField txtBuscar;

    private int periodoIdSeleccionado = -1;



    public GestionPeriodos() {
        try {
            this.controller = new PeriodoAcademicoController(this);
            initComponents();
            controller.cargarPeriodos();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Error al cargar Gestión de Periodos:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void initComponents() {
        setTitle("Gestión de Periodos Académicos - UniAJC");
        setSize(1000, 600);
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
        panel.setBackground(new Color(241, 196, 15));
        JLabel lblTitulo = new JLabel("📅 GESTIÓN DE PERIODOS ACADÉMICOS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo);
        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Periodo"));
        panel.setPreferredSize(new Dimension(350, 0));

        // Nombre del Periodo
        panel.add(crearCampo("Nombre del Periodo:",
                txtNombrePeriodo = new JTextField(20)));

        // Fecha de Inicio
        JPanel panelInicio = new JPanel(new BorderLayout(5, 5));
        panelInicio.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelInicio.add(new JLabel("Fecha de Inicio:"), BorderLayout.NORTH);
        dateInicio = new JDateChooser();
        dateInicio.setDateFormatString("yyyy-MM-dd");
        panelInicio.add(dateInicio, BorderLayout.CENTER);
        panel.add(panelInicio);

        // Fecha de Fin
        JPanel panelFin = new JPanel(new BorderLayout(5, 5));
        panelFin.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelFin.add(new JLabel("Fecha de Fin:"), BorderLayout.NORTH);
        dateFin = new JDateChooser();
        dateFin.setDateFormatString("yyyy-MM-dd");
        panelFin.add(dateFin, BorderLayout.CENTER);
        panel.add(panelFin);

        // Información
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información"));
        JLabel lblInfo1 = new JLabel("• El periodo debe durar entre 30 días y 1 año");
        JLabel lblInfo2 = new JLabel("• No debe solaparse con otros periodos");
        lblInfo1.setFont(new Font("Arial", Font.PLAIN, 11));
        lblInfo2.setFont(new Font("Arial", Font.PLAIN, 11));
        panelInfo.add(lblInfo1);
        panelInfo.add(lblInfo2);
        panel.add(panelInfo);

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
        btnGuardar.addActionListener(e -> guardarPeriodo());
        btnActualizar.addActionListener(e -> actualizarPeriodo());
        btnEliminar.addActionListener(e -> eliminarPeriodo());

        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Periodos"));

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarPeriodo());
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);

        JButton btnRefrescar = new JButton("Todos");
        btnRefrescar.addActionListener(e -> controller.cargarPeriodos());
        panelBusqueda.add(btnRefrescar);

        JButton btnActivos = new JButton("✓ Activos");
        btnActivos.addActionListener(e -> controller.cargarPeriodosActivos());
        panelBusqueda.add(btnActivos);

        btnEstadisticas = new JButton("📊 Estadísticas");
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas());
        panelBusqueda.add(btnEstadisticas);

        panel.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre del Periodo", "Fecha Inicio",
                "Fecha Fin", "Duración (días)", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPeriodos = new JTable(modeloTabla);
        tablaPeriodos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPeriodos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarPeriodoSeleccionado();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaPeriodos);
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

    // ========== MÉTODOS PÚBLICOS ==========

    public void actualizarTabla(List<PeriodoAcademico> periodos) {
        modeloTabla.setRowCount(0);
        java.util.Date hoy = new java.util.Date();

        for (PeriodoAcademico p : periodos) {
            // Calcular duración
            long duracion = (p.getFechaFin().getTime() - p.getFechaInicio().getTime())
                    / (1000 * 60 * 60 * 24);

            // Determinar estado
            String estado;
            if (p.getFechaFin().before(hoy)) {
                estado = "Finalizado";
            } else if (p.getFechaInicio().after(hoy)) {
                estado = "Próximo";
            } else {
                estado = "✓ Activo";
            }

            Object[] fila = {
                    p.getPeriodoAcademicoId(),
                    p.getNombrePeriodo(),
                    p.getFechaInicio(),
                    p.getFechaFin(),
                    duracion + " días",
                    estado
            };
            modeloTabla.addRow(fila);
        }
    }

    public void limpiarFormulario() {
        periodoIdSeleccionado = -1;
        txtNombrePeriodo.setText("");
        dateInicio.setDate(null);
        dateFin.setDate(null);
        tablaPeriodos.clearSelection();
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

    private void cargarPeriodoSeleccionado() {
        int fila = tablaPeriodos.getSelectedRow();
        if (fila >= 0) {
            periodoIdSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
            PeriodoAcademico p = controller.obtenerPeriodo(periodoIdSeleccionado);

            if (p != null) {
                txtNombrePeriodo.setText(p.getNombrePeriodo());
                dateInicio.setDate(p.getFechaInicio());
                dateFin.setDate(p.getFechaFin());
            }
        }
    }

    private void guardarPeriodo() {
        if (dateInicio.getDate() == null || dateFin.getDate() == null) {
            mostrarMensaje("Debe seleccionar las fechas", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.crearPeriodo(
                txtNombrePeriodo.getText().trim(),
                new Date(dateInicio.getDate().getTime()),
                new Date(dateFin.getDate().getTime())
        );
    }

    private void actualizarPeriodo() {
        if (dateInicio.getDate() == null || dateFin.getDate() == null) {
            mostrarMensaje("Debe seleccionar las fechas", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.actualizarPeriodo(
                periodoIdSeleccionado,
                txtNombrePeriodo.getText().trim(),
                new Date(dateInicio.getDate().getTime()),
                new Date(dateFin.getDate().getTime())
        );
    }

    private void eliminarPeriodo() {
        controller.eliminarPeriodo(periodoIdSeleccionado);
    }

    private void buscarPeriodo() {
        String criterio = txtBuscar.getText().trim();
        controller.buscarPeriodos(criterio);
    }

    private void mostrarEstadisticas() {
        String estadisticas = controller.obtenerEstadisticas();
        JOptionPane.showMessageDialog(
                this,
                estadisticas,
                "Estadísticas de Periodos",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GestionPeriodos().setVisible(true);
        });
    }
}

