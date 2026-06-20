package com.covoit.controller;

import com.covoit.dao.SignalementDao;
import com.covoit.dao.TrajetDao;
import com.covoit.dao.UtilisateurDao;
import com.covoit.model.Signalement;
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

@WebServlet("/trajet/demarrer")
public class DemarrerTrajetServlet extends HttpServlet {

    @Inject
    private SignalementDao signalementDao;

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

        Utilisateur conducteur = (Utilisateur) session.getAttribute("utilisateur");
        if (!"conducteur".equals(DashboardServlet.getRoleActif(session, conducteur))) {
            response.sendRedirect(request.getContextPath() + "/dashboard?err=Action+reservee+au+mode+conducteur.");
            return;
        }

        conducteur = utilisateurDao.trouverParId(conducteur.getId());
        Integer places = conducteur.getPlacesDisponibles();
        if (places == null || places <= 0) {
            response.sendRedirect(request.getContextPath() + "/dashboard?err=Aucune+place+disponible.");
            return;
        }

        Long signalementId = Long.parseLong(request.getParameter("signalementId"));
        String codeSecret = request.getParameter("codeSecret");

        Signalement alert = signalementDao.trouverParId(signalementId);

        if (alert != null && "en attente".equals(alert.getEtat())) {
            if (alert.getCodeValidation().equals(codeSecret.trim())) {
                alert.setEtat("pris_en_charge");
                signalementDao.mettreAJour(alert);

                Trajet t = new Trajet();
                t.setConducteur(conducteur);
                t.setPassager(alert.getPassager());
                t.setPointRendezVous(alert.getPointRendezVous());
                t.setDestination(alert.getDestination());
                t.setEtat("en_cours");
                trajetDao.creer(t);

                conducteur.setPlacesDisponibles(places - 1);
                utilisateurDao.mettreAJour(conducteur);
                session.setAttribute("utilisateur", conducteur);

                response.sendRedirect(request.getContextPath() + "/dashboard?msg=Code+valide.+Trajet+demarre+avec+succes+%21");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard?err=Code+d'identification+incorrect.");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard?err=Signalement+introuvable+ou+deja+pris.");
        }
    }
}
