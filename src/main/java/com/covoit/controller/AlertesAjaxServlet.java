package com.covoit.controller;

import com.covoit.dao.NotationDao;
import com.covoit.dao.SignalementDao;
import com.covoit.dao.UtilisateurDao;
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
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/alertes")
public class AlertesAjaxServlet extends HttpServlet {

    @Inject
    private SignalementDao signalementDao;

    @Inject
    private NotationDao notationDao;

    @Inject
    private UtilisateurDao utilisateurDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
        if (!"conducteur".equals(DashboardServlet.getRoleActif(session, u))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        u = utilisateurDao.trouverParId(u.getId());
        List<Signalement> alertes = signalementDao.trouverEnAttenteParZone(u.getZone());
        Integer placesRestantes = u.getPlacesDisponibles() != null ? u.getPlacesDisponibles() : 0;

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("{\"placesRestantes\":" + placesRestantes + ",\"alertes\":[");
        for (int i = 0; i < alertes.size(); i++) {
            Signalement s = alertes.get(i);
            Double notePassager = notationDao.calculerMoyenne(s.getPassager().getId());
            if (i > 0) {
                out.print(",");
            }
            out.print("{");
            out.print("\"id\":" + s.getId() + ",");
            out.print("\"passagerPrenom\":\"" + escapeJson(s.getPassager().getPrenom()) + "\",");
            out.print("\"notePassager\":" + notePassager + ",");
            out.print("\"pointNom\":\"" + escapeJson(s.getPointRendezVous().getNom()) + "\",");
            out.print("\"zone\":\"" + escapeJson(s.getPointRendezVous().getZone()) + "\",");
            out.print("\"destination\":\"" + escapeJson(s.getDestination()) + "\"");
            out.print("}");
        }
        out.print("]}");
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
