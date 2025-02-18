/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.cifrado;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author cris_
 */
public class Interfaz extends javax.swing.JFrame {

    private ArrayList<Byte> byteList = new ArrayList<>();
    private int totalBytes = 0;//El total de bytes del archivo abierto
    String ruta;//La ruta del archivo abierto
    String[] columnNames = {"#", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A"};//Titulo de las columnas de ambas tablas
    MostrarArchivo abrir = new MostrarArchivo(); // Se necesita que los metodos sean globales del archivo analizado
    File archivo;//Se necesita el archivo global
    ComportamientoDeTablas inicializarcomportamientoarchivo, inicializarcomportamientocifrado; //Inicializa comportamiento de mostrar bytes del archivo
    MostrarCifrado archivoacifrar = new MostrarCifrado(); //Clase donde todo se puede para cifrar el archivo 

    Tablas t1;
    
    /**
     * Creates new form Interfaz
     */
    public Interfaz() {
        initComponents();
        
        //t1 = new Tablas();
        
        //jPanel3.add(t1);

        inicializarcomportamientoarchivo = new ComportamientoDeTablas(tableASCII,
                tableHex,
                spinnerSeleccion,
                txtAsciiValue,
                txtHexValue,
                columnNames);

        inicializarcomportamientoarchivo.initListeners();
        inicializarcomportamientoarchivo.configurarTablas();  // Configura títulos y bloquea columnas
        inicializarcomportamientoarchivo.sincronizarColores();// Llamar a la función para sincronizar los colores

        inicializarcomportamientocifrado = new ComportamientoDeTablas(tableASCII1,
                tableHex1,
                spinnerSeleccion1,
                txtAsciiValue1,
                txtHexValue1,
                columnNames);

        inicializarcomportamientocifrado.initListeners();
        inicializarcomportamientocifrado.configurarTablas();
        inicializarcomportamientocifrado.sincronizarColores();
    }

    // Método para inicializar la sincronización
    private void initListeners() {
        tableASCII.setCellSelectionEnabled(true);
        tableHex.setCellSelectionEnabled(true);
        // Configurar el Spinner para moverse entre celdas
        spinnerSeleccion.setModel(new SpinnerNumberModel(0, 0, 0, 1));
        spinnerSeleccion.addChangeListener(e -> moverSeleccionDesdeSpinner());
        // Evento de selección en tablas
        sincronizarSeleccionTablas();
    }

    private void moverSeleccionDesdeSpinner() {
        int index = (int) spinnerSeleccion.getValue();

        if (index >= 0 && index < totalBytes) {
            int fila = index / 11;
            int columna = index % 11;

            // Cambiar selección en ambas tablas
            tableASCII.changeSelection(fila, columna, false, false);
            tableHex.changeSelection(fila, columna, false, false);

            // Actualizar los valores en los JTextField
            byte selectedByte = byteList.get(index);
            txtAsciiValue.setText(String.valueOf((char) selectedByte));
            txtHexValue.setText(String.format("%02X", selectedByte));
        }
    }
// Método para sincronizar selección entre tablas

    private void sincronizarSeleccionTablas() {
        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable sourceTable = (JTable) e.getSource();
                int fila = sourceTable.getSelectedRow();
                int columna = sourceTable.getSelectedColumn();

                if (fila != -1 && columna != -1) {
                    int index = fila * 11 + columna;
                    if (index < totalBytes) {
                        // Actualizar el Spinner
                        spinnerSeleccion.setValue(index);

                        // Actualizar los valores en los JTextField
                        byte selectedByte = byteList.get(index - 1);
                        txtAsciiValue.setText(String.valueOf((char) selectedByte));
                        txtHexValue.setText(String.format("%02X", selectedByte));

                        // Sincronizar la selección con la otra tabla
                        JTable targetTable = (sourceTable == tableASCII) ? tableHex : tableASCII;
                        targetTable.changeSelection(fila, columna, false, false);
                    }
                }
            }
        };

