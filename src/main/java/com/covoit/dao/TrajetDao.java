package com.covoit.dao;

import com.covoit.model.Trajet;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TrajetDao {
    @PersistenceContext(unitName = "covoitPU")
    private EntityManager em;

    public void creer(Trajet trajet) {
        em.persist(trajet);
    }

    public Trajet trouverParId(Long id) {
        return em.find(Trajet.class, id);
    }

    public void mettreAJour(Trajet trajet) {
        em.merge(trajet);
    }

    public List<Trajet> trouverParConducteur(Long idConducteur) {
        return em.createQuery("SELECT t FROM Trajet t WHERE t.conducteur.id = :id ORDER BY t.dateCreation DESC", Trajet.class)
                 .setParameter("id", idConducteur)
                 .getResultList();
    }
    
    public List<Trajet> trouverParPassager(Long idPassager) {
        return em.createQuery("SELECT t FROM Trajet t WHERE t.passager.id = :id ORDER BY t.dateCreation DESC", Trajet.class)
                 .setParameter("id", idPassager)
                 .getResultList();
    }

    public Trajet trouverEnCoursPourPassager(Long idPassager) {
        try {
            return em.createQuery("SELECT t FROM Trajet t WHERE t.passager.id = :id AND t.etat = 'en_cours'", Trajet.class)
                    .setParameter("id", idPassager)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
