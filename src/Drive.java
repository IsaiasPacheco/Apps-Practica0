

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import javax.swing.JFileChooser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author isaia
 */
public class Drive {
    
    private int puerto;
    private String dir;
    
    public Drive(String dir, int puerto){
        this.puerto = puerto;
        this.dir = dir;
    }
    
    /**
     * Realiza una llamada al servidor con la opcion 0 (listar archivos)
     * @return Objeto con arreglo de tiplo File inicializado
     */
    
    public Archivos listRoot(){
        
        //Objeto de retorno ue contiene el arreglo de archivos obtenidos por la llamada
        Object auxRet = null;
        
        try {
            Socket cl = new Socket(dir, puerto);
            System.out.println("Conexion con el servidor establecida, obteniendo archivos...");
            
            //Flujo de datos de salida
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            dos.writeInt(0);
            
            //Flujo de entrada
            ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
            auxRet = (Archivos) ois.readObject();
            
            dos.close();
            cl.close();
            
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        }
        
        return (Archivos) auxRet;
    }
    
    /**
     * Crea un archivo en el servidor en la ruta especificada
     * @param nombreArchivo 
     * @param ruta
     */
    public void crearArchivo(String nombreArchivo, String ruta){
        
        try {
            Socket cl = new Socket(dir, puerto);
            System.out.println("Conexion con el servidor establecida, obteniendo archivos...");
            
            //Flujo de datos de salida
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            
            //Opcion de servidor 2( crear Archivo )
            dos.writeInt(2);
            dos.flush();
            
            //Nombre del archivo
            dos.writeUTF(nombreArchivo);
            dos.flush();
            
            //ruta del archivo
            dos.writeUTF(ruta);
            dos.flush();
            
            dos.close();
            cl.close();
            
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void creaCarpeta(String nombreCarpeta, String rutaCarpeta ){
        try {
            Socket cl = new Socket(dir, puerto);
            System.out.println("Conexion con el servidor establecida, creando carpeta...");
            
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            
            //Opcion 1 del Servidor
            dos.writeInt(1);
            dos.flush();
            
            //Nombre de la carpeta
            dos.writeUTF(nombreCarpeta);
            dos.flush();
            
            //Ruta de la carpeta
            dos.writeUTF(rutaCarpeta);
            dos.flush();
            
            dos.close();
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
            
        }
            
    }
    
    /**
     * Se encarga de subir un archivo, archivo(s) o carpetas seleccioandas con
     * el JFileChooser
     */
    public void subirArchivos(){
        
        //JFileChooser para seleccionar archivos y carpetas
        JFileChooser jf = new JFileChooser();
        jf.setMultiSelectionEnabled(true);
        
        //Con jf se podran seleccionar archivos y carpetas 
        jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int r = jf.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            
            //Se obtiene el archivo(s) seleccionado
            File[] files = jf.getSelectedFiles();
            
            for( int i = 0; i<files.length; i++){
                if( files[i].isFile() ){
                    enviarArchivo(files[i], dir, puerto);
                }else{
                    rutaArch = files[i].getName();
                    enviarCarpeta(files[i], dir, puerto);
                }
            }
        }
    }
    
    private static String rutaArch = "";
    private static String auxRuta = "";
    
    /**
     * Se encarga de recorrer el arbol de directorios para crear carpetas y subir archivos
     * @param file
     * @param dir
     * @param puerto 
     */
    public static void enviarCarpeta( File file, String dir, int puerto){
        
        File[] archivos = file.listFiles();       
        
        for (File archivo : archivos) {
            
            if (archivo.isFile()) {
                enviarArchivo(archivo, dir, puerto);
            } else {
                auxRuta = auxRuta+"\\"+archivo.getName();
                crearCarpeta(rutaArch+auxRuta, dir, puerto);
                enviarCarpeta(archivo, dir, puerto);
            }
        }
        
        auxRuta = "";
        
    }
    
    /**
     * Se encargar de crear una carpeta en el servidor especificando la ruta
     * @param ruta
     * @param dir
     * @param puerto 
     */
    public static void crearCarpeta(String ruta, String dir, int puerto ){
        try {
            Socket cl = new Socket(dir, puerto);
            System.out.println("Conexion con el servidor establecida, creando carpeta...");
            
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            dos.writeInt(1);
            dos.writeUTF(ruta);
            
            dos.close();
            cl.close();
            
        } catch (Exception e) {
            System.out.println(e);
        }
       
    }
    
    /**
     * Se encargar de subir un archivo en la carpeta "archivos" del servidor
     * @param file
     * @param dir
     * @param puerto 
     */
    private static void enviarArchivo(File file, String dir, int puerto) {
        //Establecer conexion con el servidor
        try {
            //Se crea el socket
            Socket cl = new Socket(dir, puerto);
            System.out.println("Conexion con el servidor establecida, enviando archivo...");

            File f = file;
            String nombre = f.getName();
            String path = f.getAbsolutePath();
            long tam = f.length();

            System.out.println("Preparandose pare enviar archivo " + path + " de " + tam + " bytes\n\n");

            //Flujo de datos para escritura de datos
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            //Flujo de lectura para el archivo
            DataInputStream dis = new DataInputStream(new FileInputStream(path));
            
            //Opcion 3 (subir archivos)
            dos.writeInt(3);
            dos.flush();
            
            //Bandera de carpeta en 0
            dos.writeInt(0);
            dos.flush();
            
            //Enviar ruta del archivo
            dos.writeUTF(rutaArch+auxRuta);
            dos.flush();
            
            //Escribir nombre del archivo
            dos.writeUTF(nombre);
            dos.flush();

            //Se escrible el tamaño del archivo
            dos.writeLong(tam);
            //Se cierra el flujo para que se envie el dato
            dos.flush();

            //Variable auxiliar para ver si el archivo ya se envio
            long enviados = 0;

            //Variable auxiliar para saber cuantos bytes se escriben en el socket
            int l = 0, porcentaje = 0;

            //Mientras no se complete el envio
            while (enviados < tam) {

                //Se cargan 1500 bytes del archivo en el buffer b
                byte[] b = new byte[1500];
                l = dis.read(b);

                //Se informa de cuantos bytes se enviaron
                System.out.println("enviados: " + l);

                //Se escriben los bytes en el flujo de salida
                dos.write(b, 0, l);

                //Se cierra el flujo para el envio
                dos.flush();

                //Se incrementan los bytes enviados hasta el momento
                enviados = enviados + l;

                //Se calcula el porcentaje
                porcentaje = (int) ((enviados * 100) / tam);
                System.out.print("\rEnviado el " + porcentaje + " % del archivo");

            }//while
            System.out.println("\nArchivo enviado..");
            dis.close();
            dos.close();
            cl.close();

        } catch (Exception e) {
            System.out.println(e);
        }

    }
    
}
