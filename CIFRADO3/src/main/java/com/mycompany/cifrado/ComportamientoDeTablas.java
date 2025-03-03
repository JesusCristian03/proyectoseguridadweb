/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cifrado;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author cris_
 */
public class ComportamientoDeTablas {

    int totalBytes = 0;//El total de bytes del archivo abierto
    ArrayList<Byte> byteList = new ArrayList<>();

    JTable tableASCII, tableHex;
    JSpinner spinnerSeleccion;
    JTextField txtAsciiValue, txtHexValue;
    String[] columnNames;
    String[][] asciiData, hexData;//para manejar los datos dentro de la tabla
    int fila, columna; //Fila y columna seleccionadas en ese momento

    public String[][] getAsciiData() {
        return asciiData;
    }

    public void setAsciiData(String[][] asciiData) {
        this.asciiData = asciiData;
    }

    public String[][] getHexData() {
        return hexData;
    }

    public void setHexData(String[][] hexData) {
        this.hexData = hexData;
    }

    public ComportamientoDeTablas(JTable tableASCII, JTable tableHex, JSpinner spinnerSeleccion, JTextField txtAsciiValue, JTextField txtHexValue, String[] columnNames) {
        this.tableASCII = tableASCII;
        this.tableHex = tableHex;
        this.spinnerSeleccion = spinnerSeleccion;
        this.txtAsciiValue = txtAsciiValue;
        this.txtHexValue = txtHexValue;
        this.columnNames = columnNames;
    }

    public int getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(int totalBytes) {
        this.totalBytes = totalBytes;
    }

    public ArrayList<Byte> getByteList() {
        return byteList;
    }

    public void setByteList(ArrayList<Byte> byteList) {
        this.byteList = byteList;
    }

    public JTable getTableASCII() {
        return tableASCII;
    }

    public void setTableASCII(JTable tableASCII) {
        this.tableASCII = tableASCII;
    }

    public JTable getTableHex() {
        return tableHex;
    }

    public void setTableHex(JTable tableHex) {
        this.tableHex = tableHex;
    }

    public JSpinner getSpinnerSeleccion() {
        return spinnerSeleccion;
    }

    public void setSpinnerSeleccion(JSpinner spinnerSeleccion) {
        this.spinnerSeleccion = spinnerSeleccion;
    }

    public JTextField getTxtAsciiValue() {
        return txtAsciiValue;
    }

    public void setTxtAsciiValue(JTextField txtAsciiValue) {
        this.txtAsciiValue = txtAsciiValue;
    }

    public JTextField getTxtHexValue() {
        return txtHexValue;
    }

