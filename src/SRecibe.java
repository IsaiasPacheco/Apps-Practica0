
import java.net.*;
import java.io.*;
import java.util.Arrays;

/**
 *
 * @author axele
 */
public class SRecibe {

    public static void main(String[] args) {
        try {

            //Inicializacion de servidor
            int pto = 8000;
            ServerSocket s = new ServerSocket(pto);
            s.setReuseAddress(true);
            System.out.println("Servidor iniciado esperando por archivos..");

            //En cada iteracion el socket va a recibir un archivo
            //Se especifica la ruta donde se van a aguardar los archivos
            File f = new File("");
            String ruta = f.getAbsolutePath();
            String carpeta = "archivos";
            String ruta_archivos = ruta + "\\" + carpeta + "\\";
            System.out.println("ruta: " + ruta_archivos);

            //Con mkdirs se crea la carpera o carpetas especificadas en el archivo
            File f2 = new File(ruta_archivos);
            f2.mkdirs();

            f2.setWritable(true);

            for (;;) {
                Socket cl = s.accept();
                System.out.println("Cliente conectado desde " + cl.getInetAddress() + ":" + cl.getPort());

                //Se obtiene el flujo de entrada desde el cliente
                DataInputStream dis = new DataInputStream(cl.getInputStream());

                /**
                 * Se obtiene la bandera para realizar la opcion 
                 * 0: Listar los archivos en la carpeta personal 
                 * 1: Crear Carpeta 
                 * 2: Crear Archivo 
                 * 3: Subir Archivos
                 */
                try {

                    int opcion = dis.readInt();
                    System.out.println("Opcion: " + opcion + " seleccionada ");

                    switch (opcion) {
                        case 0:

                            //Flujo de salida
                            ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());

                            System.out.println("Se estan obteniendo los archivos");

                            File auxf = new File(ruta_archivos);
                            File[] auxFiles = auxf.listFiles();

                            Archivos archivos = new Archivos(auxFiles);

                            oos.writeObject(archivos);
                            oos.flush();
                            oos.close();
                            break;
                            
                        case 1:
                            
                            String auxnombreCarpeta = dis.readUTF();
                            String rutaCarpeta = dis.readUTF();
                            
                            String rutaNuevaCarpeta;
                            
                            if( rutaCarpeta.length() == 0 ){
                                rutaNuevaCarpeta = ruta_archivos + auxnombreCarpeta;
                            }else{
                                rutaNuevaCarpeta = ruta_archivos + rutaCarpeta + "\\" + auxnombreCarpeta;
                            }
                            
                            System.out.println("ruta: " + rutaNuevaCarpeta);
                            File auxDir = new File(rutaNuevaCarpeta);
                            
                            // Si el la carpeta no existe se crea
                            auxDir.mkdir();
                            
                            System.out.println("Carpeta creada");
                        break;
                            
                        case 2:
                            
                            String nombreArchivo = dis.readUTF();
                            String rutaArchivo = dis.readUTF();
                            
                            String rutaNuevoArchivo;
                            
                            if( rutaArchivo.length() == 0 ){
                                rutaNuevoArchivo = ruta_archivos + nombreArchivo;
                            }else{
                                rutaNuevoArchivo = ruta_archivos + rutaArchivo + "\\" + nombreArchivo;
                            }
                            
                            File auxFile = new File(rutaNuevoArchivo);
                            
                            // Si el archivo no existe es creado
                            if (!auxFile.exists()) {
                                auxFile.createNewFile();
                            }
                            
                            System.out.println("Archivo creado");
                        break;

                        case 3:
                            //Bandera de carpeta
                            int bandera = dis.readInt();

                            if (bandera == 1) {
                                String nombreCarpeta = dis.readUTF();
                                File faux = new File(ruta_archivos + nombreCarpeta);
                                faux.mkdirs();
                                faux.setWritable(true);

                                continue;
                            }

                            //Recibir la ruta del archivo
                            String rutaArch = dis.readUTF();

                            if (rutaArch.length() != 0) {
                                rutaArch = "\\" + rutaArch + "\\";
                            }

                            auxf = new File(ruta_archivos + rutaArch);
                            auxf.mkdirs();
                            auxf.setWritable(true);

                            //Se recibe el nombre del archivo
                            String nombre = dis.readUTF();

                            //Se recibe el tamaño
                            long tam = dis.readLong();

                            System.out.println("Comienza descarga del archivo " + nombre + " de " + tam + " bytes\n\n");

                            //Flujo de datos para escribir el archivo
                            DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos + rutaArch + nombre));

                            //Bytes recibidos
                            long recibidos = 0;
                            //Variablesauxiliares
                            int l = 0,
                             porcentaje = 0;

                            while (recibidos < tam) {
                                byte[] b = new byte[1500];
                                l = dis.read(b);
                                System.out.println("leidos: " + l);
                                dos.write(b, 0, l);
                                dos.flush();
                                recibidos = recibidos + l;
                                porcentaje = (int) ((recibidos * 100) / tam);
                                System.out.print("\rRecibido el " + porcentaje + " % del archivo");
                            }//while

                            System.out.println("Archivo recibido..");

                            dos.close();
                            break;
                    }

                } catch (Exception e) {
                }

                dis.close();

                cl.close();
            }//for

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//main
}
