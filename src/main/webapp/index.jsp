<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Covoiturage RT0809 - Accueil</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/components/navbar.jsp" />

    <div class="main-content" style="text-align: center;">
        <h1 style="color: #00A680;">Bienvenue sur BlaBlaCovoit RT0809</h1>
        <p style="font-size: 18px; color: #555;">Le moyen le plus simple d'organiser vos trajets réguliers.</p>
        
        <div style="margin-top: 30px;">
            <a href="inscription" style="background-color: #00A680; color: white; padding: 10px 20px; border-radius: 5px; text-decoration: none; margin-right: 15px; font-weight: bold;">Créer un compte</a>
            <a href="connexion" style="border: 2px solid #00A680; color: #00A680; padding: 8px 18px; border-radius: 5px; text-decoration: none; font-weight: bold;">J'ai déjà un compte</a>
        </div>
    </div>
</body>
</html>
