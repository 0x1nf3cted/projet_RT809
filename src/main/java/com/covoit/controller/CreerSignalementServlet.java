package com.covoit.controller;

import com.covoit.dao.PointRendezVousDao;
import com.covoit.dao.SignalementDao;
import com.covoit.model.PointRendezVous;
import com.covoit.model.Signalement;
import com.covoit.model.Utilisateur;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Random;

@WebServlet("/signalement/creer")
public class CreerSignalementServlet extends HttpServlet {

    @Inject
    private PointRendezVousDao pointRendezVousDao;

    @Inject
    private SignalementDao signalementDao;

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

        String destination = request.getParameter("destination");
        String latStr = request.getParameter("latitude");
        String lonStr = request.getParameter("longitude");

        Signalement actif = signalementDao.trouverActifPourPassager(u.getId());
        if (actif != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard?err=Vous+avez+deja+un+signalement+en+cours");
            return;
        }

        PointRendezVous prdv;
        if (latStr != null && !latStr.isEmpty() && lonStr != null && !lonStr.isEmpty()) {
            prdv = pointRendezVousDao.trouverPlusProche(Double.parseDouble(latStr), Double.parseDouble(lonStr));
        } else {
            Long pointRdvId = Long.parseLong(request.getParameter("pointRdvId"));
            prdv = pointRendezVousDao.trouverParId(pointRdvId);
        }

        Signalement s = new Signalement();
        s.setPassager(u);
        s.setPointRendezVous(prdv);
        s.setDestination(destination);

        String code = String.format("%04d", new Random().nextInt(10000));
        s.setCodeValidation(code);

        signalementDao.creer(s);

        response.sendRedirect(request.getContextPath() + "/dashboard?msg=Signalement+diffuse+avec+succes");
    }
}
