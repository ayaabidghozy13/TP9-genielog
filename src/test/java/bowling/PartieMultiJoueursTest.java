package bowling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PartieMultiJoueursTest {

    private IPartieMultiJoueurs partie;
    private String[] joueurs;
    private final String JOUEUR_1 = "Alice";
    private final String JOUEUR_2 = "Bob";

    @BeforeEach
    void setUp() {
        partie = new PartieMultiJoueurs();
        joueurs = new String[]{JOUEUR_1, JOUEUR_2};
    }

    // --- Tests de démarrage et d'état ---

    @Test
    void testDemarreNouvellePartie() {
        String message = partie.demarreNouvellePartie(joueurs);
        assertEquals("Prochain tir : joueur Alice, tour n° 1, boule n° 1", message,
                "Le premier joueur doit être Alice, au tour 1, boule 1");
    }

    @Test
    void testDemarrageSansJoueur() {
        assertThrows(IllegalArgumentException.class, () -> {
            partie.demarreNouvellePartie(new String[]{});
        }, "Ne devrait pas démarrer sans joueur");
    }
    
    @Test
    void testScoreJoueurInconnu() {
        partie.demarreNouvellePartie(joueurs);
        
        // Tester l'accès au score d'un joueur qui n'est pas dans la partie
        assertThrows(IllegalArgumentException.class, () -> {
            partie.scorePour("Inconnu");
        }, "Doit lever une exception si le joueur est inconnu");
    }


    // --- Tests de rotation (RotationDesJoueurs) ---

    @Test
    void testRotationDesJoueurs() {
        partie.demarreNouvellePartie(joueurs);

        // Tour 1: Alice lance 1, passe à Bob
        // Alice fait 1 + 1 -> fin du tour d'Alice. Passage à Bob.
        partie.enregistreLancer(1);
        String message = partie.enregistreLancer(1);
        assertEquals("Prochain tir : joueur Bob, tour n° 1, boule n° 1", message,
                "Après 2 lancers d'Alice, c'est le tour de Bob");
        
        // Tour 1: Bob lance 2, passe à Alice
        // Bob fait 2 + 2 -> fin du tour de Bob. Passage à Alice.
        partie.enregistreLancer(2);
        message = partie.enregistreLancer(2);
        assertEquals("Prochain tir : joueur Alice, tour n° 2, boule n° 1", message,
                "Après 2 lancers de Bob, c'est le tour d'Alice au tour n° 2");
    }

    @Test
    void testRotationAvecStrike() {
        partie.demarreNouvellePartie(joueurs);
        
        // Alice: Strike (10). Son tour est fini en 1 lancer.
        String message = partie.enregistreLancer(10);
        assertEquals("Prochain tir : joueur Bob, tour n° 1, boule n° 1", message,
                "Après le Strike d'Alice, c'est le tour de Bob");
        
        // Bob: 5 + 3. Son tour est fini en 2 lancers.
        partie.enregistreLancer(5);
        message = partie.enregistreLancer(3);
        
        assertEquals("Prochain tir : joueur Alice, tour n° 2, boule n° 1", message,
                "Après les 2 lancers de Bob, c'est le tour d'Alice au tour n° 2");
    }

    @Test
    void testRotationAvecSpare() {
        partie.demarreNouvellePartie(joueurs);
        
        // Alice: Spare (7 + 3). Son tour est fini en 2 lancers.
        partie.enregistreLancer(7);
        String message = partie.enregistreLancer(3);
        assertEquals("Prochain tir : joueur Bob, tour n° 1, boule n° 1", message,
                "Après le Spare d'Alice, c'est le tour de Bob");
        
        // Bob: 5 + 4. Son tour est fini en 2 lancers.
        partie.enregistreLancer(5);
        message = partie.enregistreLancer(4);
        
        assertEquals("Prochain tir : joueur Alice, tour n° 2, boule n° 1", message,
                "Après les 2 lancers de Bob, c'est le tour d'Alice au tour n° 2");
    }

    // --- Tests de score (ScoreSimple) ---

    @Test
    void testScoreSimple() {
        partie.demarreNouvellePartie(joueurs);

        // Tour 1: Alice (1 + 1), Bob (2 + 2)
        partie.enregistreLancer(1);
        partie.enregistreLancer(1); // Alice: 2
        
        partie.enregistreLancer(2);
        partie.enregistreLancer(2); // Bob: 4

        // Tour 2: Alice (3 + 3), Bob (4 + 4)
        partie.enregistreLancer(3);
        partie.enregistreLancer(3); // Alice: 8
        
        partie.enregistreLancer(4);
        partie.enregistreLancer(4); // Bob: 12
        
        assertEquals(8, partie.scorePour(JOUEUR_1), "Le score d'Alice doit être 8");
        assertEquals(12, partie.scorePour(JOUEUR_2), "Le score de Bob doit être 12");
    }
    
    @Test
    void testScoreProvisoire() {
        partie.demarreNouvellePartie(joueurs);
        
        // Alice: Spare (5 + 5)
        partie.enregistreLancer(5);
        partie.enregistreLancer(5);
        
        // Bob: Lancer 1 (5)
        partie.enregistreLancer(5);
        
        // Score Alice: 10 + (bonus 5) = 15.
        // Score Bob: 5 + (0 si on attend le prochain lancer) = 5.
        assertEquals(15, partie.scorePour(JOUEUR_1), 
                "Score provisoire d'Alice après Spare + Lancer de Bob doit être 15");
        assertEquals(5, partie.scorePour(JOUEUR_2), 
                "Score provisoire de Bob (1 lancer) doit être 5");
    }

    // --- Tests de fin de partie (FinDePartie) ---

    @Test
    void testFinDePartie() {
        partie.demarreNouvellePartie(joueurs);
        
        // Simuler une partie simple où tous font 0, puis 10 au dernier tour.
        // Chaque joueur aura 10 tours (10 x 2 lancers) = 20 lancers / joueur.
        
        // 9 tours de 2 lancers à 0 (18 lancers par joueur)
        for (int i = 0; i < 9 * 2; i++) {
            partie.enregistreLancer(0); // Alice, Bob, Alice, Bob...
        }
        
        // Arrivée au Tour 10
        
        // Alice Tour 10: 1 + 1 (2 lancers) -> fini.
        partie.enregistreLancer(1);
        partie.enregistreLancer(1);
        
        // Bob Tour 10: 1 + 1 (2 lancers) -> fini.
        partie.enregistreLancer(1);
        partie.enregistreLancer(1);
        
        // La partie est maintenant terminée.
        String message = partie.enregistreLancer(0); // Lancez un lancer de plus pour vérifier l'état
        
        assertEquals("Partie terminée", message,
                "La partie doit se terminer après le dernier lancer du dernier joueur");

        // Assurez-vous que les scores sont corrects
        assertEquals(2, partie.scorePour(JOUEUR_1), "Score final d'Alice doit être 2");
        assertEquals(2, partie.scorePour(JOUEUR_2), "Score final de Bob doit être 2");
    }
    
    @Test
    void testFinDePartieAvecBonusAuTour10() {
        partie.demarreNouvellePartie(joueurs);
        
        // 9 tours de 2 lancers à 0 (18 lancers par joueur)
        for (int i = 0; i < 9 * 2; i++) {
            partie.enregistreLancer(0); // Alice, Bob, Alice, Bob...
        }
        
        // Alice Tour 10: Strike (10) + 1 + 1 (3 lancers) -> fini.
        partie.enregistreLancer(10);
        partie.enregistreLancer(1);
        partie.enregistreLancer(1);
        
        // Bob Tour 10: Strike (10) + 2 + 2 (3 lancers) -> fini.
        partie.enregistreLancer(10);
        partie.enregistreLancer(2);
        partie.enregistreLancer(2);

        // La partie est maintenant terminée.
        String message = partie.enregistreLancer(0); 
        
        assertEquals("Partie terminée", message,
                "La partie doit se terminer après le dernier lancer du dernier joueur (y compris bonus)");

        // Scores: Alice (0 * 9 tours) + 10+1+1 = 12. Bob: 10+2+2 = 14.
        assertEquals(12, partie.scorePour(JOUEUR_1), "Score final d'Alice doit être 12");
        assertEquals(14, partie.scorePour(JOUEUR_2), "Score final de Bob doit être 14");
    }
}
