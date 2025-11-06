package views;

import javax.swing.*;
import java.awt.*;


public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema de Control Académico - UniAJC");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior - Header
        JPanel panelHeader = crearHeader();
        add(panelHeader, BorderLayout.NORTH);

        // Panel central - Menú de opciones
        JPanel panelCentral = crearPanelOpciones();
        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior - Footer
        JPanel panelFooter = crearFooter();
        add(panelFooter, BorderLayout.SOUTH);
    }

    private JPanel crearHeader() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 102, 204));
        panel.setPreferredSize(new Dimension(0, 100));
        panel.setLayout(new BorderLayout());

        // Logo y título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setOpaque(false);
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("SISTEMA DE CONTROL ACADÉMICO");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUniversidad = new JLabel("Universidad Antonio José Camacho - UniAJC");
        lblUniversidad.setFont(new Font("Arial", Font.PLAIN, 16));
        lblUniversidad.setForeground(Color.WHITE);
        lblUniversidad.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelTitulo.add(Box.createVerticalStrut(20));
        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(10));
        panelTitulo.add(lblUniversidad);

        panel.add(panelTitulo, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelOpciones() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(240, 240, 240));

        // Módulo Estudiantes
        panel.add(crearBotonModulo("👨‍🎓 ESTUDIANTES",
                "Gestión de estudiantes",
                new Color(52, 152, 219),
                () -> abrirGestionEstudiantes()));

        // Módulo Docentes
        panel.add(crearBotonModulo("👨‍🏫 DOCENTES",
                "Gestión de docentes",
                new Color(46, 204, 113),
                () -> abrirGestionDocentes()));

        // Módulo Cursos
        panel.add(crearBotonModulo("📚 CURSOS",
                "Gestión de cursos",
                new Color(155, 89, 182),
                () -> abrirGestionCursos()));

        // Módulo Calificaciones
        panel.add(crearBotonModulo("📝 CALIFICACIONES",
                "Registro de notas",
                new Color(230, 126, 34),
                () -> abrirGestionCalificaciones()));

        // Módulo Asistencias
        panel.add(crearBotonModulo("✓ ASISTENCIAS",
                "Control de asistencia",
                new Color(231, 76, 60),
                () -> abrirGestionAsistencias()));

        // Módulo Clases
        panel.add(crearBotonModulo("🕐 CLASES",
                "Programación de clases",
                new Color(52, 73, 94),
                () -> abrirGestionClases()));

        // Módulo Reportes
        panel.add(crearBotonModulo("📊 REPORTES",
                "Informes y estadísticas",
                new Color(26, 188, 156),
                () -> abrirReportes()));

        // Módulo Periodos
        panel.add(crearBotonModulo("📅 PERIODOS",
                "Periodos académicos",
                new Color(241, 196, 15),
                () -> abrirGestionPeriodos()));

        // Salir
        panel.add(crearBotonModulo("🚪 SALIR",
                "Cerrar sistema",
                new Color(127, 140, 141),
                () -> salirSistema()));

        return panel;
    }

    private JPanel crearBotonModulo(String titulo, String descripcion,
                                    Color color, Runnable accion) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Título
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        // Descripción
        JLabel lblDescripcion = new JLabel(descripcion, SwingConstants.CENTER);
        lblDescripcion.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDescripcion.setForeground(Color.WHITE);

        panel.add(lblTitulo, BorderLayout.CENTER);
        panel.add(lblDescripcion, BorderLayout.SOUTH);

        // Efecto hover
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(color);
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                accion.run();
            }
        });

        return panel;
    }

    private JPanel crearFooter() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(44, 62, 80));
        panel.setPreferredSize(new Dimension(0, 40));

        JLabel lblFooter = new JLabel("© 2025 UniAJC - Sistema de Control Académico v1.0");
        lblFooter.setForeground(Color.WHITE);
        lblFooter.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(lblFooter);

        return panel;
    }

    // Métodos para abrir cada módulo
    private void abrirGestionEstudiantes() {
        new views.GestionEstudiantes().setVisible(true);
    }

    private void abrirGestionDocentes() {
        new views.GestionDocentes().setVisible(true);
    }

    private void abrirGestionCursos() {
        JOptionPane.showMessageDialog(this,
                "Módulo de Cursos en desarrollo",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void abrirGestionCalificaciones() {
        JOptionPane.showMessageDialog(this,
                "Módulo de Calificaciones en desarrollo",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void abrirGestionAsistencias() {
        JOptionPane.showMessageDialog(this,
                "Módulo de Asistencias en desarrollo",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void abrirGestionClases() {
        JOptionPane.showMessageDialog(this,
                "Módulo de Clases en desarrollo",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void abrirReportes() {
        JOptionPane.showMessageDialog(this,
                "Módulo de Reportes en desarrollo",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void abrirGestionPeriodos() {
        JOptionPane.showMessageDialog(this,
                "Módulo de Periodos en desarrollo",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void salirSistema() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de salir del sistema?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MenuPrincipal().setVisible(true);
        });
    }
}