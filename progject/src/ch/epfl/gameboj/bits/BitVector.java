
package ch.epfl.gameboj.bits;

import java.util.Arrays;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.cpu.Alu;
import ch.epfl.gameboj.component.cpu.Alu.RotDir;

/**
 * @author Alvaro Cauderan ( 282186)
 * @author Gauthier Boeshertz (283192)
 *
 *         Représente un vecteur de bits dont la taille doit etre un multiple de
 *         32
 */

public class BitVector {

    private int size;
    private int[] vector;

    /**
     * Construit un vecteur de bit dont les valeurs sont 1 ou 0
     * 
     * @param size
     *            taille du vecteur
     * 
     * @param state
     *            donne la valeur des bits
     * @throws IllegalArgumentException
     *             si la taille n'est pas un multiple de 32
     */
    public BitVector(int size, boolean state) {

        checkSize(size);

        this.size = size;

        vector = new int[size / 32];

        for (int i = 0; i < size / 32; i++) {

            vector[i] = state ? 0b11111111111111111111111111111111 : 0;

        }

    }

    private BitVector(int[] tableau) {

        vector = tableau;
        this.size = tableau.length * 32;

    }

    /**
     * Construit un vecteur de bits ou tous les bits valent 0
     * 
     * @param size
     *            taille du vecteur
     * 
     * @throws IllagalArgumentException
     *             si la taille n'est pas un multiple de 32
     */

    public BitVector(int size) {

        checkSize(size);

        this.size = size;

        vector = new int[size / 32];

        for (int i = 0; i < size / 32; i++) {

            vector[i] = 0;

        }

    }

    /**
     * retourne la taille du vecteur
     * 
     * @return la taille du vecteur
     */
    public int size() {
        return size;
    }

    /**
     * teste le bit d'index donné
     * 
     * @param index
     *            donne l'index du bit à tester
     * @return true si le bit vaut 1 et false sinon
     * @throws IllegalArgumentException
     *             si l'index n'est pas positif ou s'il est plus grand que la
     *             taille
     */

    public boolean testBit(int index) {

        Preconditions.checkArgument(!(index < 0 || index >= size()));

        int block = (index / 32);

        return Bits.test(vector[block], (index - block * 32))

        ;

    }

    /**
     * retourne le complément du vecteur
     * 
     * @return le complément du vecteur
     */
    public BitVector not() {

        int[] newVector = new int[vector.length];

        for (int i = 0; i < vector.length; i++) {

            newVector[i] = ~vector[i];

        }

        return new BitVector(newVector);
    }

    /**
     * retourne une vecteur fait par l'opération "et" bit à bit avec le vecteur
     * donné et le vecteur
     * 
     * @param vector
     *            vecteur avec lequel l'opération "et" est faite avec le vecteur
     *            initial
     * @return le vecteur créé
     * @throws IllegalArgumentException
     *             si les deux vecteurs ne sont pas de taille égales
     */
    public BitVector and(BitVector vector) {

        if (this.vector.length != vector.vector.length) {

            throw new IllegalArgumentException();
        }

        int[] newVector = new int[this.vector.length];

        for (int i = 0; i < this.vector.length; i++) {

            newVector[i] = vector.vector[i] & this.vector[i];

        }

        return new BitVector(newVector);
    }

    /**
     * retourne une vecteur fait par l'opération "ou" bit à bit avec le vecteur
     * donné et le vecteur
     * 
     * @param vector
     *            vecteur avec lequel l'opération "ou" est faite avec le vecteur
     *            initial
     * @return le vecteur créé
     * @throws IllegalArgumentException
     *             si les deux vecteurs ne sont pas de taille égales
     */
    public BitVector or(BitVector vector) {

        if (this.vector.length != vector.vector.length) {

            throw new IllegalArgumentException();
        }

        int[] newVector = new int[this.vector.length];

        for (int i = 0; i < this.vector.length; i++) {

            newVector[i] = this.vector[i] | vector.vector[i];

        }

        return new BitVector(newVector);
    }

    /**
     * extrait un vecteur de taille donnée de l'extension par 0 du vecteur
     * 
     * @param index
     *            donne l'index
     * @param size
     *            donne la taille du vecteur à créer
     * @return le vecteur extrait
     * @throws IllegalArgumentException
     *             si la taille n'est pas un multiple de 32
     */
    public BitVector extractZeroExtended(int index, int size) {

        checkSize(size);

        int[] a = new int[size / 32];

        int startBlock = Math.floorDiv(index, 32);

        int startIndex = Math.floorMod(index, 32);

        for (int i = 0; i < size / 32; i++) {

            boolean outOfBoundsB1 = (startBlock + i) < 0
                    | (startBlock + i) > (vector.length - 1);

            boolean outOfBoundsB2 = (startBlock + i + 1) < 0 | startIndex == 0
                    | (startBlock + i + 1) > (vector.length - 1);

            int firstBlock = outOfBoundsB1 ? 0b0
                    : vector[startBlock + i] >>> startIndex;

            int SecondBlock = outOfBoundsB2 ? 0b0
                    : vector[startBlock + i + 1] << 32 - startIndex;

            a[i] = firstBlock | SecondBlock;

        }
        return new BitVector(a);
    }

