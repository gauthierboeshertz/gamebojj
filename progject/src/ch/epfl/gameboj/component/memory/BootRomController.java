
package ch.epfl.gameboj.component.memory;

import java.util.Objects;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cartridge.Cartridge;


/**
*@author Alvaro Cauderan ( 282186)
*@author Gauthier Boeshertz (283192)
*
*représente un controleur de la mémoire mortue du démarrage
*/

public class BootRomController implements Component {

    private final Cartridge cartridge;
private    boolean active = true;
    /**
     * construit un controleur de la mémoire morte du démarrage auquel la cartouche donnée est attachée
     * @param cartridge cartouche à attacher au controleur
     * @throws NullPointerException si l'argument est nul
     */
    public BootRomController(Cartridge cartridge) {

   Objects.requireNonNull(cartridge);

        this.cartridge = cartridge;
    }

    /* (non-Javadoc)
     * @see ch.epfl.gameboj.component.Component#read(int)
     */
    @Override
    public int read(int address) {
        int readvalue = 0;
        if (address >= 0 && address <= 255 && active) {
            readvalue = Byte.toUnsignedInt(BootRom.DATA[address]);

        }

        else {
            readvalue = cartridge.read(address);
        }
        return readvalue;
    }

    /* (non-Javadoc)
     * @see ch.epfl.gameboj.component.Component#write(int, int)
     */
    @Override
    public void write(int address, int data) {
        if (address == AddressMap.REG_BOOT_ROM_DISABLE) {

            active = false;

        }

        else {
            cartridge.write(address, data);
        }

    }

}