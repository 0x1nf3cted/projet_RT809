package com.covoit.controller;

import com.covoit.dao.TrajetDao;
import com.covoit.dao.UtilisateurDao;
import com.covoit.model.Trajet;
import com.covoit.model.Utilisateur;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/trajet/terminer")
public class TerminerTrajetServlet extends HttpServlet {

    @Inject
    private TrajetDao trajetDao;

    @Inject
    private UtilisateurDao utilisateurDao;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
        if (!"passager".equals(DashboardServlet.getRoleActif(session, u))) {
            response.sendRedirect(request.getContextPath() + "/dashboard?err=Action+reservee+au+mode+passager.");
            return;
        }

        Long trajetId = Long.parseLong(request.getParameter("trajetId"));
        Trajet t = trajetDao.trouverParId(trajetId);
        
        if (t != null && "en_cours".equals(t.getEtat()) && t.getPassager().getId().equals(u.getId())) {
            t.setEtat("terminé");
            trajetDao.mettreAJour(t);

            Utilisateur conducteur = utilisateurDao.trouverParId(t.getConducteur().getId());
            Integer places = conducteur.getPlacesDisponibles();
            conducteur.setPlacesDisponibles(places != null ? places + 1 : 1);
            utilisateurDao.mettreAJour(conducteur);

            response.sendRedirect(request.getContextPath() + "/dashboard?msg=Trajet+termine.+Vous+pouvez+maintenant+noter+le+conducteur.");
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard?err=Erreur+lors+de+la+cloture+du+trajet.");
        }
    }
}
