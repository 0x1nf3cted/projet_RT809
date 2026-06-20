package com.covoit.config;

import com.covoit.model.PointRendezVous;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Singleton
@Startup
public class AppStartup {

    @PersistenceContext(unitName = "covoitPU")
    private EntityManager em;

    @PostConstruct
    public void init() {
        // Initialisation de quelques points de RDV pour la démo si la table est vide
        Long count = em.createQuery("SELECT COUNT(p) FROM PointRendezVous p", Long.class).getSingleResult();
        if (count == 0) {
            PointRendezVous p1 = new PointRendezVous();
            p1.setNom("Gare Centrale");
            p1.setZone("Centre-Ville");
            p1.setLatitude(48.8566);
            p1.setLongitude(2.3522);
            em.persist(p1);

            PointRendezVous p2 = new PointRendezVous();
            p2.setNom("Université Campus Nord");
            p2.setZone("Nord");
            p2.setLatitude(48.8900);
            p2.setLongitude(2.3500);
            em.persist(p2);
            
            System.out.println("Points de rendez-vous initialisés.");
        }
    }
}
