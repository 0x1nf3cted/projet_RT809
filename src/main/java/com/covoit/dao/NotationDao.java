package com.covoit.dao;

import com.covoit.model.Notation;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class NotationDao {

    @PersistenceContext(unitName = "covoitPU")
    private EntityManager em;

    public void creer(Notation notation) {
        em.persist(notation);
    }

    // Récupérer toutes les notes reçues par un utilisateur
    public List<Notation> trouverPourUtilisateur(Long utilisateurId) {
        return em.createQuery("SELECT n FROM Notation n WHERE n.evalue.id = :id", Notation.class)
                 .setParameter("id", utilisateurId)
                 .getResultList();
    }

    public Notation trouverParTrajetEtEvaluateur(Long trajetId, Long evaluateurId) {
        try {
            return em.createQuery("SELECT n FROM Notation n WHERE n.trajet.id = :tId AND n.evaluateur.id = :eId", Notation.class)
                    .setParameter("tId", trajetId)
                    .setParameter("eId", evaluateurId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean aNoteTrajet(Long trajetId, Long evaluateurId) {
        Long count = em.createQuery("SELECT COUNT(n) FROM Notation n WHERE n.trajet.id = :tId AND n.evaluateur.id = :eId", Long.class)
                .setParameter("tId", trajetId)
                .setParameter("eId", evaluateurId)
                .getSingleResult();
        return count > 0;
    }
    
    public Double calculerMoyenne(Long utilisateurId) {
        Double moyenne = em.createQuery("SELECT AVG(n.note) FROM Notation n WHERE n.evalue.id = :id", Double.class)
                 .setParameter("id", utilisateurId)
                 .getSingleResult();
        return moyenne == null ? 0.0 : Math.round(moyenne * 10.0) / 10.0;
    }
}
