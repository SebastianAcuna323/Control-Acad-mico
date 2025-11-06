package views;

import dao.EstudianteDao;
import model.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionEstudiantes extends JFrame {

    private EstudianteDao estudianteDAO;

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
    private JTextField txtBuscar;

    private int estudianteIdSeleccionado = -1;

    public GestionEstudiantes() {
        estudianteDAO = new EstudianteDao();
        initComponents();
        cargarEstudiantes();
    }

    private void initComponents() {
        setTitle("Gestión de Estudiantes - UniAJC");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(0, 102, 204));
        JLabel lblTitulo = new JLabel("GESTIÓN DE ESTUDIANTES");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel central - dividido en dos
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel izquierdo - Formulario
        JPanel panelFormulario = crearPanelFormulario();
        panelCentral.add(panelFormulario, BorderLayout.WEST);

        // Panel derecho - Tabla
        JPanel panelTabla = crearPanelTabla();
        panelCentral.add(panelTabla, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Estudiante"));
        panel.setPreferredSize(new Dimension(400, 0));

        // Identificación
        panel.add(crearCampo("Identificación:", txtIdentificacion = new JTextField(20)));

        // Tipo de Documento
        cboTipoDocumento = new JComboBox<>(new String[]{"CC", "TI", "CE", "PA"});
        panel.add(crearCampo("Tipo Documento:", cboTipoDocumento));

        // Nombre
        panel.add(crearCampo("Nombre Completo:", txtNombre = new JTextField(20)));

        // Género
        cboGenero = new JComboBox<>(new String[]{"M", "F", "Otro"});
        panel.add(crearCampo("Género:", cboGenero));

        // Correo Institucional
        panel.add(crearCampo("Correo Institucional:",
                txtCorreoInstitucional = new JTextField(20)));

        // Correo Personal
        panel.add(crearCampo("Correo Personal:",
                txtCorreoPersonal = new JTextField(20)));

        // Teléfono
        panel.add(crearCampo("Teléfono:", txtTelefono = new JTextField(20)));

        // Es Vocero
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

        // Botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 5, 5));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarEstudiante());
        btnActualizar.addActionListener(e -> actualizarEstudiante());
        btnEliminar.addActionListener(e -> eliminarEstudiante());

        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);

        panel.add(panelBotones);

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
        btnRefrescar.addActionListener(e -> cargarEstudiantes());
        panelBusqueda.add(btnRefrescar);

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

    private void cargarEstudiantes() {
        modeloTabla.setRowCount(0);
        List<Estudiante> estudiantes = estudianteDAO.listarTodos();

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

    private void cargarEstudianteSeleccionado() {
        int fila = tablaEstudiantes.getSelectedRow();
        if (fila >= 0) {
            estudianteIdSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
            Estudiante e = estudianteDAO.obtenerPorId(estudianteIdSeleccionado);

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
        if (!validarCampos()) return;

        Estudiante estudiante = new Estudiante(
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

        if (estudianteDAO.crear(estudiante)) {
            JOptionPane.showMessageDialog(this,
                    "Estudiante guardado exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarEstudiantes();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar estudiante",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEstudiante() {
        if (estudianteIdSeleccionado < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un estudiante de la tabla",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validarCampos()) return;

        Estudiante estudiante = new Estudiante(
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

        if (estudianteDAO.actualizar(estudiante)) {
            JOptionPane.showMessageDialog(this,
                    "Estudiante actualizado exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarEstudiantes();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar estudiante",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEstudiante() {
        if (estudianteIdSeleccionado < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un estudiante de la tabla",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este estudiante?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (estudianteDAO.eliminar(estudianteIdSeleccionado)) {
                JOptionPane.showMessageDialog(this,
                        "Estudiante eliminado exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarEstudiantes();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar estudiante",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarEstudiante() {
        String busqueda = txtBuscar.getText().trim();
        if (busqueda.isEmpty()) {
            cargarEstudiantes();
            return;
        }

        modeloTabla.setRowCount(0);
        List<Estudiante> estudiantes = estudianteDAO.buscarPorNombre(busqueda);

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

    private void limpiarFormulario() {
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

    private boolean validarCampos() {
        if (txtIdentificacion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La identificación es obligatoria",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre es obligatorio",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtCorreoInstitucional.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El correo institucional es obligatorio",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GestionEstudiantes().setVisible(true);
        });
    }
}
