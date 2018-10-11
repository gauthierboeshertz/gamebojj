package ch.epfl.gameboj;

import java.util.ArrayList;
import java.util.Objects;
import ch.epfl.gameboj.component.Component;

/**
*@author Alvaro Cauderan ( 282186)
*@author Gauthier Boeshertz (283192)
*représente — de manière très abstraite — les bus d'adresses et
* de données connectant les composants du Game Boy entre eux.
*
*/
public final class Bus {
    private  final  ArrayList<Component> Attachedto = new ArrayList<>();
    private final int ADDRESS_MAX8 = 255;
    
/**
 *  attache le composant donné au bus
 *  @throws NullPointerException si le composant vaut null
 *  @param donne le composant a attacher
 */
    public void attach(Component component) {

        Attachedto.add(
                Objects.requireNonNull(component, "The component is null"));

    }
    
/**
 *  retourne la valeur stockée à l'adresse donnée si au moins un des composants attaché au bus 
 *  possède une valeur à cette adresse, ou FF16 sinon 
 *  @param address  l'addresse où il y a les valeurs a retourner
 *  @throws IllegalArgumentException si l'adresse n'est pas une valeur 16 bits
 *  
 */
    public int read(int address) 
    {
        
         address = Preconditions.checkBits16(address);
        for (Component c : Attachedto) {
            if (c.read(address) != Component.NO_DATA ) {

                return c.read(address);
            }
        }

        return  ADDRESS_MAX8;

    }
    
    /**
     *  qui écrit la valeur à l'adresse donnée dans tous les composants connectés au bus
     *  
     *  @param address addresse à laquelle la nouvelle valeur sera écrite
     *  @param data valeur a inscrire à l'addresse
     *  @throws IllegalArgumentException si l'adresse n'est pas une valeur 
     *  16 bits ou si la donnée n'est pas une valeur 8 bits.
     *  
     */

    public void write(int address, int data) {

        address= Preconditions.checkBits16(address);
        data = Preconditions.checkBits8(data);

        for (Component c : Attachedto) {
            c.write(address, data);
        }
    }
}
