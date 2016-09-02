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
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private static String obtenerLinea(String name, int numero) throws FileNotFoundException, LocalDiskException{
        File indexFile = new File(name+".txt");
        BufferedReader br = new BufferedReader(new FileReader(indexFile));
        String dataFile = obtenerLinea(br);
        int count = 0;
        while(count!= numero && dataFile != null){
            dataFile = obtenerLinea(br);
        }
        if(count==numero) return dataFile;
        else throw new LocalDiskException("Line not found");
    }
    
    private static boolean puedeBorrarse(String nombre) throws FileNotFoundException, LocalDiskException{
        int indexFile = indexado(nombre);
        if(indexFile==-1){
            throw new LocalDiskException("");
        }else{
            String dataFile = obtenerLinea("indexes", indexFile);
            String[] dataInfo = dataFile.split("\\|");
            if(dataInfo.length != 7){
                throw new LocalDiskException("indexes corrupted.");
            }else{
                if(dataInfo[1].equals("true")) return true;
                else return false;
            }
        }
    } 
    
    private static boolean tieneBackup(String nombre) throws FileNotFoundException, LocalDiskException{
        int indexFile = indexado(nombre);
        if(indexFile==-1){
            throw new LocalDiskException("");
        }else{
            String dataFile = obtenerLinea("indexes", indexFile);
            String[] dataInfo = dataFile.split("\\|");
            if(dataInfo.length != 7){
                throw new LocalDiskException("indexes corrupted.");
            }else{
                if(dataInfo[3].equals("true")) return true;
                else return false;
            }
        }
    } 
    
    private static boolean permiteActualizaciones(String name){
        return false;
    } 
    
    private static void respaldarDatos(String nombre) throws FileNotFoundException, LocalDiskException{
        int indexFile = indexado(nombre);
        if(indexFile==-1){
            throw new LocalDiskException("");
        }else{
            String dataFile = obtenerLinea("indexes", indexFile);
            String[] dataInfo = dataFile.split("\\|");
            if(dataInfo.length != 7){
                throw new LocalDiskException("indexes corrupted.");
            }else{
                File file = new File(nombre+"_"+dataInfo[4]+".txt");
                File file2 = new File(nombre+"_"+new SimpleDateFormat("ddMMyyyy").format(new Date())+".txt");
                file.renameTo(file2);
            }
        }
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
            if(dataInfo.length != 7){
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
            String fecha = new SimpleDateFormat("ddMMyyyy").format(new Date());
            String fileName = name;
            Date createDate = new Date();
            if(backup) fileName +="_"+fecha;
            FileWriter newFile = new FileWriter(fileName+".txt");
            FileWriter indexFile = new FileWriter("indexes.txt",true);
            indexFile.write(name+"|"+delete+"|"+update+"|"+backup+"|"+fecha+"|"+reduce+"|"+1+System.getProperty("line.separator"));
            indexFile.close();
            newFile.close();
        }
    }
    
    public static void eliminarArchivo(String name) throws FileNotFoundException, 
            IOException, LocalDiskException {
        if(puedeBorrarse(name)){
          if(tieneBackup(name)){
            respaldarDatos(name);
          }else{
            desindexar(name);
            File fileToDelete = new File(name+".txt");
            if(!fileToDelete.delete()){
                throw new LocalDiskException("File don't deleted");
            }
          }
        }else{
          throw new FileNotFoundException("File can't be deleted'");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try{
            FileWriter newFile = new FileWriter("indexes.txt");
            newFile.close();
            
            LocalDiskBase.crearArchivo("file1", true, false, false, false);
            LocalDiskBase.eliminarArchivo("file1");
            LocalDiskBase.crearArchivo("file2", true, false, true, false);
            LocalDiskBase.eliminarArchivo("file2");
            
            File fileToDelete = new File("indexes.txt");
            fileToDelete.delete();
        }catch(IOException ioe){
            System.out.println(ioe);
        }catch(LocalDiskException lde){
            System.out.println(lde);
        }
    }
    
}
