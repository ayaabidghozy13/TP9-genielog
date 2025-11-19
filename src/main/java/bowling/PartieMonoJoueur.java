package bowling;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe a pour but d'enregistrer le nombre de quilles abattues lors des
 * lancers successifs d'<b>un seul et même</b> joueur, et de calculer le score
 * final de ce joueur.
 */
public class PartieMonoJoueur {

    private final String nomJoueur; // Ajout
    private final List<Tour> tours;
    private final CalculateurScore calculateurScore;
    private Tour tourCourant;

    /**
     * Constructeur
     */
    public PartieMonoJoueur(String nom) { // Ajout du nom
        this.nomJoueur = nom;
        this.tours = new ArrayList<>();
        this.calculateurScore = new CalculateurScore();
        this.tourCourant = new Tour(1);
    }
    
    /**
     * Constructeur par défaut (pour les tests unitaires existants)
     */
    public PartieMonoJoueur() {
        this("Joueur Inconnu");
    }

    /**
     * Cette méthode doit être appelée à chaque lancer de boule
     *
     * @param nombreDeQuillesAbattues le nombre de quilles abattues lors de ce lancer
     * @throws IllegalStateException si la partie est terminée ou le tour est invalide.
     */
    public void enregistrerLancer(int nombreDeQuillesAbattues) {
        if (estTerminee()) {
            throw new IllegalStateException("La partie est terminée");
        }

        Lancer lancer = new Lancer(nombreDeQuillesAbattues);
        boolean tourContinue = tourCourant.ajouterLancer(lancer);

        // Si le tour est terminé, l'ajouter à la liste et passer au suivant
        if (tourCourant.estTermine()) {
            tours.add(tourCourant);
            
            // Créer le tour suivant si on est avant le 10ème tour
            if (tours.size() < 10) {
                tourCourant = new Tour(tours.size() + 1);
            } else {
                // Après avoir complété le 10ème tour, la partie est finie
                tourCourant = null; 
            }
        }
    }

    /**
     * Cette méthode donne le score du joueur.
     * Si la partie n'est pas terminée, on considère que les lancers restants
     * abattent 0 quille.
     * @return Le score du joueur
     */
    public int score() {
        // Créer une copie des tours pour le calcul
        List<Tour> toursComplets = new ArrayList<>(tours);
        
        // Simuler la fin du jeu pour le calcul du score
        if (!estTerminee()) {
            // 1. Compléter le tour courant avec des 0
            if (tourCourant != null) {
                Tour tourSimule = simulerFinTour(tourCourant);
                toursComplets.add(tourSimule);
            }
            
            // 2. Ajouter les tours restants (jusqu'à 10) avec des lancers à 0
            for (int i = toursComplets.size(); i < 10; i++) {
                Tour tourVide = new Tour(i + 1);
                tourVide.ajouterLancer(new Lancer(0));
                tourVide.ajouterLancer(new Lancer(0));
                // Pas besoin de simuler le 10e tour ici, car on ne simule que 2 lancers par tour par défaut.
                toursComplets.add(tourVide);
            }
        }

        return calculateurScore.calculerScoreTotal(toursComplets);
    }

    /**
     * Simule la fin d'un tour en cours avec des lancers à 0
     */
    private Tour simulerFinTour(Tour tour) {
        Tour tourSimule = new Tour(tour.getNumeroTour());
        
        // Copier les lancers existants
        for (Lancer lancer : tour.getLancers()) {
            // NOTE: On ignore le résultat de l'ajout ici, car on sait que l'ajout est valide.
            tourSimule.ajouterLancer(lancer); 
        }
        
        // Compléter avec des lancers à 0 jusqu'à ce que le tour soit terminé
        while (!tourSimule.estTermine()) {
            try {
               tourSimule.ajouterLancer(new Lancer(0));
            } catch (IllegalArgumentException e) {
                // Peut arriver si le 10e tour est un spare au 2e lancer avec un 0 au 3e
                // Dans le cas de la simulation, on suppose le lancer valide (quilles restantes = 10)
                // On garde juste le 0, mais l'erreur est dans la logique de tour
            }
        }
        
        return tourSimule;
    }

    /**
     * @return vrai si la partie est terminée pour ce joueur, faux sinon
     */
    public boolean estTerminee() {
        return tours.size() == 10 && tourCourant == null;
    }

    /**
     * @return Le numéro du tour courant [1..10], ou 0 si le jeu est fini
     */
    public int getNumeroTourCourant() { // Changement du nom pour être cohérent avec l'usage
        if (estTerminee()) {
            return 0;
        }
        return tourCourant != null ? tourCourant.getNumeroTour() : 0;
    }

    /**
     * @return Le numéro du prochain lancer pour tour courant [1..3], ou 0 si le jeu est fini
     */
    public int getNumeroBouleCourante() { // Changement du nom pour être cohérent avec l'usage
        if (estTerminee()) {
            return 0;
        }
        return tourCourant != null ? tourCourant.getNombreLancers() + 1 : 0;
    }

    /**
     * @return Le nom du joueur
     */
    public String getNomJoueur() {
        return nomJoueur;
    }
    
    /**
     * @return true si le joueur doit lancer à nouveau pour compléter le tour en cours.
     */
    public boolean doitRelancer() {
        return tourCourant != null && tourCourant.doitRelancer();
    }
    
    /**
     * @return true si le tour courant est terminé (et qu'on doit passer au joueur suivant), 
     * false si le joueur doit relancer pour compléter le tour.
     */
    public boolean aTermineSonTour() {
        // Le tour est terminé s'il est dans la liste (donc terminé) OU si le jeu est fini
        // On vérifie si le dernier tour ajouté est le tour qui vient d'être complété
        // Le cas le plus simple : si le tour courant est null, c'est que le dernier tour vient d'être ajouté.
        if (estTerminee()) {
            return true;
        }
        
        // Si le tour n'est PAS null et qu'il n'est PAS terminé, alors le joueur doit relancer
        if (tourCourant != null && !tourCourant.estTermine()) {
            return false;
        }
        
        // Le tour courant est terminé s'il est dans la liste des tours ou si tourCourant est null
        return tourCourant == null || tours.contains(tourCourant);
    }
}
