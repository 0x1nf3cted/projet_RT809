package com.covoit.dao;

import com.covoit.model.Signalement;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class SignalementDao {

    @PersistenceContext(unitName = "covoitPU")
    private EntityManager em;

    public void creer(Signalement signalement) {
        em.persist(signalement);
    }

    public void mettreAJour(Signalement signalement) {
        em.merge(signalement);
    }

    public Signalement trouverParId(Long id) {
        return em.find(Signalement.class, id);
    }

    public List<Signalement> trouverEnAttente() {
        return em.createQuery("SELECT s FROM Signalement s WHERE s.etat = 'en attente' ORDER BY s.dateCreation DESC", Signalement.class)
                .getResultList();
    }

    public List<Signalement> trouverEnAttenteParZone(String zone) {
        return em.createQuery("SELECT s FROM Signalement s WHERE s.etat = 'en attente' AND s.pointRendezVous.zone = :zone ORDER BY s.dateCreation DESC", Signalement.class)
                .setParameter("zone", zone)
                .getResultList();
    }

    public Signalement trouverActifPourPassager(Long passagerId) {
        try {
            return em.createQuery("SELECT s FROM Signalement s WHERE s.passager.id = :id AND s.etat = 'en attente'", Signalement.class)
                     .setParameter("id", passagerId)
                     .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
