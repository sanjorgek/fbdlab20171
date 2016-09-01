/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package localdiskbase;

/**
 *
 * @author sanjorgek
 */
public class LocalDiskException extends Exception {

    public LocalDiskException() {
        super("Unknown error");
    }

    public LocalDiskException(String message) {
        super(message);
    }

}
