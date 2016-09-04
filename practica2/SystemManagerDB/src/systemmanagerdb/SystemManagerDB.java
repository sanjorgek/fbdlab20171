/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package systemmanagerdb;

import localdiskdb.LocalDiskDB;
import localdiskdb.LocalDiskDBException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author sanjorgek
 */
public class SystemManagerDB {
    private static boolean boolOption(String option){
        return option.equals("1") || option.equals("true");
    }
    
    private static void crearArchivo(Scanner scOp){
        System.out.println(
                "Indica el nombre del archivo\nTodas las opciones son necesarias y las puedes representar con booleanos\n"+
                "[nombre] [puede borrarse] [puede actualizarse] [requiere backup] [se puede eliminar]"
        );
        String[] options = scOp.nextLine().split(" ");
        if(options.length!=5) crearArchivo(scOp);
        else{
            try{
                LocalDiskDB.crearArchivo(options[0], boolOption(options[1]), 
                        boolOption(options[2]), boolOption(options[3]), boolOption(options[4]));
            }catch(LocalDiskDBException lddbe){
                System.out.println(lddbe);
            }catch(IOException ioe){
                System.out.println(ioe);
            }
        }
    }
    
    private static void borrarArchivo(Scanner scOp){
        System.out.println("Ingresa el nombre del archivo a borrar");
        String options = scOp.nextLine();
        try{
            LocalDiskDB.eliminarArchivo(options);
            System.out.println("Borrado");
        }catch(LocalDiskDBException lddbe){
            System.out.println(lddbe);
        }catch(IOException ioe){
            System.out.println(ioe);  
        }
    }
    
    private static void abrirArchivo(Scanner scOp){
        boolean continuacionFile = false;
        int id = 0;
        System.out.println("ingresa el nombre del archivo");
        String nombreArchivo = scOp.nextLine();
        continuacionFile = true;
        while(continuacionFile){
            System.out.println(
                "Selecciona la opcion a usar:\n"+
                "0 - insertar un nuevo elemento\n"+
                "1 - actualizar un elemento\n"+
                "2 - buscar un elemento\n"+
                "3 - buscar elementos\n"+
                "4 - borrar un elemento\n"+
                "5 - ver coincidencias con otro archivo\n"+
                "otra cosa para sair.\n"
            );
            try{
                switch(Integer.parseInt(scOp.nextLine())){
                    //insertar nuevo record
                    case 0:
                        System.out.println("ingresa los datos separados por un pipe");
                        String[] tupleS = scOp.nextLine().split("\\|");
                        LocalDiskDB.actializarArchivo(nombreArchivo, tupleS);
                        break;
                    //actualizar record
                    case 1:
                        System.out.println("Ingresa el id de la tupla a actualizar");
                        id = Integer.parseInt(scOp.nextLine());
                        System.out.println("Ingresa los datos separados por un pipe");
                        String[] tupleF = scOp.nextLine().split("\\|");
                        LocalDiskDB.actializarArchivo(nombreArchivo, id, tupleF);
                        break;
                    //buscar un record
                    case 2:
                        System.out.println("Ingresa el indice a buscar\n");
                        String queryS = scOp.nextLine();
                        System.out.println(
                            LocalDiskDB.buscarEnArchivo(
                                    nombreArchivo, Integer.parseInt(queryS)
                            )
                        );
                        break;
                    //buscar varios records
                    case 3:
                        System.out.println("Ingresa la busqueda indicando en que posicion y que buscar\n");
                        String[] queryF = scOp.nextLine().split(" ");
                        if(queryF.length==2) System.out.println(
                                LocalDiskDB.buscarEnArchivo(
                                        nombreArchivo, Integer.parseInt(queryF[0]), queryF[1]
                                )
                        );
                        break;
                    case 4:
                        System.out.println("Indica el id de la tupla a borrar");
                        id = Integer.parseInt(scOp.nextLine());
                        LocalDiskDB.borrarTupla(nombreArchivo, id);
                        break;
                    case 5:
                        System.out.println("Indica la posicion y el valor de este archivo, el "
                                + "nombre del archivo a buscar, la posicion del id y el nuevo nombre del archivo"
                                + " separados por espacios");
                        String[] joinParams = scOp.nextLine().split(" ");
                        LocalDiskDB.generarNuevaTabla(nombreArchivo, 
                                Integer.parseInt(joinParams[0]), joinParams[1], 
                                joinParams[2], Integer.parseInt(joinParams[3]), 
                                joinParams[4]);
                    default:
                        continuacionFile = false;
                }
            }catch(LocalDiskDBException lddbe){
                System.out.println(lddbe);
                continuacionFile = false;
            }catch(IOException ioe){
                System.out.println(ioe);
                continuacionFile = false;
            }catch(NumberFormatException nfe){
                System.out.println(nfe);
                continuacionFile = false;
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        File f = new File("indexes.txt");
        try{
            if(!f.exists() || f.isDirectory()){
                FileWriter newFile = new FileWriter("indexes.txt");
                newFile.close();
            }
            boolean continuacion = true;
            Scanner scOp = new Scanner(System.in);
            while(continuacion){
                System.out.println("Selecciona una opcion:\n"+
                        "0 - crear un archivo.\n"+
                        "1 - abrir un archivo.\n"+
                        "2 - borrar archivo.\n"+
                        "otro numero salir.\n"
                );
                try{
                    switch(Integer.parseInt(scOp.nextLine())){
                        //Crear archivo
                        case 0:
                            crearArchivo(scOp);
                            break;
                        //Abrir archivo
                        case 1:
                            abrirArchivo(scOp);
                            break;
                        //Borrar archivo
                        case 2:
                            borrarArchivo(scOp);
                            break;
                        default:
                            continuacion = false;
                    }
                }catch(NumberFormatException nfe){
                    continuacion = false;
                }
            }
        }catch(IOException ioe){
            System.out.println("No se puede acceder a la base.");
        }
    }
    
}
