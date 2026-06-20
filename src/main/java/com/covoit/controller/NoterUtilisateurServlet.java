package com.covoit.controller;

import com.covoit.dao.NotationDao;
import com.covoit.dao.TrajetDao;
import com.covoit.dao.UtilisateurDao;
import com.covoit.model.Notation;
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

@WebServlet("/utilisateur/noter")
public class NoterUtilisateurServlet extends HttpServlet {

    @Inject
    private NotationDao notationDao;

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

        Utilisateur evaluateur = (Utilisateur) session.getAttribute("utilisateur");
        Long trajetId = Long.parseLong(request.getParameter("trajetId"));
        Long evalueId = Long.parseLong(request.getParameter("evalueId"));
        int note = Integer.parseInt(request.getParameter("note"));
        String commentaire = request.getParameter("commentaire");

        Trajet t = trajetDao.trouverParId(trajetId);
        Utilisateur evalue = utilisateurDao.trouverParId(evalueId);

        if (t != null && evalue != null && "terminé".equals(t.getEtat())) {
            if (!notationDao.aNoteTrajet(t.getId(), evaluateur.getId())) {
                Notation n = new Notation();
                n.setEvaluateur(evaluateur);
                n.setEvalue(evalue);
                n.setNote(note);
                n.setCommentaire(commentaire);
                n.setTrajet(t);
                
                notationDao.creer(n);
                response.sendRedirect(request.getContextPath() + "/dashboard?msg=Note+attribuee+avec+succes.");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard?err=Vous+avez+deja+note+cet+utilisateur+pour+ce+trajet.");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard?err=Action+impossible.");
        }
    }
}
