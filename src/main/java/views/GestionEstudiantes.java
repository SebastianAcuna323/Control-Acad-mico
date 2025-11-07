package views;

import controllers.EstudianteController;
import model.Estudiante;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionEstudiantes extends JFrame {

    private EstudianteController controller;

    // Componentes de la interfaz
    private JTable tablaEstudiantes;
    private DefaultTableModel modeloTabla;
    private JTextField txtIdentificacion;
    private JTextField txtNombre;
    private JTextField txtCorreoInstitucional;
    private JTextField txtCorreoPersonal;
    private JTextField txtTelefono;
    private JComboBox<String> cboTipoDocumento;
    private JComboBox<String> cboGenero;
    private JCheckBox chkEsVocero;
    private JTextArea txtComentarios;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnBuscar;
    private JButton btnEstadisticas;
    private JTextField txtBuscar;

    private int estudianteIdSeleccionado = -1;

    public GestionEstudiantes() {
        this.controller = new EstudianteController(this);
        initComponents();
        controller.cargarEstudiantes();
    }

    private void initComponents() {
        setTitle("Gestión de Estudiantes - UniAJC");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel superior con título
        add(crearPanelTitulo(), BorderLayout.NORTH);

        // Panel central - dividido en dos
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelCentral.add(crearPanelFormulario(), BorderLayout.WEST);
        panelCentral.add(crearPanelTabla(), BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 102, 204));
        JLabel lblTitulo = new JLabel("GESTIÓN DE ESTUDIANTES");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo);
        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Estudiante"));
        panel.setPreferredSize(new Dimension(400, 0));

        // Campos del formulario
        panel.add(crearCampo("Identificación:", txtIdentificacion = new JTextField(20)));

        cboTipoDocumento = new JComboBox<>(new String[]{"CC", "TI", "CE", "PA"});
        panel.add(crearCampo("Tipo Documento:", cboTipoDocumento));

        panel.add(crearCampo("Nombre Completo:", txtNombre = new JTextField(20)));

        cboGenero = new JComboBox<>(new String[]{"M", "F", "Otro"});
        panel.add(crearCampo("Género:", cboGenero));

        panel.add(crearCampo("Correo Institucional:",
                txtCorreoInstitucional = new JTextField(20)));

        panel.add(crearCampo("Correo Personal:",
                txtCorreoPersonal = new JTextField(20)));

        panel.add(crearCampo("Teléfono:", txtTelefono = new JTextField(20)));

        // Checkbox vocero
        JPanel panelVocero = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chkEsVocero = new JCheckBox("Es Vocero del Grupo");
        panelVocero.add(chkEsVocero);
        panel.add(panelVocero);

        // Comentarios
        JPanel panelComentarios = new JPanel(new BorderLayout());
        panelComentarios.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelComentarios.add(new JLabel("Comentarios:"), BorderLayout.NORTH);
        txtComentarios = new JTextArea(4, 20);
        txtComentarios.setLineWrap(true);
        JScrollPane scrollComentarios = new JScrollPane(txtComentarios);
        panelComentarios.add(scrollComentarios, BorderLayout.CENTER);
        panel.add(panelComentarios);

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
        btnGuardar.addActionListener(e -> guardarEstudiante());
        btnActualizar.addActionListener(e -> actualizarEstudiante());
        btnEliminar.addActionListener(e -> eliminarEstudiante());

        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Estudiantes"));

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarEstudiante());
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> controller.cargarEstudiantes());
        panelBusqueda.add(btnRefrescar);

        btnEstadisticas = new JButton("📊 Estadísticas");
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas());
        panelBusqueda.add(btnEstadisticas);

        panel.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Identificación", "Nombre", "Correo",
                "Teléfono", "Vocero", "Género"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaEstudiantes = new JTable(modeloTabla);
        tablaEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEstudiantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarEstudianteSeleccionado();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaEstudiantes);
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

    // --------------------- MÉTODOS PÚBLICOS LLAMADOS POR EL CONTROLADOR ----------------------


     //Actualizar la tabla con la lista de estudiantes

    public void actualizarTabla(List<Estudiante> estudiantes) {
        modeloTabla.setRowCount(0);

        for (Estudiante e : estudiantes) {
            Object[] fila = {
                    e.getEstudianteId(),
                    e.getIdentificacion(),
                    e.getNombre(),
                    e.getCorreoInstitucional(),
                    e.getTelefono(),
                    e.isEsVocero() ? "Sí" : "No",
                    e.getGenero()
            };
            modeloTabla.addRow(fila);
        }
    }

    public void limpiarFormulario() {
        estudianteIdSeleccionado = -1;
        txtIdentificacion.setText("");
        txtNombre.setText("");
        txtCorreoInstitucional.setText("");
        txtCorreoPersonal.setText("");
        txtTelefono.setText("");
        txtComentarios.setText("");
        chkEsVocero.setSelected(false);
        cboTipoDocumento.setSelectedIndex(0);
        cboGenero.setSelectedIndex(0);
        tablaEstudiantes.clearSelection();
    }


    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }

    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(
                this, mensaje, titulo, JOptionPane.YES_NO_OPTION
        );
    }

    // --------------------MÉTODOS PRIVADOS DE EVENTOS --------------------------

    private void cargarEstudianteSeleccionado() {
        int fila = tablaEstudiantes.getSelectedRow();
        if (fila >= 0) {
            estudianteIdSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
            Estudiante e = controller.obtenerEstudiante(estudianteIdSeleccionado);

            if (e != null) {
                txtIdentificacion.setText(e.getIdentificacion());
                txtNombre.setText(e.getNombre());
                txtCorreoInstitucional.setText(e.getCorreoInstitucional());
                txtCorreoPersonal.setText(e.getCorreoPersonal());
                txtTelefono.setText(e.getTelefono());
                cboTipoDocumento.setSelectedItem(e.getTipoDocumento());
                cboGenero.setSelectedItem(e.getGenero());
                chkEsVocero.setSelected(e.isEsVocero());
                txtComentarios.setText(e.getComentarios());
            }
        }
    }

    private void guardarEstudiante() {
        controller.crearEstudiante(
                txtIdentificacion.getText().trim(),
                txtNombre.getText().trim(),
                txtCorreoInstitucional.getText().trim(),
                txtCorreoPersonal.getText().trim(),
                txtTelefono.getText().trim(),
                chkEsVocero.isSelected(),
                cboTipoDocumento.getSelectedItem().toString(),
                cboGenero.getSelectedItem().toString(),
                txtComentarios.getText().trim()
        );
    }

    private void actualizarEstudiante() {
        controller.actualizarEstudiante(
                estudianteIdSeleccionado,
                txtIdentificacion.getText().trim(),
                txtNombre.getText().trim(),
                txtCorreoInstitucional.getText().trim(),
                txtCorreoPersonal.getText().trim(),
                txtTelefono.getText().trim(),
                chkEsVocero.isSelected(),
                cboTipoDocumento.getSelectedItem().toString(),
                cboGenero.getSelectedItem().toString(),
                txtComentarios.getText().trim()
        );
    }

    private void eliminarEstudiante() {
        controller.eliminarEstudiante(estudianteIdSeleccionado);
    }

    private void buscarEstudiante() {
        String criterio = txtBuscar.getText().trim();
        controller.buscarEstudiantes(criterio);
    }

    private void mostrarEstadisticas() {
        String estadisticas = controller.obtenerEstadisticas();
        JOptionPane.showMessageDialog(
                this,
                estadisticas,
                "Estadísticas de Estudiantes",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GestionEstudiantes().setVisible(true);
        });
    }
}