/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package localdiskdb;

import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author sanjorgek
 */
public class LocalDiskDB {
    private static String obtenerLinea(BufferedReader br){
        String lineReaded = null;
        try {
            String line = br.readLine();
            if(line != null) lineReaded = line;
        }catch(IOException ioe){}
        return lineReaded;
    }

    private static String obtenerLinea(String name, int numero) throws FileNotFoundException, LocalDiskDBException{
        File indexFile = new File(name+".txt");
        BufferedReader br = new BufferedReader(new FileReader(indexFile));
        String dataFile = obtenerLinea(br);
        int count = 0;
        while(count!= numero && dataFile != null){
            dataFile = obtenerLinea(br);
        }
        if(count==numero) return dataFile;
        else throw new LocalDiskDBException("Line not found");
    }
    
    private static boolean puedeBorrarse(String nombre) throws FileNotFoundException, LocalDiskDBException{
        int indexFile = indexado(nombre);
        if(indexFile==-1){
            throw new LocalDiskDBException("");
        }else{
            String dataFile = obtenerLinea("indexes", indexFile);
            String[] dataInfo = dataFile.split("\\|");
            if(dataInfo.length != 7){
                throw new LocalDiskDBException("indexes corrupted.");
            }else{
                if(dataInfo[1].equals("true")) return true;
                else return false;
            }
        }
    } 
    
    private static boolean tieneBackup(String nombre) throws FileNotFoundException, LocalDiskDBException{
        int indexFile = indexado(nombre);
        if(indexFile==-1){
            throw new LocalDiskDBException("");
        }else{
            String dataFile = obtenerLinea("indexes", indexFile);
            String[] dataInfo = dataFile.split("\\|");
            if(dataInfo.length != 7){
                throw new LocalDiskDBException("indexes corrupted.");
            }else{
                if(dataInfo[3].equals("true")) return true;
                else return false;
            }
        }
    } 
    
    private static boolean permiteActualizaciones(String nombre) throws FileNotFoundException, LocalDiskDBException{
        int indexFile = indexado(nombre);
        if(indexFile==-1){
            throw new LocalDiskDBException("");
        }else{
            String dataFile = obtenerLinea("indexes", indexFile);
            String[] dataInfo = dataFile.split("\\|");
            if(dataInfo.length != 7){
                throw new LocalDiskDBException("indexes corrupted.");
            }else{
                if(dataInfo[3].equals("true")) return true;
                else return false;
            }
        }
    } 
    
    private static void respaldarDatos(String nombre) throws FileNotFoundException, IOException, LocalDiskDBException{
        int numberFile = indexado(nombre);
        if(numberFile==-1){
            throw new LocalDiskDBException("");
        }else{
            String dataFile = obtenerLinea("indexes", numberFile);
            String[] dataInfo = dataFile.split("\\|");
            if(dataInfo.length != 7){
                throw new LocalDiskDBException("indexes corrupted.");
            }else{
                String fecha = new SimpleDateFormat("ddMMyyyy").format(new Date());
                File file = new File(nombre+"_"+dataInfo[4]+".txt");
                File file2 = new File(nombre+"_"+fecha+".txt");
                file.renameTo(file2);
                desindexar(nombre);
                FileWriter indexFile = new FileWriter("indexes.txt",true);
                indexFile.write(nombre+"|"+dataInfo[1]+"|"+dataInfo[2]+"|"+dataInfo[3]+"|"+fecha+"|"+dataInfo[5]+"|"+dataInfo[6]+System.getProperty("line.separator"));
                indexFile.close();
            }
        }
    }
    
    private static int indexado(String name) throws FileNotFoundException, LocalDiskDBException {
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
                throw new LocalDiskDBException("index corrupted!!");
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
            IOException, LocalDiskDBException{
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
            throw new LocalDiskDBException("File don't deleted");
        }
        return false;
    }
    
    public static void crearArchivo(String name, boolean delete, boolean update, 
            boolean backup, boolean reduce) throws LocalDiskDBException, IOException {
        if(indexado(name)!=-1){
            throw new LocalDiskDBException("File already exists");
        }else{
            String fecha = new SimpleDateFormat("ddMMyyyy").format(new Date());
            String fileName = name;
            if(backup) fileName +="_"+fecha;
            FileWriter newFile = new FileWriter(fileName+".txt");
            FileWriter indexFile = new FileWriter("indexes.txt",true);
            indexFile.write(name+"|"+delete+"|"+update+"|"+backup+"|"+fecha+"|"+reduce+"|"+1+System.getProperty("line.separator"));
            indexFile.close();
            newFile.close();
        }
    }
    
    public static void eliminarArchivo(String name) throws FileNotFoundException, 
            IOException, LocalDiskDBException {
        if(puedeBorrarse(name)){
          if(tieneBackup(name)){
            respaldarDatos(name);
          }else{
            desindexar(name);
            File fileToDelete = new File(name+".txt");
            if(!fileToDelete.delete()){
                throw new LocalDiskDBException("File don't deleted");
            }
          }
        }else{
          throw new FileNotFoundException("File can't be deleted'");
        }
    }
    
    private static String arregloACadena(String[] values){
        String salida = "";
        for(int index = 0; index<values.length; index++){
            salida += values[index];
            if(index!=values.length-1) salida += "|";
        }
        return salida;
    }
    
