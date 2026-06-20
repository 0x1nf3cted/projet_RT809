<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Inscription</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/components/navbar.jsp" />

    <div class="main-content">
        <h2>Créer un compte</h2>
        <% if(request.getAttribute("erreur") != null) { %>
            <p style="color:red;"><%= request.getAttribute("erreur") %></p>
        <% } %>
        <form action="inscription" method="post">
            <label>Nom: <input type="text" name="nom" required></label><br/>
            <label>Prénom: <input type="text" name="prenom" required></label><br/>
            <label>Email: <input type="email" name="email" required></label><br/>
            <label>Mot de passe: <input type="password" name="motDePasse" required></label><br/>
            <label>Rôle: 
                <select name="role" id="roleSelect" onchange="toggleConducteur()">
                    <option value="passager">Passager</option>
                    <option value="conducteur">Conducteur</option>
                    <option value="les_deux">Les deux</option>
                </select>
            </label><br/>
            
            <div id="divConducteur" style="display:none;">
                <label>Places disponibles : <input type="number" name="placesDisponibles" min="1" max="9"></label><br/>
                <label>Zone de couverture :
                    <select name="zone">
                        <option value="Centre-Ville">Centre-Ville</option>
                        <option value="Nord">Nord</option>
                    </select>
                </label>
            </div>
            <br/>
            <button type="submit">S'inscrire</button>
        </form>
    </div>

    <script>
        function toggleConducteur() {
            var val = document.getElementById("roleSelect").value;
            document.getElementById("divConducteur").style.display = (val === 'conducteur' || val === 'les_deux') ? 'block' : 'none';
        }
    </script>
</body>
</html>
