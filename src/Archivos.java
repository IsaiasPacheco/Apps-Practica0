
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author isaia
 */
public class Archivos implements Serializable {
    
    private File [] files;
    
    public Archivos(){
        
    }
    
    public Archivos( File [] files ){
        this.files = files;
    }
    
    public File[] getFiles(){
        return files;
    }
    
    public String toString(){
        return Arrays.toString(files);
    }
    
}
