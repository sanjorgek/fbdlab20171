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
    
    private static int indexado(String name) throws FileNotFoundException, LocalDiskException {
        int resultSearch = -1;
        boolean flag = true;
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
        return resultSearch;
    }
    
    private static boolean desindexar(String name) throws FileNotFoundException,
            IOException, LocalDiskException{
        File inputFile = new File("indexes.txt");
        File tempFile = new File("indexes.txt.tem");
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        String currentLine;
        String[] dataInfo;
        while((currentLine = reader.readLine()) != null){
            String trimmedLine = currentLine.trim();
            dataInfo = trimmedLine.split("\\|");
            if(!dataInfo[0].equals(name)){
                writer.write(trimmedLine+System.getProperty("line.separator"));
            }
        }
        writer.close(); 
        reader.close(); 
        boolean successful = tempFile.renameTo(inputFile);
        if(!successful){
            throw new LocalDiskException("File don't deleted");
        }
        return false;
    }
    
    public static void crearArchivo(String name, boolean delete, boolean update, 
            boolean backup, boolean reduce) throws LocalDiskException, IOException {
        if(indexado(name)!=-1){
            throw new LocalDiskException("File already exists");
        }else{
            FileWriter newFile = new FileWriter(name);
            FileWriter indexFile = new FileWriter("indexes.txt",true);
            indexFile.write(name+"|"+delete+"|"+update+"|"+backup+"|"+reduce+"|"+1+System.getProperty("line.separator"));
            indexFile.close();
            newFile.close();
        }
    }
    
    public static void eliminarArchivo(String name) throws FileNotFoundException, 
            IOException, LocalDiskException {
        File fileToDelete = new File(name);
        desindexar(name);
        if(!fileToDelete.delete()){
            throw new LocalDiskException("File don't deleted");
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
        }catch(IOException ioe){
            System.out.println(ioe);
        }catch(LocalDiskException lde){
            System.out.println(lde);
        }
    }
    
}
