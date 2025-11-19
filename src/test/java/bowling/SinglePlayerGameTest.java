package bowling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SinglePlayerGameTest {

    private PartieMonoJoueur partie;

    @BeforeEach
    void setUp() {
        partie = new PartieMonoJoueur();
    }

    // --- Tests de déroulement et d'état ---

    @Test
    void testBeginGame() {
        assertEquals(1, partie.getNumeroTourCourant(),
                "On doit commencer au tour n°1");
        assertEquals(1, partie.getNumeroBouleCourante(),
                "On doit commencer à la boule n°1");
    }

    @Test
    void passeAuTourSuivant() {
        // Lancer 1: 1 quille (Tour 1)
        partie.enregistrerLancer(1);
        assertTrue(partie.doitRelancer(), "Premier lancer, le tour continue");

        // Lancer 2: 1 quille (Tour 1) -> Fin du tour
        partie.enregistrerLancer(1);
        assertFalse(partie.doitRelancer(), "Deuxième lancer, le tour est fini");

        // Vérification de l'état après le changement de tour
        assertEquals(2, partie.getNumeroTourCourant(), "On doit être au tour n°2");
        assertEquals(1, partie.getNumeroBouleCourante(), "On doit être à la boule n°1");
    }

    @Test
    void unLancerDeTrop() {
        // Faire 10 tours de 2 lancers à 0 (20 lancers au total)
        for (int i = 0; i < 20; i++) {
            partie.enregistrerLancer(0);
        }

        assertEquals(0, partie.getNumeroTourCourant(),
                "Le n° de tour doit être 0 si fini");
        assertTrue(partie.estTerminee(),
                "Le jeu doit être terminé");

        // Le 21ème lancer doit lever une exception
        assertThrows(IllegalStateException.class, () -> {
            partie.enregistrerLancer(0);
        }, "Le jeu est fini, on doit avoir une exception");
    }

    // --- Tests de Score (Frames 1-9) ---

    @Test
    void testAllOnes() {
        // 20 lancers à 1 quille chacun
        lancerPlusieursFois(20, 1);
        assertEquals(20, partie.score(), "20 lancers à 1 doit faire un score de 20");
        assertTrue(partie.estTerminee());
    }

    @Test
    void testGutterGame() {
        // 20 lancers à 0 quille chacun
        lancerPlusieursFois(20, 0);
        assertEquals(0, partie.score(), "20 lancers à 0 doit faire un score de 0");
        assertTrue(partie.estTerminee());
    }

    @Test
    void testOneSpare() {
        // Tour 1: Spare (5 + 5)
        partie.enregistrerLancer(5);
        partie.enregistrerLancer(5);
        
        // Tour 2: Bonus + Lancer normal (3 + 0)
        partie.enregistrerLancer(3);
        partie.enregistrerLancer(0);

        // Tours 3-10: 8 tours de 2 lancers à 0 (16 lancers à 0)
        lancerPlusieursFois(16, 0);
        
        // Score: (5 + 5 + 3) + (3 + 0) = 13 + 3 = 16
        assertEquals(16, partie.score(), "Un Spare de 5/5 avec bonus 3 doit faire 16");
        assertTrue(partie.estTerminee());
    }

    @Test
    void testOneStrike() {
        // Tour 1: Strike (10)
        partie.enregistrerLancer(10);
        
        // Tour 2: Bonus + Lancer normal (3 + 4)
        partie.enregistrerLancer(3);
        partie.enregistrerLancer(4);

        // Tours 3-10: 8 tours de 2 lancers à 0 (16 lancers à 0)
        lancerPlusieursFois(16, 0);
        
        // Score: (10 + 3 + 4) + (3 + 4) = 17 + 7 = 24
        assertEquals(24, partie.score(), "Un Strike de 10 avec bonus 3 et 4 doit faire 24");
        assertTrue(partie.estTerminee());
    }

    @Test
    void testPerfectGame() {
        // 12 Strikes (10 tours + 2 bonus au tour 10)
        lancerPlusieursFois(12, 10);
        assertEquals(300, partie.score(), "Le jeu parfait (12 Strikes) doit faire 300");
        assertTrue(partie.estTerminee());
    }

    @Test
    void testNineSpareAndFives() {
        // Tours 1-9: 9 Spares de 5/5 (18 lancers)
        for (int i = 0; i < 9; i++) {
            partie.enregistrerLancer(5);
            partie.enregistrerLancer(5);
        }
        
        // Tour 10: 5 (pour le bonus du 9e Spare) + 5 + 5
        partie.enregistrerLancer(5);
        partie.enregistrerLancer(5);
        partie.enregistrerLancer(5);
        
        // Score: 9 * (5 + 5 + 5) + (5 + 5 + 5) = 9 * 15 + 15 = 135 + 15 = 150
        assertEquals(150, partie.score(), "Neuf Spares de 5/5 suivis d'un 5/5/5 doivent faire 150");
        assertTrue(partie.estTerminee());
    }
    
    // --- Tests de Score (Tour 10) ---
    
    @Test
    void testTenthFrameNoBonus() {
        // Tours 1-9: 9 tours de 2 lancers à 0 (18 lancers à 0)
        lancerPlusieursFois(18, 0);
        
        // Tour 10: 3 + 4 (pas de bonus)
        partie.enregistrerLancer(3);
        partie.enregistrerLancer(4);
        
        // Score: 0 + 0 + ... + (3 + 4) = 7
        assertEquals(7, partie.score(), "Score du tour 10 sans bonus doit être 7");
        assertTrue(partie.estTerminee());
    }
    
    @Test
    void testTenthFrameSpareBonus() {
        // Tours 1-9: 9 tours de 2 lancers à 0 (18 lancers à 0)
        lancerPlusieursFois(18, 0);
        
        // Tour 10: Spare (6 + 4) + Bonus (5)
        partie.enregistrerLancer(6); // Boule 1
        partie.enregistrerLancer(4); // Boule 2 (Spare)
        partie.enregistrerLancer(5); // Boule 3 (Bonus)
        
        // Score: 0 + ... + (6 + 4 + 5) = 15
        assertEquals(15, partie.score(), "Score du tour 10 Spare + 5 doit être 15");
        assertTrue(partie.estTerminee());
    }
    
    @Test
    void testTenthFrameStrikeBonus() {
        // Tours 1-9: 9 tours de 2 lancers à 0 (18 lancers à 0)
        lancerPlusieursFois(18, 0);
        
        // Tour 10: Strike (10) + Bonus (4 + 6)
        partie.enregistrerLancer(10); // Boule 1 (Strike)
        partie.enregistrerLancer(4);  // Boule 2 (Bonus)
        partie.enregistrerLancer(6);  // Boule 3 (Bonus)
        
        // Score: 0 + ... + (10 + 4 + 6) = 20
        assertEquals(20, partie.score(), "Score du tour 10 Strike + 4 + 6 doit être 20");
        assertTrue(partie.estTerminee());
    }

    // --- Test d'intégrité (Règles) ---
    
    @Test
    void lancerInvalideTropDeQuilles() {
        // Lancer qui dépasse 10 quilles au deuxième coup (hors Spare/Strike)
        partie.enregistrerLancer(5); // Lancer 1: OK
        
        // Lancer 2: 6 quilles. Total du tour: 11 > 10. Doit lever une exception.
        assertThrows(IllegalArgumentException.class, () -> {
            partie.enregistrerLancer(6);
        }, "Le total des quilles abattues dans un tour normal ne peut pas dépasser 10.");
    }

    // --- Méthodes utilitaires ---

    /**
     * Lance plusieurs fois le même nombre de quilles.
     * @param n nombre de lancers
     * @param quilles quilles abattues à chaque lancer
     */
    private void lancerPlusieursFois(int n, int quilles) {
        for (int i = 0; i < n; i++) {
            // S'assurer de ne pas lancer après la fin du jeu pour les tests de score
            if (!partie.estTerminee()) {
                partie.enregistrerLancer(quilles);
            }
        }
    }
}