/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package systemmanagerdb;

import localdiskdb.LocalDiskDB;
import localdiskdb.LocalDiskDBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author sanjorgek
 */
public class SystemManagerDB {
    private static boolean boolOption(String option){
        if(option.equals("1") || option.equals("true")) return true;
        else return false;
    }
    
    private static void crearArchivo(Scanner scOp){
        System.out.println(
                "Indica el nombre del archivo asi como todas las opciones son necesarias\n"+
                "[nombre] [puede borrarse] [puede actualizarse] [requiere backup] [se puede eliminar]"
        );
        String[] options = scOp.nextLine().split(" ");
        if(options.length!=5) crearArchivo(scOp);
        else{
            try{
                LocalDiskDB.crearArchivo(options[0], boolOption(options[1]), 
                        boolOption(options[2]), boolOption(options[3]), boolOption(options[4]));
            }catch(LocalDiskDBException lddbe){
                
            }catch(IOException ioe){
                
            }
        }
    }
    
    private static void borrarArchivo(Scanner scOp){
        String options = scOp.nextLine();
        try{
            LocalDiskDB.eliminarArchivo(options);
        }catch(LocalDiskDBException lddbe){
              
        }catch(IOException ioe){
               
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        boolean continuacion = true;
        boolean continuacionFile = false;
        Scanner scOp = new Scanner(System.in);
        while(continuacion){
            System.out.println("Selecciona una opcion:\n"+
                    "0 - crear un archivo.\n"+
                    "1 - abrir un archivo.\n"+
                    "2 - borrar archivo.\n"+
                    "otro numero salir.\n"
            );
            switch(scOp.nextInt()){
                //Crear archivo
                case 0:
                    crearArchivo(scOp);
                    break;
                //Abrir archivo
                case 1:
                    String nombreArchivo = scOp.nextLine();
                    continuacionFile = true;
                    while(continuacionFile){
                        switch(scOp.nextInt()){
                            //insertar nuevo record
                            case 0:
                                String[] tupleS = scOp.nextLine().split("|");
                                try{
                                    LocalDiskDB.actializarArchivo(nombreArchivo, tupleS);
                                }catch(FileNotFoundException fnfe){
                                    
                                }catch(IOException ioe){
                                    
                                }catch(LocalDiskDBException lddbe){
                                    
                                }
                                break;
                            //actualizar record
                            case 1:
                                int id = scOp.nextInt();
                                String[] tupleF = scOp.nextLine().split("|");
                                try{
                                    LocalDiskDB.actializarArchivo(nombreArchivo, id, tupleF);
                                }catch(FileNotFoundException fnfe){
                                    
                                }catch(IOException ioe){
                                    
                                }catch(LocalDiskDBException lddbe){
                                    
                                }
                                break;
                            //buscar un record
                            case 2:
                                String queryS = scOp.nextLine();
                                try{
                                    System.out.println(
                                        LocalDiskDB.buscarEnArchivo(
                                                nombreArchivo, Integer.parseInt(queryS)
                                        )
                                    );
                                }catch(FileNotFoundException fnfe){
                                    
                                }catch(IOException ioe){
                                    
                                }catch(LocalDiskDBException lddbe){
                                    
                                }
                                break;
                            //buscar varios records
                            case 3:
                                String[] queryF = scOp.nextLine().split(" ");
                                try{
                                    if(queryF.length==2) System.out.println(
                                            LocalDiskDB.buscarEnArchivo(
                                                    nombreArchivo, Integer.parseInt(queryF[0]), queryF[1]
                                            )
                                    );
                                }catch(FileNotFoundException fnfe){
                                    
                                }catch(IOException ioe){
                                    
                                }catch(LocalDiskDBException lddbe){
                                    
                                }
                                break;
                            default:
                                continuacionFile = false;
                        }
                    }
                    break;
                //Borrar archivo
                case 2:
                    borrarArchivo(scOp);
                    break;
                default:
                    continuacion = false;
            }
        }
    }
    
}