        tableASCII.addMouseListener(listener);
        tableHex.addMouseListener(listener);
    }

    private void configurarTablas() {//Metodo para inializar la tabla

        DefaultTableModel modeloASCII = new DefaultTableModel(null, columnNames);//El primer parametro son todos los datos que se van a poner y columnnames los titulos de las columnas
        DefaultTableModel modeloHex = new DefaultTableModel(null, columnNames);

        tableASCII.setModel(modeloASCII);//Actualizar la tabla con el modelo
        tableHex.setModel(modeloHex);//Actualizar la tabla con el modelo

        // Evitar que las columnas se puedan mover
        tableASCII.getTableHeader().setReorderingAllowed(false);
        tableHex.getTableHeader().setReorderingAllowed(false);
    }

    private void sincronizarColores() {
        // Crear un MouseListener para manejar los clics en las celdas
        MouseListener sincronizadorColor = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable sourceTable = (JTable) e.getSource();
                int fila = sourceTable.getSelectedRow();
                int columna = sourceTable.getSelectedColumn();

                if (fila != -1 && columna != -1) {
                    // Obtiene la tabla objetivo (la que no es la tabla fuente)
                    JTable targetTable = (sourceTable == tableASCII) ? tableHex : tableASCII;
                    // Cambiar el color de fondo de la celda seleccionada en la tabla fuente
                    sourceTable.setSelectionBackground(Color.GRAY);
                    // Cambiar el color de fondo de la celda seleccionada en la tabla objetivo
                    targetTable.setSelectionBackground(Color.GRAY);
                    // Hacer que la tabla fuente y la tabla objetivo sincronicen la selección
                    targetTable.changeSelection(fila, columna, false, false);
                }
            }
        };
        // Agregar el MouseListener a ambas tablas
        tableASCII.addMouseListener(sincronizadorColor);
        tableHex.addMouseListener(sincronizadorColor);
    }
    // Método para mover la selección con el JSpinner

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelPrincipal = new javax.swing.JPanel();
        Panel1 = new javax.swing.JPanel();
        jTextFielddireccion = new javax.swing.JTextField();
        jlabelarchivo = new javax.swing.JLabel();
        jButtonAbrir = new javax.swing.JButton();
        jPanelTables = new javax.swing.JPanel();
        scrollPaneASCII = new javax.swing.JScrollPane();
        tableASCII = new javax.swing.JTable();
        scrollPaneHex = new javax.swing.JScrollPane();
        tableHex = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        spinnerSeleccion = new javax.swing.JSpinner();
        txtTotalBytes = new javax.swing.JTextField();
        txtAsciiValue = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtHexValue = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        Panel2 = new javax.swing.JPanel();
        jPanelTables1 = new javax.swing.JPanel();
        scrollPaneASCII1 = new javax.swing.JScrollPane();
        tableASCII1 = new javax.swing.JTable();
        scrollPaneHex1 = new javax.swing.JScrollPane();
        tableHex1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        spinnerSeleccion1 = new javax.swing.JSpinner();
        txtTotalBytes1 = new javax.swing.JTextField();
        txtAsciiValue1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtHexValue1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JTextField();
        btnCifrar = new javax.swing.JButton();
        Guardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CIFRADO Y DESCIFRADO");

        PanelPrincipal.setBackground(new java.awt.Color(204, 0, 102));

        Panel1.setBackground(new java.awt.Color(255, 153, 153));

        jTextFielddireccion.setEditable(false);

        jlabelarchivo.setText("ARCHIVO");

        jButtonAbrir.setText("ABRIR ARCHIVO");
        jButtonAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirActionPerformed(evt);
            }
        });

        jPanelTables.setBackground(new java.awt.Color(255, 255, 204));

        tableASCII.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableASCII.setRowSelectionAllowed(false);
        tableASCII.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPaneASCII.setViewportView(tableASCII);

        tableHex.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableHex.setRowSelectionAllowed(false);
        tableHex.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPaneHex.setViewportView(tableHex);

        javax.swing.GroupLayout jPanelTablesLayout = new javax.swing.GroupLayout(jPanelTables);
        jPanelTables.setLayout(jPanelTablesLayout);
        jPanelTablesLayout.setHorizontalGroup(
            jPanelTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTablesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneASCII, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(scrollPaneHex, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelTablesLayout.setVerticalGroup(
            jPanelTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTablesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPaneHex, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(scrollPaneASCII, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 204));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("posición /");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("ASCII:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("HEX:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spinnerSeleccion, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTotalBytes, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtAsciiValue, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtHexValue, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(spinnerSeleccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHexValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtTotalBytes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtAsciiValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout Panel1Layout = new javax.swing.GroupLayout(Panel1);
        Panel1.setLayout(Panel1Layout);
        Panel1Layout.setHorizontalGroup(
            Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel1Layout.createSequentialGroup()
                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jlabelarchivo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFielddireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 493, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAbrir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(Panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanelTables, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(Panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        Panel1Layout.setVerticalGroup(
            Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlabelarchivo)
                    .addComponent(jTextFielddireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAbrir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTables, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.setBackground(new java.awt.Color(255, 51, 51));
        jTabbedPane1.setForeground(new java.awt.Color(255, 255, 255));

        Panel2.setBackground(new java.awt.Color(255, 153, 153));

        jPanelTables1.setBackground(new java.awt.Color(255, 255, 204));

        tableASCII1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableASCII1.setRowSelectionAllowed(false);
        tableASCII1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPaneASCII1.setViewportView(tableASCII1);

        tableHex1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableHex1.setRowSelectionAllowed(false);
        tableHex1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPaneHex1.setViewportView(tableHex1);

        javax.swing.GroupLayout jPanelTables1Layout = new javax.swing.GroupLayout(jPanelTables1);
        jPanelTables1.setLayout(jPanelTables1Layout);
        jPanelTables1Layout.setHorizontalGroup(
            jPanelTables1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTables1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneASCII1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(scrollPaneHex1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelTables1Layout.setVerticalGroup(
            jPanelTables1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTables1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTables1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPaneHex1, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addComponent(scrollPaneASCII1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 204));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("posición /");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("ASCII:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("HEX:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Contraseña");

        btnCifrar.setText("CIFRAR");
        btnCifrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCifrarActionPerformed(evt);
            }
        });

        Guardar.setText("GUARDAR");
        Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(spinnerSeleccion1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTotalBytes1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtAsciiValue1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHexValue1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(174, 174, 174)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCifrar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Guardar)))
                .addContainerGap(118, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(spinnerSeleccion1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHexValue1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtTotalBytes1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtAsciiValue1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCifrar)
                    .addComponent(Guardar))
                .addContainerGap())
        );

        javax.swing.GroupLayout Panel2Layout = new javax.swing.GroupLayout(Panel2);
        Panel2.setLayout(Panel2Layout);
        Panel2Layout.setHorizontalGroup(
            Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelTables1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        Panel2Layout.setVerticalGroup(
            Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelTables1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("CIFRAR", Panel2);

        javax.swing.GroupLayout PanelPrincipalLayout = new javax.swing.GroupLayout(PanelPrincipal);
        PanelPrincipal.setLayout(PanelPrincipalLayout);
        PanelPrincipalLayout.setHorizontalGroup(
            PanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1)
                    .addComponent(Panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        PanelPrincipalLayout.setVerticalGroup(
            PanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(PanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirActionPerformed

        JFileChooser fileChooser = new JFileChooser(); //Crea un cuadro de diálogo para seleccionar archivos.
        int resultado = fileChooser.showOpenDialog(this);//Muestra el cuadro de diálogo para abrir un archivo.
        if (resultado == JFileChooser.APPROVE_OPTION) {//Verifica si el usuario seleccionó un archivo correctamente
            // Obtener la ruta del archivo seleccionado
            archivo = fileChooser.getSelectedFile();
            ruta = archivo.getAbsolutePath();
            jTextFielddireccion.setText(ruta);//Pone la direccion del archivo en el JtextField
            abrir.procesarArchivo(archivo);//Procesa el archivo para sacar los bytes y ponerlos en arreglos bidimensionales para ascii y hexadecimal

    }//GEN-LAST:event_jButtonAbrirActionPerformed
        byteList = abrir.getByteList(); //Obtiene la lista de bytes que fueron tomados del archivo
        totalBytes = abrir.getTotalBytes();

        inicializarcomportamientoarchivo.setByteList(byteList); //Se ocupa para poner para que el comportamiento de ver bytes de archivo funcione
        inicializarcomportamientoarchivo.setTotalBytes(totalBytes); //Se ocupa para poner para que el comportamiento de ver bytes de archivo funcione

        inicializartablas(txtTotalBytes, spinnerSeleccion, tableASCII, tableHex, abrir);//Inicializa la tabla para mostrar los archivos
    }

    public void inicializartablas(JTextField txtTotalBytes, JSpinner spinnerSeleccion, JTable tableASCII, JTable tableHex, MostrarArchivo abrir) {//Metodo para inicializar las tablas ya sea con los bytes del archivo o con los bytes de cifrado o descifrado 

        txtTotalBytes.setText(String.valueOf(abrir.getTotalBytes()));
        // Ajustar el Spinner al rango correcto
        System.out.println("Total de bytes: " + abrir.getTotalBytes());
        spinnerSeleccion.setModel(new SpinnerNumberModel(0, 0, abrir.getTotalBytes() - 1, 1));

        tableASCII.setModel(new DefaultTableModel(abrir.getAsciiData(), columnNames));//Esto actualiza la tabla con nuevos datos.
        tableHex.setModel(new DefaultTableModel(abrir.getHexData(), columnNames));
    }
    private void btnCifrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCifrarActionPerformed

        MostrarArchivo archivonormal = new MostrarArchivo();

        try {
            // Se necesita para que procese el archivo y funcione los metodos
//           archivonormal.procesarArchivo(archivo);
            //Se necesita darle la lista de bytes tomados del archivo y el total de bytes
            archivonormal.setByteList(byteList);
            archivonormal.setTotalBytes(totalBytes);

            //Verifica que la contraseña no este vacia
            //Lee el archivo y saca los bytes de la contraseña
            //cifra con los 9 pasos 
            archivoacifrar.cifrarArchivo(ruta, txtPassword.getText(), this); //Cifra el archivo con los bytes que contiene y la contraseña
            //Devuelve la lista de bytes ya cifrados y se los da a mostrarDatosEnTablas
            //para que pueda acomodarlos en los arreglos bidimensionales que iran en las tablas correspondientes de cifrado
            archivonormal.mostrarDatosEnTablas(archivoacifrar.getEncryptedBytes());
            //Le doy el arreglo de bytes ya cifrados tomados en el archivo y la longitud
            inicializarcomportamientocifrado.setByteList(archivoacifrar.getEncryptedBytes());
            inicializarcomportamientocifrado.setTotalBytes(totalBytes);
            //Le doy el objeto que ya ha sido inicializado y ya se hayan obtenido los arreglos bidimensionales
            //para que use los getters dentro de inicializar las tablas y pueda obtener los arreglos bidimensionales de los 
            //bytes cifrados y puedan mostrarse en tablas
            inicializartablas(txtTotalBytes1, spinnerSeleccion1, tableASCII1, tableHex1, archivonormal);
        } catch (IOException ex) {
            Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnCifrarActionPerformed

    private void GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GuardarActionPerformed
        // TODO add your handling code here:
        inicializarcomportamientocifrado.guardarArchivo(this, archivoacifrar.getEncryptedBytes());
    }//GEN-LAST:event_GuardarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Interfaz().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Guardar;
    private javax.swing.JPanel Panel1;
    private javax.swing.JPanel Panel2;
    private javax.swing.JPanel PanelPrincipal;
    private javax.swing.JButton btnCifrar;
    private javax.swing.JButton jButtonAbrir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelTables;
    private javax.swing.JPanel jPanelTables1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextFielddireccion;
    private javax.swing.JLabel jlabelarchivo;
    private javax.swing.JScrollPane scrollPaneASCII;
    private javax.swing.JScrollPane scrollPaneASCII1;
    private javax.swing.JScrollPane scrollPaneHex;
    private javax.swing.JScrollPane scrollPaneHex1;
    private javax.swing.JSpinner spinnerSeleccion;
    private javax.swing.JSpinner spinnerSeleccion1;
    private javax.swing.JTable tableASCII;
    private javax.swing.JTable tableASCII1;
    private javax.swing.JTable tableHex;
    private javax.swing.JTable tableHex1;
    private javax.swing.JTextField txtAsciiValue;
    private javax.swing.JTextField txtAsciiValue1;
    private javax.swing.JTextField txtHexValue;
    private javax.swing.JTextField txtHexValue1;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtTotalBytes;
    private javax.swing.JTextField txtTotalBytes1;
    // End of variables declaration//GEN-END:variables
}
