/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cifrado;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class LeerArchivo {
    public static void main(String[] args) {
        ArrayList<Character> caracteres = new ArrayList<>();

        try (InputStream is = LeerArchivo.class.getClassLoader().getResourceAsStream("archivo.txt")) {
            if (is == null) {
                throw new FileNotFoundException("El archivo no se encontró en resources.");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int c;
            while ((c = br.read()) != -1) {
                caracteres.add((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(caracteres);
    }
}
