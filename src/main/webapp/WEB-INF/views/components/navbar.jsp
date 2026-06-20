<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.covoit.model.Utilisateur" %>
<% 
   Utilisateur userNav = (Utilisateur) session.getAttribute("utilisateur");
%>
<header class="navbar">
    <div>
        <h1><a href="<%= request.getContextPath() %>/dashboard" style="background:none; padding:0; font-size: inherit;">BlaBlaCovoit RT0809</a></h1>
    </div>
    <div class="auth-buttons">
        <% if(userNav != null) { %>
            <a href="<%= request.getContextPath() %>/dashboard">Mon Profil (<%= userNav.getPrenom() %>)</a>
            <a href="<%= request.getContextPath() %>/logout" style="background-color: #d9534f;">Se déconnecter</a>
        <% } else { %>
            <a href="<%= request.getContextPath() %>/inscription">S'inscrire</a>
            <a href="<%= request.getContextPath() %>/connexion">Se connecter</a>
        <% } %>
    </div>
</header>