    private static boolean duplica(String nombre, String[] values) throws FileNotFoundException, LocalDiskDBException{
        boolean allEq;
        File indexFile = new File(nombre);
        BufferedReader br = new BufferedReader(new FileReader(indexFile));
        String dataFile = obtenerLinea(br);
        String[] dataInfo;
        while(dataFile != null){
            allEq = true;
            dataInfo = dataFile.split("\\|");
            for(int index = 0; index< values.length; index++){
                if(dataInfo[index+1].equals(values[index])) allEq &= true;
                else allEq &= false;
            }
            if(allEq) return true;
            else dataFile = obtenerLinea(br);
        }
        return false;
    }
    
    public static void actializarArchivo(String nombre, String[] values) throws FileNotFoundException, IOException, LocalDiskDBException{
        int numberFile = indexado(nombre);
        if(numberFile==-1){
            throw new LocalDiskDBException("Bad name.");
        }else{
            String[] fileInfo = obtenerLinea("indexes", numberFile).split("\\|");
            if(fileInfo.length!=7){
                throw new LocalDiskDBException("Indexes corrupted.");
            }else{
                String fileName = nombre;
                if(tieneBackup(nombre)){
                    fileName += "_"+fileInfo[4];
                }
                fileName += ".txt";
                String tupla = arregloACadena(values);
                if(!duplica(fileName, values)){
                    FileWriter indexFile = new FileWriter(fileName,true);
                    indexFile.write(fileInfo[6]+"|"+tupla+System.getProperty("line.separator"));
                    indexFile.close();
                    desindexar(nombre);
                    indexFile = new FileWriter("indexes.txt");
                    indexFile.write(nombre+"|"+fileInfo[1]+"|"+fileInfo[2]+"|"+fileInfo[3]+"|"+fileInfo[4]+"|"+fileInfo[5]+"|"+(Integer.parseInt(fileInfo[6])+1)+System.getProperty("line.separator"));
                    indexFile.close();
                }else{
                    throw new LocalDiskDBException("Duplicated info");
                }
            }
        }
    }
    
    public static void actializarArchivo(String nombre, int id, String[] values) throws FileNotFoundException, IOException, LocalDiskDBException{
        int numberFile = indexado(nombre);
        if(numberFile==-1){
            throw new LocalDiskDBException("Bad name.");
        }else{
            String[] fileInfo = obtenerLinea("indexes", numberFile).split("\\|");
            if(fileInfo.length!=7){
                throw new LocalDiskDBException("Indexes corrupted.");
            }else{
                String fileName = nombre;
                if(tieneBackup(nombre)){
                    fileName += "_"+fileInfo[4];
                }
                fileName += ".txt";
                String tupla = arregloACadena(values);
                if(!duplica(fileName, values)){
                    File inputFile = new File(fileName);
                    File tempFile = new File(fileName+".tem");
                    BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                    String currentLine;
                    String[] dataInfo;
                    while((currentLine = reader.readLine()) != null){
                        String trimmedLine = currentLine.trim();
                        dataInfo = trimmedLine.split("\\|");
                        if(dataInfo[0].equals(id+"")){
                            writer.write(id+"|"+arregloACadena(values)+System.getProperty("line.separator"));
                        }else{
                            writer.write(trimmedLine+System.getProperty("line.separator"));
                        }
                    }
                    writer.close(); 
                    reader.close();
                    boolean successful = tempFile.renameTo(inputFile);
                }
            }
        }
    }
    
    public static String buscarEnArchivo(String nombre, int id) throws FileNotFoundException, IOException, LocalDiskDBException{
        String result = null;
        int numberFile = indexado(nombre);
        if(numberFile==-1){
            throw new LocalDiskDBException("Bad name.");
        }else{
            String[] fileInfo = obtenerLinea("indexes", numberFile).split("\\|");
            if(fileInfo.length!=7){
                throw new LocalDiskDBException("Indexes corrupted.");
            }else{
                String fileName = nombre;
                if(tieneBackup(nombre)){
                    fileName += "_"+fileInfo[4];
                }
                fileName += ".txt";
                File inputFile = new File(fileName);
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                String currentLine;
                String[] dataInfo;
                boolean bandera = true;
                while((currentLine = reader.readLine()) != null && bandera){
                    String trimmedLine = currentLine.trim();
                    dataInfo = trimmedLine.split("\\|");
                    if(dataInfo[0].equals(id+"")){
                        result = arregloACadena(dataInfo);
                        bandera = false;
                    }
                }
                reader.close();
            }
        }
        return result;
    }
    
    public static List<String> buscarEnArchivo(String nombre, int number, String valor)throws FileNotFoundException, IOException, LocalDiskDBException{
        List<String> result = new ArrayList<String>();
        int numberFile = indexado(nombre);
        if(numberFile==-1){
            throw new LocalDiskDBException("Bad name.");
        }else{
            String[] fileInfo = obtenerLinea("indexes", numberFile).split("\\|");
            if(fileInfo.length!=7){
                throw new LocalDiskDBException("Indexes corrupted.");
            }else{
                String fileName = nombre;
                if(tieneBackup(nombre)){
                    fileName += "_"+fileInfo[4];
                }
                fileName += ".txt";
                File inputFile = new File(fileName);
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                String currentLine;
                String[] dataInfo;
                while((currentLine = reader.readLine()) != null){
                    String trimmedLine = currentLine.trim();
                    dataInfo = trimmedLine.split("\\|");
                    if(dataInfo[number].equals(valor)){
                        result.add(arregloACadena(dataInfo));
                    }
                }
                reader.close();
            }
        }
        return result;
    }
}
