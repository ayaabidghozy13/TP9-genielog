package bowling;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implémentation de l'interface IPartieMultiJoueurs pour gérer une partie de bowling multi-joueurs.
 */
public class PartieMultiJoueurs implements IPartieMultiJoueurs {

    private Map<String, PartieMonoJoueur> partiesParJoueur;
    private List<String> nomsDesJoueurs;
    private int indexJoueurCourant = -1;
    private boolean partieDemarree = false;

    /**
     * Démarre une nouvelle partie pour un groupe de joueurs.
     * @param nomsDesJoueurs un tableau des noms de joueurs (il faut au moins un joueur)
     * @return une chaîne de caractères indiquant le prochain joueur.
     * @throws java.lang.IllegalArgumentException si le tableau est vide ou null
     */
    @Override
    public String demarreNouvellePartie(String[] nomsDesJoueurs) throws IllegalArgumentException {
        if (nomsDesJoueurs == null || nomsDesJoueurs.length == 0) {
            throw new IllegalArgumentException("Le tableau de joueurs ne doit pas être vide.");
        }
        
        this.nomsDesJoueurs = Arrays.asList(nomsDesJoueurs);
        this.partiesParJoueur = new LinkedHashMap<>();
        
        for (String nom : nomsDesJoueurs) {
            // Utilisation du constructeur avec nom
            partiesParJoueur.put(nom, new PartieMonoJoueur(nom)); 
        }
        
        indexJoueurCourant = 0;
        partieDemarree = true;
        
        return messageProchainTir();
    }

    /**
     * Enregistre le nombre de quilles abattues pour le joueur courant.
     * @param nombreDeQuillesAbattues : nombre de quilles abattue à ce lancer
     * @return une chaîne de caractères indiquant le prochain joueur, ou "Partie terminée".
     * @throws java.lang.IllegalStateException si la partie n'est pas démarrée.
     * @throws java.lang.IllegalArgumentException si le nombre de quilles est invalide.
     */
    @Override
    public String enregistreLancer(int nombreDeQuillesAbattues) throws IllegalStateException {
        if (!partieDemarree) {
            throw new IllegalStateException("La partie n'a pas été démarrée.");
        }
        
        PartieMonoJoueur partieCourante = getPartieCourante();
        
        // Enregistre le lancer dans la partie mono-joueur
        // Note: cela peut lancer IllegalArgumentException si le lancer est invalide.
        partieCourante.enregistrerLancer(nombreDeQuillesAbattues);

        // Si la partie est terminée pour tous les joueurs, on retourne le message de fin.
        if (estPartieTerminee()) {
            return "Partie terminée";
        }
        
        // LOGIQUE CORRIGÉE : Le joueur change si son tour (frame) est terminé.
        if (!partieCourante.doitRelancer()) { 
            passerAuJoueurSuivant();
        }

        return messageProchainTir();
    }

    /**
     * Donne le score pour le joueur playerName
     * @param nomDuJoueur le nom du joueur recherché
     * @return le score pour ce joueur
     * @throws IllegalArgumentException si nomDuJoueur ne joue pas dans cette partie
     */
    @Override
    public int scorePour(String nomDuJoueur) throws IllegalArgumentException {
        PartieMonoJoueur partie = partiesParJoueur.get(nomDuJoueur);
        if (partie == null) {
            throw new IllegalArgumentException("Joueur inconnu: " + nomDuJoueur);
        }
        return partie.score();
    }
    
    /**
     * Construit le message d'état du prochain tir.
     * @return Le message formaté.
     */
    private String messageProchainTir() {
        PartieMonoJoueur partie = getPartieCourante();
        return String.format("Prochain tir : joueur %s, tour n° %d, boule n° %d",
                             partie.getNomJoueur(),
                             partie.getNumeroTourCourant(),
                             partie.getNumeroBouleCourante());
    }

    /**
     * Obtient la partie mono-joueur courante.
     * @return La PartieMonoJoueur.
     */
    private PartieMonoJoueur getPartieCourante() {
        return partiesParJoueur.get(nomsDesJoueurs.get(indexJoueurCourant));
    }
    
    /**
     * Passe à l'index du joueur suivant.
     */
    private void passerAuJoueurSuivant() {
        indexJoueurCourant = (indexJoueurCourant + 1) % nomsDesJoueurs.size();
    }
    
    /**
     * Vérifie si tous les joueurs ont terminé leur partie.
     * @return true si tous les joueurs ont terminé.
     */
    private boolean estPartieTerminee() {
        for (PartieMonoJoueur partie : partiesParJoueur.values()) {
            if (!partie.estTerminee()) {
                return false;
            }
        }
        return true;
    }
}
