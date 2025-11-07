package views;

import controllers.DocenteController;
import model.Docente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionDocentes extends JFrame {

    private DocenteController controller;

    // Componentes de la interfaz
    private JTable tablaDocentes;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombreDocente;
    private JTextField txtIdentificacion;
    private JComboBox<String> cboTipoIdentificacion;
    private JComboBox<String> cboGenero;
    private JTextField txtCorreo;
    private JTextField txtTituloEstudios;
    private JTextField txtIdiomas;
    private JTextArea txtCertificaciones;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnBuscar;
    private JButton btnEstadisticas;
    private JTextField txtBuscar;

    private int docenteIdSeleccionado = -1;

    public GestionDocentes() {
        this.controller = new DocenteController(this);
        initComponents();
        controller.cargarDocentes();
    }

    private void initComponents() {
        setTitle("Gestión de Docentes - UniAJC");
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
        panel.setBackground(new Color(46, 204, 113));
        JLabel lblTitulo = new JLabel("👨‍🏫 GESTIÓN DE DOCENTES");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo);
        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Docente"));
        panel.setPreferredSize(new Dimension(400, 0));

        // Nombre del Docente
        panel.add(crearCampo("Nombre Completo:",
                txtNombreDocente = new JTextField(20)));

        // Identificación
        panel.add(crearCampo("Identificación:",
                txtIdentificacion = new JTextField(20)));

        // Tipo de Identificación
        cboTipoIdentificacion = new JComboBox<>(new String[]{"CC", "CE", "PA", "TI"});
        panel.add(crearCampo("Tipo Documento:", cboTipoIdentificacion));

        // Género
        cboGenero = new JComboBox<>(new String[]{"M", "F", "Otro"});
        panel.add(crearCampo("Género:", cboGenero));

        // Correo
        panel.add(crearCampo("Correo Electrónico:",
                txtCorreo = new JTextField(20)));

        // Título de Estudios
        panel.add(crearCampo("Título de Estudios:",
                txtTituloEstudios = new JTextField(20)));

        // Idiomas
        panel.add(crearCampo("Idiomas:",
                txtIdiomas = new JTextField(20)));

        // Certificaciones
        JPanel panelCertificaciones = new JPanel(new BorderLayout());
        panelCertificaciones.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelCertificaciones.add(new JLabel("Certificaciones:"), BorderLayout.NORTH);
        txtCertificaciones = new JTextArea(4, 20);
        txtCertificaciones.setLineWrap(true);
        JScrollPane scrollCert = new JScrollPane(txtCertificaciones);
        panelCertificaciones.add(scrollCert, BorderLayout.CENTER);
        panel.add(panelCertificaciones);

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
        btnGuardar.addActionListener(e -> guardarDocente());
        btnActualizar.addActionListener(e -> actualizarDocente());
        btnEliminar.addActionListener(e -> eliminarDocente());

        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Docentes"));

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarDocente());
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> controller.cargarDocentes());
        panelBusqueda.add(btnRefrescar);

        btnEstadisticas = new JButton("📊 Estadísticas");
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas());
        panelBusqueda.add(btnEstadisticas);

        panel.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Identificación", "Correo",
                "Título", "Idiomas", "Género"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaDocentes = new JTable(modeloTabla);
        tablaDocentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDocentes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarDocenteSeleccionado();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaDocentes);
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

    // ------------------------- MÉTODOS PÚBLICOS ------------------------------

    public void actualizarTabla(List<Docente> docentes) {
        modeloTabla.setRowCount(0);

        for (Docente d : docentes) {
            Object[] fila = {
                    d.getDocenteId(),
                    d.getNombreDocente(),
                    d.getIdentificacion(),
                    d.getCorreo(),
                    d.getTituloEstudios(),
                    d.getIdiomas(),
                    d.getGenero()
            };
            modeloTabla.addRow(fila);
        }
    }

    public void limpiarFormulario() {
        docenteIdSeleccionado = -1;
        txtNombreDocente.setText("");
        txtIdentificacion.setText("");
        txtCorreo.setText("");
        txtTituloEstudios.setText("");
        txtIdiomas.setText("");
        txtCertificaciones.setText("");
        cboTipoIdentificacion.setSelectedIndex(0);
        cboGenero.setSelectedIndex(0);
        tablaDocentes.clearSelection();
    }

    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }

    public int confirmarAccion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(
                this, mensaje, titulo, JOptionPane.YES_NO_OPTION
        );
    }

    // ---------------------------- MÉTODOS PRIVADOS -----------------------

    private void cargarDocenteSeleccionado() {
        int fila = tablaDocentes.getSelectedRow();
        if (fila >= 0) {
            docenteIdSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
            Docente d = controller.obtenerDocente(docenteIdSeleccionado);

            if (d != null) {
                txtNombreDocente.setText(d.getNombreDocente());
                txtIdentificacion.setText(d.getIdentificacion());
                cboTipoIdentificacion.setSelectedItem(d.getTipoIdentificacion());
                cboGenero.setSelectedItem(d.getGenero());
                txtCorreo.setText(d.getCorreo());
                txtTituloEstudios.setText(d.getTituloEstudios());
                txtIdiomas.setText(d.getIdiomas());
                txtCertificaciones.setText(d.getCertificaciones());
            }
        }
    }

    private void guardarDocente() {
        controller.crearDocente(
                txtNombreDocente.getText().trim(),
                txtIdentificacion.getText().trim(),
                cboTipoIdentificacion.getSelectedItem().toString(),
                cboGenero.getSelectedItem().toString(),
                txtCorreo.getText().trim(),
                txtTituloEstudios.getText().trim(),
                txtIdiomas.getText().trim(),
                txtCertificaciones.getText().trim()
        );
    }

    private void actualizarDocente() {
        controller.actualizarDocente(
                docenteIdSeleccionado,
                txtNombreDocente.getText().trim(),
                txtIdentificacion.getText().trim(),
                cboTipoIdentificacion.getSelectedItem().toString(),
                cboGenero.getSelectedItem().toString(),
                txtCorreo.getText().trim(),
                txtTituloEstudios.getText().trim(),
                txtIdiomas.getText().trim(),
                txtCertificaciones.getText().trim()
        );
    }

    private void eliminarDocente() {
        controller.eliminarDocente(docenteIdSeleccionado);
    }

    private void buscarDocente() {
        String criterio = txtBuscar.getText().trim();
        controller.buscarDocentes(criterio);
    }

    private void mostrarEstadisticas() {
        String estadisticas = controller.obtenerEstadisticas();
        JOptionPane.showMessageDialog(
                this,
                estadisticas,
                "Estadísticas de Docentes",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GestionDocentes().setVisible(true);
        });
    }
}
