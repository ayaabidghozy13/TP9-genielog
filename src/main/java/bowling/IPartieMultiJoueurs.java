package bowling;

public interface IPartieMultiJoueurs {

    /**
     * Démarre une nouvelle partie à plusieurs joueurs
     * @param nomsDesJoueurs tableau contenant le nom des joueurs
     * @return message indiquant le prochain tir
     */
    String demarreNouvellePartie(String[] nomsDesJoueurs);

    /**
     * Enregistre un lancer pour le joueur courant
     * @param quillesAbattues nombre de quilles abattues
     * @return message indiquant le prochain tir, ou "Partie terminée"
     */
    String enregistreLancer(int quillesAbattues);

    /**
     * Retourne le score du joueur
     * @param joueur nom du joueur
     * @return score final
     */
    int scorePour(String joueur);
}

