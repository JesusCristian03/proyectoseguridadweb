/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cifrado;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author cris_
 */
public class MostrarArchivo {

    private int totalBytes = 0;
    String[][] asciiData, hexData;
    ArrayList<Byte> byteList = new ArrayList<>(); //Lista sin ninguna combinación y caracteres del archivo
    ArrayList<Integer> listatitulos = new ArrayList<>();//Lista con los titulos de fila y caracteres del archivo
    int convertirNumaChar = 0;//Para ver si el metodo MostrarDatosTabla convierte los int del hash a char
    char[] rangoSalida = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();//Rango de bytes aceptados
    Set<Integer> indicesInsertados = new HashSet<>(); // Guarda índices de los valores agregados manualmente
    int x = 0; //Guardar el tamaño de la lista para darselo al spinner    

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getConvertirNumaChar() {
        return convertirNumaChar;
    }

    public void setConvertirNumaChar(int convertirNumaChar) {
        this.convertirNumaChar = convertirNumaChar;
    }

    public ArrayList<Integer> getListatitulos() {
        return listatitulos;
    }

    public void setListatitulos(ArrayList<Integer> listatitulos) {
        this.listatitulos = listatitulos;
    }

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

    public void procesarArchivo(File archivo) {
        try {
            // Leer todo el archivo en un solo String usando una codificación adecuada (UTF-8 o la que corresponda)
            byte[] bytes = Files.readAllBytes(archivo.toPath());

            // Convertir el byte array a String con la codificación UTF-8 (puedes cambiar a ISO-8859-1 o la que sea adecuada para tu archivo)
            // Convertir a String usando ISO-8859-1 para mantener caracteres acentuados en un solo byte
            String contenido = new String(bytes, Charset.forName("ISO-8859-1"));

            // Ahora puedes procesar el contenido del archivo correctamente
            System.out.println("Contenido del archivo: ");
            System.out.println(contenido);

            // Si necesitas seguir procesando en bytes, puedes continuar
            byteList.clear();
            for (byte b : bytes) {
                byteList.add(b);
            }
                char c = (char) (160 & 0xFF);
            System.out.println("Valor de que: "+c);
            for (int i = 0; i < byteList.size(); i++) {
                System.out.print("{" + byteList.get(i) + "}");

            }
        
            totalBytes = byteList.size();
            convertirAFormatoTabla(byteList, 11);
        } catch (IOException e) {
            System.out.println("Error al leer el archivo");
        }
    }

    public void procesarArchivo2(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] data = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        byteList = new ArrayList<>();
        byteList.clear();
        fis.read(data);
        fis.close();
        for (byte b : data) {
            byteList.add(b);
        }
        totalBytes = byteList.size();//El tamaño total de la lista de bytes que hay en el txt

