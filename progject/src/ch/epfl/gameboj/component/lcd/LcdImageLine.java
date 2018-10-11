package ch.epfl.gameboj.component.lcd;

import java.util.Objects;

import ch.epfl.gameboj.Preconditions;

import ch.epfl.gameboj.bits.BitVector;

import ch.epfl.gameboj.bits.BitVector.Builder;

/**
 * @author Alvaro Cauderan ( 282186)
 * @author Gauthier Boeshertz (283192)
 *
 *         Représente une ligne de l'écran
 */
public class LcdImageLine {

    private final BitVector msb;

    private final BitVector lsb;

    private final BitVector opacity;

    /**
     * Construit une ligne avec trois vecteurs de bits
     * 
     * @param msb
     *            donne le vecteur de bits de poids fort
     * @param lsb
     *            donne le vecteur de bits de poids faible
     * @param opacity
     *            donne le vecteur d'opacité
     * 
     * @throws IllegalArgumentException
     *             si la taille des trois vecteurs n'est pas égale
     */
    public LcdImageLine(BitVector msb, BitVector lsb, BitVector opacity) {
        Preconditions.checkArgument(
                msb.size() == lsb.size() || msb.size() == opacity.size());

        this.msb = msb;

        this.lsb = lsb;

        this.opacity = opacity;

    }

    /**
     * donne la taille de la ligne
     * 
     * @return la taille du vecteur de msb puisque tous les vecteurs ont la meme
     *         taille
     */
    public int size() {

        return msb.size();

    }

    /**
     * donne le vecteur de bits de poids forts
     * 
     * @return le vecteur de msb
     */
    public BitVector msb() {

        return msb;

    }

    /**
     * donne le vecteur de bits de poids faible
     * 
     * @return le vecteur de lsb
     */
    public BitVector lsb() {

        return lsb;

    }

    /**
     * donne le vecteur d'opacité
     * 
     * @return le vecteur d'opacité
     */
    public BitVector opacity() {

        return opacity;

    }

    /**
     * décale la ligne d'un nombre de bits donné
     * 
     * @param shift
     *            donne le nombre de bits
     * @return une nouvelle ligne qui est l'ancienne décalée
     */
    public LcdImageLine shift(int shift) {

        BitVector msbb = msb.shift(shift);

        BitVector lsbb = lsb.shift(shift);

        BitVector opacityy = opacity.shift(shift);

        return new LcdImageLine(msbb, lsbb, opacityy);

    }

    /**
     * extrait une ligne d'une taille donnée et depuis un index donné depuis
     * l'ancienne par enroulement
     * 
     * @param x
     *            donne l'index de dpépart de l'extraction
     * @param size
     *            la taille de la nouvelle ligne
     * @return la ligne extraite
     * 
     * @throws IllegalArgumentException
     *             si la taille n'est pas un multiple de 32 ou si la taile est
     *             négative
     */
    public LcdImageLine extract(int x, int size) {

        Preconditions.checkArgument(size % 32 == 0 && size > 0);

        BitVector msbb = msb.extractWrapped(x, size);

        BitVector lsbb = lsb.extractWrapped(x, size);

        BitVector opacityy = opacity.extractWrapped(x, size);

        return new LcdImageLine(msbb, lsbb, opacityy);

    }

    /**
     * 
     * @param colors
     * @return
     */
    public LcdImageLine mapColors(int colors) {

        Preconditions.checkBits8(colors);

        if (colors == 0b11100100) {

            return this;

        }

        BitVector msbb = this.msb;

        BitVector lsbb = this.lsb;

        BitVector mask = null;

        for (int i = 0; i < 4; i++) {

            int color = colors >> 2 * i & 0b0000000000000000000000000000_0011;

            switch (i) {

            case 0:

                mask = msb().or(lsb()).not();

                break;

            case 1:

                mask = lsb().and(msb().not());

                break;

            case 2:

                mask = lsb().not().and(msb());

                break;

            case 3:

                mask = msb().and(lsb());

                break;

            }

            lsbb = (color % 2 == 0) ? lsbb.and(mask.not()) : lsbb.or(mask);

            msbb = (color / 2 < 1) ? msbb.and(mask.not()) : msbb.or(mask);

        }

        return new LcdImageLine(msbb, lsbb, opacity());

    }

    /**
     * créee une ligne en en mettant une en dessous d'une autre
     * 
     * @param lineUp
     *            ligne a mettre sur la ligne actuelle
     * @return la ligne créee
     * 
     * @throws IllegalArgumentException
     *             si les deux lignes n'ont pas la même taille
     * @throws NullPointerException
     *             si la ligne en argument est nulle
     */
    public LcdImageLine below(LcdImageLine lineUp) {

        Preconditions.checkArgument(size() == lineUp.size());
        Objects.requireNonNull(lineUp);

        BitVector msbb = (msb.and(lineUp.opacity.not()))

                .or(lineUp.msb.and(lineUp.opacity));

        BitVector lsbb = (lsb.and(lineUp.opacity.not()))

                .or(lineUp.lsb.and(lineUp.opacity));

        BitVector opacityy = opacity.or(lineUp.opacity);

        return new LcdImageLine(msbb, lsbb, opacityy);

    }

