package com.covoit.dao;

import com.covoit.model.PointRendezVous;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PointRendezVousDao {
    @PersistenceContext(unitName = "covoitPU")
    private EntityManager em;

    public List<PointRendezVous> listerTous() {
        return em.createQuery("SELECT p FROM PointRendezVous p WHERE p.actif = true", PointRendezVous.class).getResultList();
    }
    public PointRendezVous trouverParId(Long id) {
        return em.find(PointRendezVous.class, id);
    }

    public PointRendezVous trouverPlusProche(double latitude, double longitude) {
        List<PointRendezVous> points = listerTous();
        PointRendezVous plusProche = null;
        double minDist = Double.MAX_VALUE;
        for (PointRendezVous p : points) {
            if (p.getLatitude() == null || p.getLongitude() == null) {
                continue;
            }
            double dist = Math.pow(p.getLatitude() - latitude, 2) + Math.pow(p.getLongitude() - longitude, 2);
            if (dist < minDist) {
                minDist = dist;
                plusProche = p;
            }
        }
        return plusProche != null ? plusProche : points.get(0);
    }
}
