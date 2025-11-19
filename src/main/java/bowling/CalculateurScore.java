package bowling;

import java.util.List;

/**
 * Calcule le score total d'une partie
 */
public class CalculateurScore {

    /**
     * Calcule le score total d'une partie
     * @param tours la liste des tours de la partie
     * @return le score total
     */
    public int calculerScoreTotal(List<Tour> tours) {
        int scoreTotal = 0;
        
        // Un jeu de bowling a exactement 10 tours (frames)
        int tailleJeu = Math.min(tours.size(), 10); 
        
        for (int i = 0; i < tailleJeu; i++) {
            Tour tour = tours.get(i);
            
            // Le calcul est le même pour tous les tours (y compris le 10e) : 
            // score du tour + bonus
            scoreTotal += calculerScoreTour(tour, tours, i);
        }
        
        return scoreTotal;
    }

    /**
     * Calcule le score d'un tour, incluant les bonus éventuels.
     * @param tour le tour à calculer
     * @param tours la liste de tous les tours
     * @param indexTour l'index du tour dans la liste
     * @return le score du tour (incluant les bonus)
     */
    private int calculerScoreTour(Tour tour, List<Tour> tours, int indexTour) {
        int score = tour.getQuillesAbattuesTour();
        
        if (tour.estStrike()) {
            // Strike : ajouter les 2 prochains lancers
            // Un Strike au tour 10 (1er lancer) n'a pas de bonus d'un autre tour
            if (tour.getNumeroTour() < 10) { 
                score += obtenirBonusStrike(tours, indexTour);
            }
        } else if (tour.estSpare()) {
            // Spare : ajouter le prochain lancer
            // Un Spare au tour 10 n'a pas de bonus d'un autre tour
            if (tour.getNumeroTour() < 10) { 
                score += obtenirBonusSpare(tours, indexTour);
            }
        }
        
        return score;
    }

    /**
     * Obtient le bonus pour un strike (2 prochains lancers).
     * @param tours la liste des tours
     * @param indexTourStrike l'index du tour avec strike (doit être < 9)
     * @return le bonus du strike
     */
    private int obtenirBonusStrike(List<Tour> tours, int indexTourStrike) {
        int bonus = 0;
        int lancersComptes = 0;
        
        // Chercher les 2 prochains lancers à partir du tour suivant
        for (int i = indexTourStrike + 1; i < tours.size(); i++) {
            Tour tourSuivant = tours.get(i);
            List<Lancer> lancers = tourSuivant.getLancers();
            
            for (Lancer lancer : lancers) {
                if (lancersComptes < 2) {
                    bonus += lancer.getQuillesAbattues();
                    lancersComptes++;
                } else {
                    return bonus;
                }
            }
        }
        
        return bonus;
    }

    /**
     * Obtient le bonus pour un spare (1 prochain lancer).
     * @param tours la liste des tours
     * @param indexTourSpare l'index du tour avec spare (doit être < 9)
     * @return le bonus du spare
     */
    private int obtenirBonusSpare(List<Tour> tours, int indexTourSpare) {
        // Chercher le prochain lancer à partir du tour suivant
        for (int i = indexTourSpare + 1; i < tours.size(); i++) {
            Tour tourSuivant = tours.get(i);
            List<Lancer> lancers = tourSuivant.getLancers();
            
            if (!lancers.isEmpty()) {
                return lancers.get(0).getQuillesAbattues();
            }
        }
        
        return 0;
    }
}

