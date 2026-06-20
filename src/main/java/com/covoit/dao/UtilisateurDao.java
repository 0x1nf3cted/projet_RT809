package com.covoit.dao;

import com.covoit.model.Utilisateur;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
public class UtilisateurDao {

    @PersistenceContext(unitName = "covoitPU")
    private EntityManager em;

    public void creer(Utilisateur utilisateur) {
        em.persist(utilisateur);
    }

    public Utilisateur trouverParId(Long id) {
        return em.find(Utilisateur.class, id);
    }

    public Utilisateur trouverParEmail(String email) {
        try {
            TypedQuery<Utilisateur> query = em.createQuery("SELECT u FROM Utilisateur u WHERE u.email = :email", Utilisateur.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (Exception e) {
            return null; // ou throw exception si on préfère
        }
    }

    public void mettreAJour(Utilisateur utilisateur) {
        em.merge(utilisateur);
    }
}