    /**
     * extrai tun vecteur de taille donnée de l'extension par enroulement du
     * vecteur
     * 
     * @param index
     *            donne l'index du début de l'extraction
     * @param size
     *            donne la taille du vecteur à créer
     * @return le vecteur extrait
     * @throws IllegalArgumentException
     *             si la taille n'est pas un multiple de 32
     */
    public BitVector extractWrapped(int index, int size) {

        checkSize(size);

        int[] a = new int[size / 32];
        int startBlock = Math.floorDiv(index, 32);
        int startIndex = Math.floorMod(index, 32);

        for (int i = 0; i < size / 32; i++) {

            boolean outOfBoundsB1L = (startBlock + i) < 0;
            boolean outOfBoundsB2L = (startBlock + i + 1) > (vector.length - 1);

            int firstBlock = outOfBoundsB1L
                    ? vector[Math.floorMod(startBlock + i,
                            vector.length)] >>> startIndex
                    : vector[(startBlock + i) % vector.length] >>> startIndex;

            int SecondBlock = outOfBoundsB2L
                    ? vector[Math.floorMod(startBlock + i + 1,
                            vector.length)] << 32 - startIndex
                    : vector[(startBlock + i + 1) % vector.length] << 32
                            - startIndex;

            SecondBlock = (startIndex == 0) ? 0 : SecondBlock;

            a[i] = firstBlock | SecondBlock;

        }
        return new BitVector(a);

    }

    /**
     * Décale le vecteur d'une distance donnée
     * 
     * @param shift
     *            donne la distance
     * @return un nouveau vecteur
     */
    public BitVector shift(int shift) {

        return extractZeroExtended(-shift, size());

    }

    /**
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuilder a = new StringBuilder();

        for (int i = 0; i < size(); i++) {

            a.append(testBit(i) ? 1 : 0);
        }

        return a.reverse().toString();

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BitVector other = (BitVector) obj;
        if (size != other.size)
            return false;
        if (!Arrays.equals(vector, other.vector))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + size;
        result = prime * result + Arrays.hashCode(vector);
        return result;
    }

    /**
     * /**
     * 
     * @author Alvaro Cauderan ( 282186)
     * @author Gauthier Boeshertz (283192) Classe utilisée pour construire des
     *         vecteurs
     */

    public final static class Builder {
        private int[] toBuild;
        private int size;

        /**
         * construit un builder de vecteur
         * 
         * @param size
         *            donne la taille du vecteur à construire donc la taille du
         *            builder
         * @throws IllegalArgumentException
         *             si la taille n'est pas un multiple de 32
         */
        public Builder(int size) {
            checkSize(size);

            this.size = size / 32;
            toBuild = new int[this.size];
            Arrays.fill(toBuild, 0);
        }

        /**
         * Définit la valeur d'un octet désigné par son index
         * 
         * @param index
         *            donne l'index de l'octet à changer
         * @param octet
         *            donne l'octet à mettre
         * @return le builder pour pouvoir enchainer les fonctions
         * @throws IllegalArgumentException
         *             si la valeur passée en argument n'est pas une valeur de 8
         *             bits ou si l'index est négatif ou s'il est supérieur
         * @throws IllegalStateException
         *             si la fonction build a été appelée avant
         */
        public Builder setByte(int index, int octet) {

            Preconditions.checkBits8(octet);
            Preconditions.checkArgument(index >= 0 && index < size * 32);

            if (toBuild == null) {
                throw new IllegalStateException();
            }

            if (index > toBuild.length * Integer.SIZE / Byte.SIZE
                    || index < 0) {
                throw new IllegalArgumentException();
            }

            int shift = Math.floorMod(Byte.SIZE * index, Integer.SIZE);

            int tab = Math.floorDiv(Byte.SIZE * index, Integer.SIZE);

            toBuild[tab] &= ~(0b1111_1111 << shift);
            toBuild[tab] |= octet << Math.floorMod(Byte.SIZE * index,
                    Integer.SIZE);

            return this;
        }

        /*
         * public Builder setInt(int index, int value) { if (toBuild == null)
         * throw new IllegalStateException(); //
         * Preconditions.checkIndex(vector.length); toBuild[index] = value;
         * return this; }
         */
        /**
         * Construit un vecteur de bit à partir du tableau
         * 
         * @return
         */
        public BitVector build() {

            if (toBuild == null) {
                throw new NullPointerException();
            }

            int[] copy = toBuild;
            toBuild = null;

            return new BitVector(copy);
        }

    }

    public static void checkSize(int size) {
        if (size % 32 != 0 || size < 0) {
            throw new IllegalArgumentException();

        }

    }

    public static BitVector rand() {
        int[] val = { -1, -8, 2839, 7 };
        return new BitVector(val);
    }

}
