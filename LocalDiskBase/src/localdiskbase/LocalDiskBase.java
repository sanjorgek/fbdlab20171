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
    
    private static int indexado(String name){
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
                if(dataInfo.length != 5){
                    System.out.println("index corrupted!!");
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
    
    private static boolean desindexar(){
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
            boolean backup, boolean reduce) {
        try{
            if(indexado(name)==-1){
                System.out.println("Already exists");
            }else{
                FileWriter newFile = new FileWriter(name);
                FileWriter indexFile = new FileWriter("indexes.txt",true);
                indexFile.write(name+"|"+delete+"|"+update+"|"+backup+"|"+reduce);
                System.out.println("file "+ name + " created");
                indexFile.close();
                newFile.close();
            }
        }catch(IOException ioe){
            System.out.println(ioe);
        }
    }
    
    public static void eliminarArchivo(String nombre){
        File fileToDelete = new File(nombre);
        if(fileToDelete.delete()){
            System.out.println("file deleted");
        }else{
            System.out.println("file don't deleted");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        LocalDiskBase.crearArchivo("hola.txt", false, false, false, false);
        LocalDiskBase.eliminarArchivo("hola.txt");
    }
    
}
