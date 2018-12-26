/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudserver;

/**
 *
 * @author Nuno Campos
 */
class ServerIsEmptyException extends Exception {
    
    public ServerIsEmptyException() {
        super();
    }

    public ServerIsEmptyException(String message) {
        super(message);
    }
}
