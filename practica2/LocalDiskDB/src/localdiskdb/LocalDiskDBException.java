/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package localdiskdb;

/**
 *
 * @author sanjorgek
 */
public class LocalDiskDBException extends Exception {

    public LocalDiskDBException() {
        super("Unknown error");
    }

    public LocalDiskDBException(String message) {
        super(message);
    }
    
}
