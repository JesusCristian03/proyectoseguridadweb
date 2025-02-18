/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cifrado;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author cris_
 */
public class MostrarArchivo {

    private int totalBytes = 0;
    String[][] asciiData, hexData;
    ArrayList<Byte> byteList = new ArrayList<>();

    public ArrayList<Byte> getByteList() {
        return byteList;
    }

    public void setByteList(ArrayList<Byte> byteList) {
        this.byteList = byteList;
    }

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

    public int getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(int totalBytes) {
        this.totalBytes = totalBytes;
    }

    public void procesarArchivo(File archivo) {//Procesa para poner los caracteres en la tabla principal
        try ( BufferedInputStream bis = new BufferedInputStream(new FileInputStream(archivo))) {

            int byteValue;
            byteList.clear(); // Limpiar lista antes de cargar nuevo archivo
            while ((byteValue = bis.read()//Lee el siguiente byte del archivo y lo almacena en byteValue.
                    ) != -1) {//Devuelve -1 cuando ya no hay más datos es decir la última linea.
                byteList.add((byte) byteValue);//Convierte el valor leído a tipo byte y lo guarda en la lista byteList.
            }

            totalBytes = byteList.size();//El tamaño total de la lista de bytes que hay en el txt

            mostrarDatosEnTablas(byteList);
        } catch (IOException e) {
            //JOptionPane.showMessageDialog(this, "Error al leer el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("\"Error al leer el archivo\"");
        }
    }

    public void mostrarDatosEnTablas(ArrayList<Byte> byteList) {//Este metodo para guardar en una tabla 
        System.out.println("Tamaño de mi lista nueva"+byteList.size());
        int filas = (int) Math.ceil(totalBytes / 11.0);//Se divide entre 11 para saber cuantas filas hay en total siendo el 11 el numero de columnas que hay que mostrar
        System.out.println("filas :-> "+filas);
        asciiData = new String[filas][12];//Crea un arreglo bidemensional que representan todos los caracteres toda la tabla ASCII
        hexData = new String[filas][12];//Crea un arreglo bidimensional que representa toda la tabla de Hexadecimal

        for (int i = 0; i < filas; i++) {//Se recorre primero las filas 
            asciiData[i][0] = String.valueOf((i) * 10);//Acomoda los titulos de las filas (10,20,30..)
            //numeros de manera vertical y luego lo pasa a modo string
            hexData[i][0] = String.valueOf((i) * 10);

            for (int j = 0; j < 11; j++) {//Recorre las columnas
                int index = i * 11 + j;// Significa que estamos accediendo al byte en la posición index(x) del archivo.
                System.out.print("{"+index+"}"+"("+j+")");
                if (j == totalBytes) {

                } else if (index < totalBytes) {//Aquí se verifica si el índice calculado es menor que el total de bytes en el archivo.
                    byte b = byteList.get(index);//Obtiene el caracter de la lista
                    asciiData[i][j + 1] = Character.toString((char) b);//Pasa el caracter que esta representado en byte a String
                    hexData[i][j + 1] = String.format("%02X", b);//Pasa el caracter que esta representado en byte a Hexadecimal
                } else {
                    asciiData[i][j + 1] = "";// Si no encuentra caracter vacío pone un espacio vacío
                    hexData[i][j + 1] = "";
                }
            }
            System.out.println("");
        }
         for (int i = 0; i < asciiData.length; i++) { // Recorre filas
            for (int j = 0; j < asciiData[i].length; j++) { // Recorre columnas
                System.out.print("["+asciiData[i][j] + "]");
            }
            System.out.println(); // Salto de línea por fila
        }

    }
    
}
