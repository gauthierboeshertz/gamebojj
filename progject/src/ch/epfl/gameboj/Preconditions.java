package ch.epfl.gameboj;

/**
 * @author Alvaro Cauderan ( 282186)
 * @author Gauthier Boeshertz (283192) Interface qui recueille des tests.
 *
 */

public interface Preconditions {
    final  static int MAX8 = 255;
    final  static int MAX16 = 65535;

    /**
     * lève l'exception IllegalArgumentException si son argument est faux, et ne
     * fait rien sinon.
     * 
     * @param r
     *            boolean dont on teste la valeur
     * 
     * @throws l'exception
     *             IllegalArgumentException l'argument est faux
     * 
     */
    public static void checkArgument(boolean r) {
        if (r == false) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Retourne l'argument si celui ci est une valeur infèrieure à huit bit
     * sinon lève l'exception IllegalArgumentException
     * 
     * @return l'argument
     * 
     * @param v
     *            entier dont on teste la valeur
     * 
     * @throws l'exception
     *             IllegalArgumentException l'argument est un valeur supérieure
     *             ou égale à 255 ou si elle est négative
     */
    public static int checkBits8(int v) {
       checkArgument(v>=0 && v<= MAX8);
            return v;
    }

    /**
     * Retourne l'argument si celui ci est une valeur infèrieure à 16 bit sinon
     * lève l'exception IllegalArgumentException
     * 
     * @retourne l'argument
     * 
     * @param v
     *            entier dont on teste la valeur
     * 
     * @throws l'exception
     *             IllegalArgumentException l'argument n'est pas une valeur de
     *             16 bits
     */
    public static int checkBits16(int v) {
        checkArgument(v>=0 && v<= MAX16);

            return v;
    }

  

}
