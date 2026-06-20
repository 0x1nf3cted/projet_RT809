package com.covoit.controller;

import com.covoit.dao.SignalementDao;
import com.covoit.dao.TrajetDao;
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
import java.io.PrintWriter;

@WebServlet("/api/statut-passager")
public class PassagerStatutAjaxServlet extends HttpServlet {

    @Inject
    private SignalementDao signalementDao;

    @Inject
    private TrajetDao trajetDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
        if (!"passager".equals(DashboardServlet.getRoleActif(session, u))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Signalement signalement = signalementDao.trouverActifPourPassager(u.getId());
        Trajet trajetEnCours = trajetDao.trouverEnCoursPourPassager(u.getId());

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("{");
        out.print("\"enAttente\":" + (signalement != null));
        if (signalement != null) {
            out.print(",\"pointNom\":\"" + escapeJson(signalement.getPointRendezVous().getNom()) + "\"");
            out.print(",\"destination\":\"" + escapeJson(signalement.getDestination()) + "\"");
            out.print(",\"code\":\"" + escapeJson(signalement.getCodeValidation()) + "\"");
        }
        out.print(",\"trajetEnCours\":");
        if (trajetEnCours != null) {
            out.print("{\"id\":" + trajetEnCours.getId());
            out.print(",\"conducteurPrenom\":\"" + escapeJson(trajetEnCours.getConducteur().getPrenom()) + "\"");
            out.print(",\"destination\":\"" + escapeJson(trajetEnCours.getDestination()) + "\"}");
        } else {
            out.print("null");
        }
        out.print("}");
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
