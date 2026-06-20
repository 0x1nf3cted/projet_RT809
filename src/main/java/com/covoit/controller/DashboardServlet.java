package com.covoit.controller;

import com.covoit.dao.NotationDao;
import com.covoit.dao.PointRendezVousDao;
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
import java.util.List;
import java.util.ArrayList;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Inject
    private PointRendezVousDao pointRendezVousDao;

    @Inject
    private SignalementDao signalementDao;

    @Inject
    private TrajetDao trajetDao;

    @Inject
    private NotationDao notationDao;

    @Inject
    private UtilisateurDao utilisateurDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null) {
            response.sendRedirect(request.getContextPath() + "/connexion");
            return;
        }

        Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
        String forceRole = request.getParameter("force_role");

        if ("les_deux".equals(u.getRole())) {
            if ("passager".equals(forceRole) || "conducteur".equals(forceRole)) {
                session.setAttribute("roleActif", forceRole);
            }
            if (session.getAttribute("roleActif") == null) {
                request.getRequestDispatcher("/WEB-INF/views/dashboard_choix.jsp").forward(request, response);
                return;
            }
        }

        String roleActif = getRoleActif(session, u);
        u = utilisateurDao.trouverParId(u.getId());
        session.setAttribute("utilisateur", u);

        Double maMoyenne = notationDao.calculerMoyenne(u.getId());
        request.setAttribute("maMoyenne", maMoyenne);

        if ("conducteur".equals(roleActif)) {
            List<Signalement> alertes = signalementDao.trouverEnAttenteParZone(u.getZone());
            List<Trajet> trajets = trajetDao.trouverParConducteur(u.getId());
            List<Trajet> trajetsEnCours = new ArrayList<>();
            List<Trajet> trajetsARater = new ArrayList<>();
            List<Trajet> trajetsPasses = new ArrayList<>();

            for (Trajet t : trajets) {
                if ("en_cours".equals(t.getEtat())) {
                    trajetsEnCours.add(t);
                } else if ("terminé".equals(t.getEtat())) {
                    trajetsPasses.add(t);
                    if (!notationDao.aNoteTrajet(t.getId(), u.getId())) {
                        trajetsARater.add(t);
                    }
                }
            }

            request.setAttribute("alertes", alertes);
            request.setAttribute("trajetsEnCours", trajetsEnCours);
            request.setAttribute("trajetsARater", trajetsARater);
            request.setAttribute("trajetsPasses", trajetsPasses);
            request.setAttribute("notationDao", notationDao);
            request.setAttribute("placesRestantes", u.getPlacesDisponibles());

            request.getRequestDispatcher("/WEB-INF/views/dashboard_conducteur.jsp").forward(request, response);
        } else if ("passager".equals(roleActif)) {
            Signalement enCours = signalementDao.trouverActifPourPassager(u.getId());
            List<Trajet> trajets = trajetDao.trouverParPassager(u.getId());

            List<Trajet> trajetsEnCours = new ArrayList<>();
            List<Trajet> trajetsARater = new ArrayList<>();
            List<Trajet> trajetsPasses = new ArrayList<>();

            for (Trajet t : trajets) {
                if ("en_cours".equals(t.getEtat())) {
                    trajetsEnCours.add(t);
                } else if ("terminé".equals(t.getEtat())) {
                    trajetsPasses.add(t);
                    if (!notationDao.aNoteTrajet(t.getId(), u.getId())) {
                        trajetsARater.add(t);
                    }
                }
            }

            request.setAttribute("signalementActif", enCours);
            request.setAttribute("pointsRendezVous", pointRendezVousDao.listerTous());
            request.setAttribute("trajetsEnCours", trajetsEnCours);
            request.setAttribute("trajetsARater", trajetsARater);
            request.setAttribute("trajetsPasses", trajetsPasses);
            request.setAttribute("notationDao", notationDao);

            request.getRequestDispatcher("/WEB-INF/views/dashboard_passager.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/dashboard_choix.jsp").forward(request, response);
        }
    }

    static String getRoleActif(HttpSession session, Utilisateur u) {
        if ("les_deux".equals(u.getRole())) {
            return (String) session.getAttribute("roleActif");
        }
        return u.getRole();
    }
}
