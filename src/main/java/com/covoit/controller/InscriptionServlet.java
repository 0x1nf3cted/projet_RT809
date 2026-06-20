package com.covoit.controller;

import com.covoit.dao.UtilisateurDao;
import com.covoit.model.Utilisateur;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/inscription")
public class InscriptionServlet extends HttpServlet {
    
    @Inject
    private UtilisateurDao utilisateurDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/inscription.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse"); // À hasher en prod
        String role = request.getParameter("role");

        Utilisateur u = new Utilisateur();
        u.setNom(nom);
        u.setPrenom(prenom);
        u.setEmail(email);
        u.setMotDePasse(motDePasse);
        u.setRole(role);
        
        if ("conducteur".equals(role) || "les_deux".equals(role)) {
            String places = request.getParameter("placesDisponibles");
            u.setPlacesDisponibles(places != null && !places.isEmpty() ? Integer.parseInt(places) : 0);
            u.setDisponible(false);
            u.setZone(request.getParameter("zone"));
        }

        try {
            utilisateurDao.creer(u);
            response.sendRedirect(request.getContextPath() + "/connexion?succes=1");
        } catch (Exception e) {
            request.setAttribute("erreur", "Erreur réseau ou email déjà utilisé.");
            request.getRequestDispatcher("/WEB-INF/views/inscription.jsp").forward(request, response);
        }
    }
}