        convertirAFormatoTabla(byteList, 11);
        //return byteList;
    }

    public void procesarArchivo1(File archivo) {//Procesa para poner los caracteres en la tabla principal
        try ( BufferedInputStream bis = new BufferedInputStream(new FileInputStream(archivo))) {

            int byteValue;
            byteList.clear(); // Limpiar lista antes de cargar nuevo archivo
            while ((byteValue = bis.read()//Lee el siguiente byte del archivo y lo almacena en byteValue.
                    ) != -1) {//Devuelve -1 cuando ya no hay más datos es decir la última linea.
                byteList.add((byte) byteValue);//Convierte el valor leído a tipo byte y lo guarda en la lista byteList.
            }

            totalBytes = byteList.size();//El tamaño total de la lista de bytes que hay en el txt

            convertirAFormatoTabla(byteList, 11);
        } catch (IOException e) {
            //JOptionPane.showMessageDialog(this, "Error al leer el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("\"Error al leer el archivo\"");
        }
    }

    public static ArrayList<Integer> convertirListaByteAInteger(ArrayList<Byte> listaOriginal) {
        ArrayList<Integer> listaEnteros = new ArrayList<>();
        for (Byte b : listaOriginal) {
            listaEnteros.add(b & 0xFF); // Convertir byte sin signo a entero
        }
        return listaEnteros;
    }

    public void ContarTamañoDeDatos(ArrayList<Byte> listaOriginal1) {
        x = 0;
//Pasamos el arraylistoriginal1 de bytes a un arraylist de enteros
        ArrayList<Integer> listaoringalenteros = convertirListaByteAInteger(listaOriginal1);
        listatitulos = new ArrayList<>();//Inicializamos listatitulos
        indicesInsertados = new HashSet<>(); // Guarda índices de los valores agregados manualmente
        int contador = 0;
        // Insertar números y valores ASCII en la lista
        for (Integer valor : listaoringalenteros) {
            if (listatitulos.size() == contador * 11) {
                listatitulos.add(contador * 10); // Insertar 0, 10, 20, 30...
                indicesInsertados.add(listatitulos.size() - 1); // Guardamos el índice donde lo insertamos
                contador++;
                x++;

            }
            listatitulos.add(valor);
            x++;

        }
        System.out.println("Valor de x: " + getX());
        // Si al final falta agregar otro número
        if (listatitulos.size() == contador * 11) {
            listatitulos.add(contador * 10);
            indicesInsertados.add(listatitulos.size() - 1);
        }
    }

    // Método para convertir un ArrayList<Byte> a un formato de tabla bidimensional
    public void convertirAFormatoTabla(ArrayList<Byte> listaOriginal1, int numColumnas) {
        ContarTamañoDeDatos(listaOriginal1);
        totalBytes = listaOriginal1.size();

        // Calcular filas necesarias sin dejar espacios vacíos
        int numFilas = (int) Math.ceil((double) listatitulos.size() / numColumnas);
        asciiData = new String[numFilas][numColumnas];
        hexData = new String[numFilas][numColumnas];
        // Llenar las tablas correctamente
        int index = 0;
        for (int fila = 0; fila < numFilas; fila++) {
            for (int columna = 0; columna < numColumnas; columna++) {
                if (index < listatitulos.size()) {
                    int valor = listatitulos.get(index);

                    // Si el índice está en la lista de insertados, es un número real (0, 10, 20, ...)
                    if (indicesInsertados.contains(index)) {
                        asciiData[fila][columna] = Integer.toString(valor);
                    } else {
                        // Si es un valor ASCII imprimible, lo convertimos a char
                        asciiData[fila][columna] = (valor >= 32 && valor <= 255) ? Character.toString((char) valor) : Integer.toString(valor);
                    }

                    // En la tabla hexadecimal, todos los valores se representan en formato HEX
                    hexData[fila][columna] = String.format("%02X", valor);
                    index++;
                }
            }
        }
    }

    public ArrayList<Byte> convertirAArrayList(String[][] data) {//Convertir los arreglos bidimensionales a un arraylist
        ArrayList<Byte> byteList = new ArrayList<>();

// Recorrer filas y columnas con dos bucles for        
        /*for (int i = 0; i < data.length; i++) { // Filas
            for (int j = 0; j < data[i].length; j++) { // Columnas
                System.out.print(data[i][j] + " "); // Imprimir cada valor
            }
            System.out.println(); // Salto de línea al terminar una fila
        }*/
        for (String[] fila : data) {
            for (String valor : fila) {
                if (valor != null && !valor.isEmpty()) { // Verifica que no sea null ni vacío
                    try {
                        byteList.add(Byte.parseByte(valor)); // Si es número, lo convierte directamente
                    } catch (NumberFormatException e) {
                        byteList.add((byte) valor.charAt(0)); // Si es texto, lo convierte a ASCII
                    }
                }
            }
        }
        return byteList;
    }

    public String[][] reemplazarValores(String[][] matriz) {
        String[][] nuevaMatriz = new String[matriz.length][matriz[0].length];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                String valor = matriz[i][j];
                if (valor != null) {//Siempre debe tener algo
                    // Verificar si el valor es un número dentro del rango
                    if (valor.matches("\\d+") && Integer.parseInt(valor) < rangoSalida.length) {
                        nuevaMatriz[i][j] = String.valueOf(rangoSalida[Integer.parseInt(valor)]);
                    } else {
                        nuevaMatriz[i][j] = valor; // Mantener el valor original si no es un número válido
                    }

                }

            }

        }
        // Acceder al último valor
        int ultimaFila = nuevaMatriz.length - 1; // Última fila
        int ultimaColumna = nuevaMatriz[ultimaFila].length - 1; // Última columna de la última fila

        // Condicional usando el último valor
        if ("a".equals(nuevaMatriz[ultimaFila][ultimaColumna])) {
            nuevaMatriz[ultimaFila][ultimaColumna] = "" + 10;
        } else {

        }
        return nuevaMatriz;
    }

    public void mostrarDatosEnTablas(ArrayList<Byte> byteList) {//Este metodo para guardar en una tabla 

        System.out.println("Tamaño de mi lista nueva" + byteList.size());
        int filas = (int) Math.ceil(totalBytes / 11.0);//Se divide entre 11 para saber cuantas filas hay en total siendo el 11 el numero de columnas que hay que mostrar
        System.out.println("filas :-> " + filas);
        asciiData = new String[filas][12];//Crea un arreglo bidemensional que representan todos los caracteres toda la tabla ASCII
        hexData = new String[filas][12];//Crea un arreglo bidimensional que representa toda la tabla de Hexadecimal

        for (int i = 0; i < filas; i++) {//Se recorre primero las filas 
            asciiData[i][0] = String.valueOf((i) * 10);//Acomoda los titulos de las filas (10,20,30..)
            //numeros de manera vertical y luego lo pasa a modo string
            hexData[i][0] = String.valueOf((i) * 10);

            for (int j = 0; j < 11; j++) {//Recorre las columnas
                int index = i * 11 + j;// Significa que estamos accediendo al byte en la posición index(x) del archivo.
                //System.out.print("{" + index + "}" + "(" + j + ")");
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
        /*for (int i = 0; i < asciiData.length; i++) { // Recorre filas
            for (int j = 0; j < asciiData[i].length; j++) { // Recorre columnas
                System.out.print("[" + asciiData[i][j] + "]");
            }
            System.out.println(); // Salto de línea por fila
        }*/

    }

}
