package com.covoit.controller;

import com.covoit.dao.UtilisateurDao;
import com.covoit.model.Utilisateur;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/connexion")
public class ConnexionServlet extends HttpServlet {
    
    @Inject
    private UtilisateurDao utilisateurDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/connexion.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");

        Utilisateur u = utilisateurDao.trouverParEmail(email);

        if (u != null && u.getMotDePasse().equals(motDePasse)) {
            HttpSession session = request.getSession();
            session.setAttribute("utilisateur", u);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("erreur", "Identifiants invalides");
            request.getRequestDispatcher("/WEB-INF/views/connexion.jsp").forward(request, response);
        }
    }
}
