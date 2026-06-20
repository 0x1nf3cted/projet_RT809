<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Choix du rôle</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/components/navbar.jsp" />
    <div class="main-content">
        <h2>Bonjour, souhaitez-vous agir en tant que Passager ou Conducteur ?</h2>
        <a href="<%= request.getContextPath() %>/dashboard?force_role=passager">Espace Passager</a> <br/><br/>
        <a href="<%= request.getContextPath() %>/dashboard?force_role=conducteur">Espace Conducteur</a>
    </div>
</body>
</html>
