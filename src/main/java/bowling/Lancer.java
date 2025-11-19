package bowling;

/**
 * Représente un lancer de boule au bowling
 */
public class Lancer {
    private final int quillesAbattues;

    /**
     * Constructeur d'un lancer
     * @param quillesAbattues nombre de quilles abattues lors de ce lancer
     */
    public Lancer(int quillesAbattues) {
        if (quillesAbattues < 0 || quillesAbattues > 10) {
            throw new IllegalArgumentException("Le nombre de quilles abattues doit être entre 0 et 10");
        }
        this.quillesAbattues = quillesAbattues;
    }

    /**
     * @return le nombre de quilles abattues lors de ce lancer
     */
    public int getQuillesAbattues() {
        return quillesAbattues;
    }

    /**
     * @return true si ce lancer est un strike (10 quilles abattues)
     */
    public boolean estStrike() {
        return quillesAbattues == 10;
    }
}