    /**
     * créee une ligne en en mettant une en dessous d'une autre mais aveec un
     * vecteur d'opacié passé en argument
     * 
     * @param lineUp
     *            ligne a mettre sur la ligne actuelle
     * @return la ligne créee
     * 
     * @throws IllegalArgumentException
     *             si les deux lignes et le vecteur d'opacité n'ont pas la même
     *             taille
     * @throws NullPointerException
     *             si la ligne en argument est nulle ou si le vecteur d'opacité
     *             est nul.
     */
    public LcdImageLine below(LcdImageLine line, BitVector newOp) {

        Objects.requireNonNull(line);
        Objects.requireNonNull(newOp);

        Preconditions.checkArgument(size() == line.size());

        Preconditions.checkArgument(size() == newOp.size());

        BitVector msbb = (line.msb.and(newOp)).or(msb.and(newOp.not()));

        BitVector lsbb = (line.lsb.and(newOp)).or(lsb.and(newOp.not()));

        BitVector opacityy = opacity.or(newOp);

        return new LcdImageLine(msbb, lsbb, opacityy);

    }

    /**
     * créee une ligne en joignant la ligne actuelle avec une ligne passée en
     * argument à partir d'un index donné
     * 
     * @param line
     *            la ligne a joindre
     * @param index
     *            l'index à partir duquel la jointure se fait
     * @return la ligne créee
     * @throws IllegalArgumentException
     *             si les deux lignes n'ont pas la même taille ou si l'index est
     *             inférieur à 0 ou si l'index est supérieur à la taille de la
     *             ligne
     *             
     * @throws NullPointerException si la ligne est nulle
     */
    public LcdImageLine join(LcdImageLine line, int index) {
        Preconditions.checkArgument(size() == line.size());
        Preconditions.checkArgument(!(line == null));
        Preconditions.checkArgument(index >= 0 && index < size());

        BitVector msb1 = msb.shift(-index + size()).shift(-size() + index);

        BitVector msb2 = line.msb.shift(-index).shift(index);

        BitVector lsb1 = lsb.shift(-index + size()).shift(-size() + index);

        BitVector lsb2 = line.lsb.shift(-index).shift(+index);

        return new LcdImageLine(msb1.or(msb2), lsb1.or(lsb2),

                line.opacity().or(opacity()));

    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override

    public int hashCode() {

        final int prime = 31;

        int result = 1;

        result = prime * result + ((lsb == null) ? 0 : lsb.hashCode());

        result = prime * result + ((msb == null) ? 0 : msb.hashCode());

        result = prime * result + ((opacity == null) ? 0 : opacity.hashCode());

        return result;

    }

    /* (non-Javadoc)
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

        LcdImageLine other = (LcdImageLine) obj;

        if (lsb == null) {

            if (other.lsb != null)

                return false;

        } else if (!lsb.equals(other.lsb))

            return false;

        if (msb == null) {

            if (other.msb != null)

                return false;

        } else if (!msb.equals(other.msb))

            return false;

        if (opacity == null) {

            if (other.opacity != null)

                return false;

        } else if (!opacity.equals(other.opacity))

            return false;

        return true;

    }
    /**
    *@author Alvaro Cauderan ( 282186)
    *@author Gauthier Boeshertz (283192)
    * Fait le builder d'une ligne
    */
    public static final class Builder {

        private BitVector.Builder msbBuilder;

        private BitVector.Builder lsbBuilder;

        private boolean canBuild;
/**
 * Construit un builder  d'une taille donéne
 * @param size donne la taille de la ligne à construire 
 * 
 * @throws Si la taille est négative ou si elle n'est pas multiple de 32
 */
        public Builder(int size) {

            Preconditions.checkArgument(size % 32 == 0 && size > 0);

            msbBuilder = new BitVector.Builder(size);

            lsbBuilder = new BitVector.Builder(size);
            canBuild = true;

        }
        /**
         * Donne une valeur à l'index donnée au vecteur de lsb et de msb  de la ligne
         * @param index l'index ou on met les valeurs 
         * @param octetmsb la valeur à mettre sur le vecteur de msb
         * @param octetlsb la valeur à mettre sur le vecteur de lsb
         * @return this pour pouvoir enchainer les actions sur le builder
         * 
         * @throws IllegalStateException si un build a deja été fait sur ce builder
         */

        public Builder setBytes(int index, int octetmsb, int octetlsb) {
            
            if (!canBuild) {
                throw new IllegalStateException();
            }
            msbBuilder.setByte(index, octetmsb);
            lsbBuilder.setByte(index, octetlsb);
            return this;

        }
/**
 * contruit la ligne 
 * @return la ligne construite à partir du builder
 * 
 * @throws 
 */
        public LcdImageLine build() {
            if (!canBuild) {
                throw new IllegalStateException();
            }
            canBuild = false;
            BitVector msb = msbBuilder.build();
            BitVector lsb = lsbBuilder.build();
            BitVector opacity = msb.or(lsb);
            return new LcdImageLine(msb, lsb, opacity);
        }

    }

}