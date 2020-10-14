
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
