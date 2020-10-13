/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author isaia
 */
public class Main {
    
    public static void main(String [] args ){
        Drive drive = new Drive("localhost", 8000);
        System.out.println("Datos: " + drive.listRoot().toString());
        drive.creaCarpeta("ppprueba", "");
        drive.crearArchivo("prueba.txt", "");
    }
    
}