    public void setTxtHexValue(JTextField txtHexValue) {
        this.txtHexValue = txtHexValue;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    // Método para inicializar la sincronización
    public void initListeners() {
        tableASCII.setCellSelectionEnabled(true);
        tableHex.setCellSelectionEnabled(true);
        // Configurar el Spinner para moverse entre celdas
        spinnerSeleccion.setModel(new SpinnerNumberModel(0, 0, 0, 1));
        spinnerSeleccion.addChangeListener(e -> moverSeleccionDesdeSpinner());
        // Evento de selección en tablas
        sincronizarSeleccionTablas();
    }

    public void moverSeleccionDesdeSpinner() {
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

    public void sincronizarSeleccionTablas() {
        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable sourceTable = (JTable) e.getSource();
                fila = sourceTable.getSelectedRow();
                columna = sourceTable.getSelectedColumn();

                if (fila != -1 && columna != -1) {
                    int index = fila * 11 + columna;
                    if (index < totalBytes) {
                        // Actualizar el Spinner
                        spinnerSeleccion.setValue(index);
                        // Cambiar el valor en la fila 1, columna 2 (índices empiezan en 0)

                        // Actualizar los valores en los JTextField
                        byte selectedByte = byteList.get(index);
                        txtAsciiValue.setText(String.valueOf((char) selectedByte));
                        txtHexValue.setText(String.format("%02X", selectedByte));
                        System.out.println("LLEGUE: ");
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

    public void editarTablas(JTextField asciivalue, JTextField hexvalue) {
        if (fila != -1 && columna != -1) {
            int index = fila * 11 + columna;
            if (index < totalBytes) {
                // Actualizar el Spinner
                //spinnerSeleccion.setValue(index);
                byte selectedByte = byteList.get(index);
                String selectedByteHex = String.format("%02X", selectedByte);
                if (selectedByte != (byte) asciivalue.getText().charAt(0)) {
                    String x = String.format("%02X", (byte) asciivalue.getText().charAt(0));//Caracter de la caja de text en hexadecimal
                    tableASCII.setValueAt(asciivalue.getText(), fila, columna);
                    tableHex.setValueAt(x, fila, columna);
                    hexvalue.setText(x);
                    asciiData[fila][columna] = asciivalue.getText();//Poner los datos modificados en el arreglo bidimensional que se pone en el JTabler
                    hexData[fila][columna] = x;
                } else if (selectedByteHex == null ? hexvalue.getText() != null : !selectedByteHex.equals(hexvalue.getText())) {
                    String hex = hexvalue.getText(); // Representa 'A' en ASCII (65 en decimal)
                    int decimal = Integer.parseInt(hex, 16); // Convierte de hex a decimal
                    char asciiChar = (char) decimal; // Convierte de int a char
                    tableHex.setValueAt(hex, fila, columna);//Poner el dato en la tabla en la celda en especificai
                    tableASCII.setValueAt(asciiChar, fila, columna);//Poner el dato en la tabla en la celda en especificai
                    asciivalue.setText("" + asciiChar);
                    asciiData[fila][columna] = ""+asciiChar;//Poner los datos modificados en el arreglo bidimensional que se pone en el JTabler
                    hexData[fila][columna] = hex;
                }
                // Actualizar los valores en los JTextField
                // txtAsciiValue.setText(tableASCII.get);
                // txtHexValue.setText(String.format("%02X", selectedByte));
            }
        }

    }

    public void configurarTablas() {//Metodo para inializar la tabla

        DefaultTableModel modeloASCII = new DefaultTableModel(null, columnNames);//El primer parametro son todos los datos que se van a poner y columnnames los titulos de las columnas
        DefaultTableModel modeloHex = new DefaultTableModel(null, columnNames);

        tableASCII.setModel(modeloASCII);//Actualizar la tabla con el modelo
        tableHex.setModel(modeloHex);//Actualizar la tabla con el modelo

        // Evitar que las columnas se puedan mover
        tableASCII.getTableHeader().setReorderingAllowed(false);
        tableHex.getTableHeader().setReorderingAllowed(false);
    }

    public void sincronizarColores() {
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

    public void guardarArchivo(JFrame ventanaprincipal, ArrayList<Byte> bytesCifrados) {
        JFileChooser fileChooser = new JFileChooser();// Abre una ventana para elegir dónde guardar el archivo.
        fileChooser.setDialogTitle("Guardar Archivo Cifrado");//Titulo del explorador de archivos

        int userSelection = fileChooser.showSaveDialog(ventanaprincipal);//Si selecciono un archivo a guardar

        if (userSelection == JFileChooser.APPROVE_OPTION) {//Si fue correcto o canclo la operacion
            File fileToSave = fileChooser.getSelectedFile();//

            try ( FileOutputStream fos = new FileOutputStream(fileToSave)) {
                // Suponiendo que tienes los bytes cifrados en un ArrayList<Byte>

                // Convertir ArrayList<Byte> a byte[]
                byte[] byteArray = new byte[bytesCifrados.size()];
                for (int i = 0; i < bytesCifrados.size(); i++) {
                    byteArray[i] = bytesCifrados.get(i);
                }

                fos.write(byteArray);
                fos.flush();
                JOptionPane.showMessageDialog(ventanaprincipal, "Archivo guardado con éxito");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(ventanaprincipal, "Error al guardar el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
