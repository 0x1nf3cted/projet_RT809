<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Connexion</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/components/navbar.jsp" />

    <div class="main-content">
        <h2>Se Connecter</h2>
        <% if(request.getParameter("succes") != null) { %>
            <p style="color:green;">Inscription réussie ! Veuillez vous connecter.</p>
        <% } %>
        <% if(request.getAttribute("erreur") != null) { %>
            <p style="color:red;"><%= request.getAttribute("erreur") %></p>
        <% } %>
        <form action="connexion" method="post">
            <label>Email: <input type="email" name="email" required></label><br/>
            <label>Mot de passe: <input type="password" name="motDePasse" required></label><br/>
            <button type="submit">Connexion</button>
        </form>
    </div>
</body>
</html>
