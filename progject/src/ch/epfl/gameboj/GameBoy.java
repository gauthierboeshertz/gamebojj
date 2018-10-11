package ch.epfl.gameboj;

import java.util.Objects;

import ch.epfl.gameboj.component.Joypad;
import ch.epfl.gameboj.component.Timer;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.lcd.LcdController;
import ch.epfl.gameboj.component.memory.BootRomController;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;
import ch.epfl.gameboj.component.memory.Rom;

/**
 * @author Alvaro Cauderan ( 282186)
 * @author Gauthier Boeshertz (283192)
 *
 *         Classe qui lance tout ce qui faut pour faire fonctionner la gameboj,
 *         en attachant les différents composants à un bus commun
 * 
 */
public final  class GameBoy {
    
    private final Bus bus = new Bus();
    private  final Ram workRAM;
    private final Cpu cpu;
    private final  RamController workControl;
    private final RamController workCopy;
    private final BootRomController controller;
    private long cycles = 0;
    private final Timer timer;
    private final LcdController lcdControl;
    private final Joypad joypad;
    private static final long CYCLES_PER_SECOND = (long) Math.pow(2,20);
    public static final double CYCLES_PER_NANOSECOND = CYCLES_PER_SECOND / 1e9; //*Math.pow(10, -9);
   
    /**
     * Construit une gameboy
     * 
     * @param cartridge
     *            représente la cartouche
     * @throws NullPointerException
     *             si la cartouche est nulle
     */
    public GameBoy(Cartridge cartridge) {

       Objects.requireNonNull(cartridge);

        cpu = new Cpu();
        timer = new Timer(cpu);
        controller = new BootRomController(cartridge);
        workRAM = new Ram(AddressMap.WORK_RAM_SIZE);
        workControl = new RamController(workRAM, AddressMap.WORK_RAM_START,
                AddressMap.WORK_RAM_END);
        workCopy = new RamController(workRAM, AddressMap.ECHO_RAM_START,
                AddressMap.ECHO_RAM_END);
        lcdControl = new LcdController(cpu);
       joypad= new Joypad(cpu);
        

        workCopy.attachTo(bus);
        controller.attachTo(bus);
        timer.attachTo(bus);
        cpu.attachTo(bus);
       joypad.attachTo(bus);
        lcdControl.attachTo(bus);
        
        workControl.attachTo(bus);

    }

    /**
     * Retourne le bus
     * 
     * @return le bus
     */
    public Bus bus() {
        return bus;
    }

    /**
     * Retourne le cpu
     * 
     * @return le cpu
     */
    public Cpu cpu() {
        return cpu;
    }

    /**
     * retourne le nombre de cycle effectué
     * 
     * @return cycles
     */
    public long cycles() {
        return cycles;
    }
    public Joypad joypad() {
        return joypad;
    }

    /**
     * Simule le fonctionnement de la gameboy, en incrémentant les cycles
     * 
     * @param cycle
     *            le nombre de cycle que la gameboy doit effectuer
     * @throws IllegalArgumentException
     *             si le cycle actuel de la gameboy est plus grand que le nombre
     *             de cycle que la gameboy doit aire
     */
    public void runUntil(long cycle) {

        Preconditions.checkArgument(cycles() <= cycle);

        while (cycles() < cycle) {

           
            timer.cycle(cycles);
            
            lcdControl.cycle(cycles);
            cpu.cycle(cycles);
            ++cycles;
        }
    }

    /**
     * Retourne le timer
     * 
     * @return timer
     */
    public Timer getTimer() {
        return timer;
        
    }

    /**
     * retourne le controlleur du lcd
     * 
     * @return lcdControl
     */
    public LcdController getLcdController() {
        return lcdControl;
    }
}