import java.net.*;
import java.io.*;
import javax.swing.JFileChooser;
/**
 *
 * @author axele
 */
public class CEnvia {
    public static void main(String[] args){
        try{
            
            //Inializacion de socket del cliente
            int pto = 8000;
            String dir = "127.0.0.1";
            Socket cl = new Socket(dir,pto);
            
            //Conexion establecida
            System.out.println("Conexion con servidor establecida.. lanzando FileChooser..");
            
            //Selector de archivos
            JFileChooser jf = new JFileChooser();
            jf.setMultiSelectionEnabled(true);
            
            //Se abre la caja de dialogo
            int r = jf.showOpenDialog(null);
            
            //Si se selecciono un archivo
            if(r==JFileChooser.APPROVE_OPTION){
                
                //Arreglo de archivos seleccionados
                File [] files = jf.getSelectedFiles();
                
                //Se crear archivo con el archivo seleccionado
                File f = jf.getSelectedFile();
                
                //Obtengo el nombre del archivo
                String nombre = f.getName();
                
                //Obtengo la ruta del archivo
                String path = f.getAbsolutePath();
                
                //Obtenfo el tam del archivo
                long tam = f.length();
                
                System.out.println("Preparandose pare enviar archivo "+path+" de "+tam+" bytes\n\n");
                
                //Ya que quiero enviar tipos primivos se selecciona este flujo de salida
                //Que sea primitivo me indica que solo puedo escribir valores y no referencias
                //Si quiero utilizar referencias se necesita un objectOutputStream
                
                //Escribo en el Socket : flujo para escribir en el socket
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                
                //Leo el archivo byte por byte : flujo para leer el archivo
                DataInputStream dis = new DataInputStream(new FileInputStream(path));
                
                //Se va a leer el archivo y escribir en el socket hasta que el archivo 
                //se envie completamente
                
                //Primero enviamos los atributos del archivo
                
                //Se escribe el nombre del archivo
                dos.writeUTF(nombre);
                
                //Se cierra el flujo para que se envie el dato
                dos.flush();
                
                //Se escrible el tamaño del archivo
                dos.writeLong(tam);
                //Se cierra el flujo para que se envie el dato
                dos.flush();
                
                //Variable auxiliar para ver si el archivo ya se envio
                long enviados = 0;
                
                //Variable auxiliar para saber cuantos bytes se escriben en el socket
                int l=0,porcentaje=0;
               
                //Minestras no se envie todo el archivo
                while(enviados<tam){
                   
                    //Se cargan 1500 bytes del archivo en el buffer b
                    byte[] b = new byte[1500];
                    l=dis.read(b);
                    
                    //Se informa de cuantos bytes se enviaron
                    System.out.println("enviados: "+l);
                    
                    //Se escriben los bytes en el flujo de salida
                    dos.write(b,0,l);
                    
                    //Se cierra el flujo para el envio
                    dos.flush();
                    
                    //Se incrementan los bytes enviados hasta el momento
                    enviados = enviados + l;
                    
                    //Se calcula el porcentaje
                    porcentaje = (int)((enviados*100)/tam);
                    System.out.print("\rEnviado el "+porcentaje+" % del archivo");
                    
                }//while
                System.out.println("\nArchivo enviado..");
                dis.close();
                dos.close();
                cl.close();
            }//if
        }catch(Exception e){
            e.printStackTrace();
        }//catch
    }//main
}
