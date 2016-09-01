/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package localdiskbase;

import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import localdiskbase.LocalDiskException;

/**
 *
 * @author sanjorgek
 */
public class LocalDiskBase {
    private static String obtenerLinea(BufferedReader br){
        String lineReaded = null;
        try {
            String line = br.readLine();
            if(line != null) lineReaded = line;
        }catch(IOException ioe){
        }
        return lineReaded;
    }
    
    private static int indexado(String name) throws LocalDiskException {
        int resultSearch = -1;
        boolean flag = true;
        try{
            File indexFile = new File("indexes.txt");
            BufferedReader br = new BufferedReader(new FileReader(indexFile));
            String dataFile = obtenerLinea(br);
            String[] dataInfo;
            int count = 0;
            while(dataFile != null && flag){
                dataInfo = dataFile.split("\\|");
                if(dataInfo.length != 6){
                    throw new LocalDiskException("index corrupted!!");
                }else{
                    if(dataInfo[0].equals(name)){
                        resultSearch = count;
                        flag = false;
                    }
                }
                dataFile = obtenerLinea(br);
                count +=1;
            }
        }catch(FileNotFoundException fnfe){
            System.out.println(fnfe);
        }
        return resultSearch;
    }
    
    private static boolean desindexar(String name){
        File inputFile = new File("indexes.txt");
        File tempFile = new File("indexes.txt.tem");
        try{
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        }catch(FileNotFoundException fnfe){
            System.out.println(fnfe);
        }catch(IOException ioe){
            System.out.println(ioe);
        }
        return false;
    }
    
    public static void crearArchivo(String name, boolean delete, boolean update, 
            boolean backup, boolean reduce) throws LocalDiskException {
        try{
            if(indexado(name)!=-1){
                throw new LocalDiskException("File already exists");
            }else{
                FileWriter newFile = new FileWriter(name);
                FileWriter indexFile = new FileWriter("indexes.txt",true);
                indexFile.write(name+"|"+delete+"|"+update+"|"+backup+"|"+reduce+"|"+1+"\n");
                indexFile.close();
                newFile.close();
            }
        }catch(IOException ioe){
            throw new LocalDiskException("IO Error");
        }
    }
    
    public static void eliminarArchivo(String nombre) throws LocalDiskException {
        File fileToDelete = new File(nombre);
        if(!fileToDelete.delete()){
            throw new LocalDiskException("file don't deleted");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try{
            LocalDiskBase.crearArchivo("hola.txt", false, false, false, false);
            LocalDiskBase.eliminarArchivo("hola.txt");
        }catch(LocalDiskException lde){
            System.out.println(lde);
        }
    }
    
}
