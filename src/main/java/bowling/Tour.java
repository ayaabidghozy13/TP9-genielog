package bowling;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un tour de bowling
 */
public class Tour {
    private final List<Lancer> lancers;
    private final int numeroTour;
    private final boolean estDernierTour;

    /**
     * Constructeur d'un tour
     * @param numeroTour le numéro du tour (1-10)
     */
    public Tour(int numeroTour) {
        if (numeroTour < 1 || numeroTour > 10) {
            throw new IllegalArgumentException("Le numéro de tour doit être entre 1 et 10");
        }
        this.numeroTour = numeroTour;
        this.estDernierTour = (numeroTour == 10);
        this.lancers = new ArrayList<>();
    }

    /**
     * Ajoute un lancer au tour.
     * @param lancer le lancer à ajouter
     * @return true si le joueur doit lancer à nouveau pour ce tour, false sinon.
     * @throws IllegalStateException si le tour est déjà terminé.
     */
    public boolean ajouterLancer(Lancer lancer) {
        if (estTermine()) {
            throw new IllegalStateException("Le tour est déjà terminé.");
        }
        
        // La règle du bowling veut qu'on ne puisse pas dépasser 10 quilles entre le 1er et le 2e lancer
        if (lancers.size() == 1 && !estDernierTour) {
            if (lancers.get(0).getQuillesAbattues() + lancer.getQuillesAbattues() > 10) {
                 throw new IllegalArgumentException("Le total des quilles abattues dans ce tour ne peut pas dépasser 10.");
            }
        }
        
        lancers.add(lancer);

        // Si le tour est terminé après ce lancer, la réponse est false (ne doit PAS relancer)
        return !estTermine(); 
    }

    /**
     * @return true si ce tour est terminé.
     */
    public boolean estTermine() {
        if (estDernierTour) {
            // Dernier tour: terminé après 3 lancers
            if (lancers.size() == 3) {
                return true;
            }
            // Dernier tour: terminé après 2 lancers si pas de Strike ou Spare
            if (lancers.size() == 2 && !estStrike() && !estSpare()) {
                return true;
            }
            return false;
        } else {
            // Tours 1-9: terminé après Strike ou 2 lancers
            return estStrike() || lancers.size() == 2;
        }
    }

    /**
     * @return true si le joueur doit lancer à nouveau dans ce tour pour compléter sa frame.
     */
    public boolean doitRelancer() {
        return !estTermine();
    }
    
    /**
     * @return true si ce tour est un strike
     */
    public boolean estStrike() {
        // Au 10ème tour, ce n'est un strike que si c'est le premier lancer
        if (estDernierTour) {
            return lancers.size() >= 1 && lancers.get(0).estStrike();
        }
        return lancers.size() == 1 && lancers.get(0).estStrike();
    }

    /**
     * @return true si ce tour est un spare
     */
    public boolean estSpare() {
        if (lancers.size() < 2) {
            return false;
        }
        // Pour les tours 1-9 : Spare si 10 quilles en 2 lancers (et non Strike)
        if (!estDernierTour) {
             return getQuillesAbattuesTour() == 10 && !estStrike();
        }
        // Pour le tour 10 : Spare si la somme des DEUX PREMIERS lancers est 10 et que le 1er n'est pas un strike
        if (estDernierTour) {
            if(lancers.size() >= 2) {
                return (lancers.get(0).getQuillesAbattues() + lancers.get(1).getQuillesAbattues() == 10) 
                        && !lancers.get(0).estStrike();
            }
        }
        return false;
    }

    /**
     * @return le nombre total de quilles abattues dans ce tour (y compris les bonus du 10e tour)
     */
    public int getQuillesAbattuesTour() {
        return lancers.stream().mapToInt(Lancer::getQuillesAbattues).sum();
    }

    /**
     * @return la liste des lancers de ce tour
     */
    public List<Lancer> getLancers() {
        return new ArrayList<>(lancers);
    }

    /**
     * @return le numéro de ce tour
     */
    public int getNumeroTour() {
        return numeroTour;
    }

    /**
     * @return le nombre de lancers effectués dans ce tour
     */
    public int getNombreLancers() {
        return lancers.size();
    }

    /**
     * @return true si c'est le dernier tour (tour 10)
     */
    public boolean estDernierTour() {
        return estDernierTour;
    }
}
