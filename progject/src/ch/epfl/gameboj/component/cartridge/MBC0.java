package ch.epfl.gameboj.component.cartridge;

import java.lang.ModuleLayer.Controller;
import java.util.Objects;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

/**
 * @author Alvaro Cauderan ( 282186)
 * @author Gauthier Boeshertz (283192)
 *
 *         représente un contrôleur de banque mémoire de type 0, c-à-d doté
 *         uniquement d'une mémoire morte de 32 768 octets
 */
public final class MBC0 implements Component {

    private Rom controller;
    private final int ROM_SIZE = 32768;

    /**
     * Construit un controleur de type 0
     * 
     * @param rom
     * @throws NullPointerException
     *             si la rom donnée est nulle
     * @throws IllegalArgumentException
     *             si la rom donnée n'a pas une taille de 32768
     */
    public MBC0(Rom rom) {

Objects.requireNonNull(rom);
Preconditions.checkArgument(rom.size() == ROM_SIZE);
        
            controller = rom;

        

    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.gameboj.component.Component#read(int)
     */
    @Override
    public int read(int address) {
        if(address>Component.maxAddress || address<0) {
            throw new IllegalArgumentException();
        }
        if( address >=ROM_SIZE) {
            return NO_DATA;
        }
       
        int read = controller.read(address);
        return read;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.gameboj.component.Component#read(int)
     */
    @Override
    public void write(int address, int data) {
    }

}