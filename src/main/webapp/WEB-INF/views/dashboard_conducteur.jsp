<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.covoit.model.Utilisateur" %>
<%@ page import="com.covoit.model.Signalement" %>
<%@ page import="com.covoit.model.Trajet" %>
<%@ page import="com.covoit.dao.NotationDao" %>
<%@ page import="com.covoit.model.Notation" %>
<%@ page import="java.util.List" %>
<% 
   Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
   List<Signalement> alertes = (List<Signalement>) request.getAttribute("alertes");
   List<Trajet> trajetsEnCours = (List<Trajet>) request.getAttribute("trajetsEnCours");
   List<Trajet> trajetsARater = (List<Trajet>) request.getAttribute("trajetsARater");
   List<Trajet> trajetsPasses = (List<Trajet>) request.getAttribute("trajetsPasses");
   Double moyenneConducteur = (Double) request.getAttribute("maMoyenne");
   Integer placesRestantes = (Integer) request.getAttribute("placesRestantes");
   NotationDao notationDao = (NotationDao) request.getAttribute("notationDao");
   String contextPath = request.getContextPath();
   boolean sansPlaces = placesRestantes == null || placesRestantes <= 0;
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard Conducteur</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/components/navbar.jsp" />

    <div class="main-content">
        <h2>Bonjour <%= u.getPrenom() %> (Conducteur) - Note globale : <%= moyenneConducteur %>/5 ⭐</h2>
        <p>Places restantes : <strong id="places-restantes"><%= placesRestantes != null ? placesRestantes : 0 %></strong></p>
        <% if("les_deux".equals(u.getRole())) { %>
            <p><a href="<%= contextPath %>/dashboard?force_role=passager">Passer en mode passager</a></p>
        <% } %>
        <% if(request.getParameter("msg") != null) { %>
            <p style="color:green;"><%= request.getParameter("msg") %></p>
        <% } %>
        <% if(request.getParameter("err") != null) { %>
            <p style="color:red;"><%= request.getParameter("err") %></p>
        <% } %>

        <hr/>
        <h3>Alertes temps réel des passagers</h3>
        <p><i>Cette liste se met à jour automatiquement...</i></p>
        <% if(sansPlaces) { %>
            <p id="sans-places-msg" style="color:red;">Aucune place disponible. Vous ne pouvez pas prendre de passager.</p>
        <% } else { %>
            <p id="sans-places-msg" style="display:none;"></p>
        <% } %>

        <div id="alertes-container">
            <% if(alertes == null || alertes.isEmpty()) { %>
                <p id="alertes-vide">Aucun passager en attente dans votre zone pour le moment.</p>
            <% } else { %>
                <table id="alertes-table" border="1" cellpadding="10" cellspacing="0" style="width:100%; text-align:left;">
                    <tr style="background:#f2f2f2;">
                        <th>Passager</th>
                        <th>Lieu (Point de RDV)</th>
                        <th>Destination</th>
                        <th>Action (Prise en charge)</th>
                    </tr>
                    <tbody id="alertes-body">
                    <% for(Signalement s : alertes) { 
                        Double notePassager = notationDao.calculerMoyenne(s.getPassager().getId());
                    %>
                    <tr>
                        <td><%= s.getPassager().getPrenom() %> (<%= notePassager %>/5 ⭐)</td>
                        <td><%= s.getPointRendezVous().getNom() %> (<%= s.getPointRendezVous().getZone() %>)</td>
                        <td><%= s.getDestination() %></td>
                        <td>
                            <% if(!sansPlaces) { %>
                            <form action="<%= contextPath %>/trajet/demarrer" method="post" style="display:inline;">
                                <input type="hidden" name="signalementId" value="<%= s.getId() %>">
                                <input type="text" name="codeSecret" placeholder="Code secret" required maxlength="6" style="width:100px;">
                                <button type="submit" style="background:#2ecc71;">Prendre en charge</button>
                            </form>
                            <% } else { %>
                                -
                            <% } %>
                        </td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>

        <% if(trajetsEnCours != null && !trajetsEnCours.isEmpty()) { %>
            <hr/>
            <h3>Vos trajets en cours</h3>
            <% for(Trajet t : trajetsEnCours) { %>
            <div style="background-color:#fff3cd; padding: 15px; border-radius:5px; margin-bottom:10px;">
                <p>Vous emmenez actuellement <strong><%= t.getPassager().getPrenom() %></strong> vers <strong><%= t.getDestination() %></strong>.</p>
                <p><i>En attente que le passager confirme la fin du trajet de son côté...</i></p>
            </div>
            <% } %>
        <% } %>

        <% if(trajetsARater != null && !trajetsARater.isEmpty()) { %>
            <hr/>
            <h3>Laisser un avis (Trajets terminés)</h3>
            <% for(Trajet t : trajetsARater) { %>
            <div style="background-color:#e2e3e5; padding: 15px; border-radius:5px; margin-bottom:10px;">
                <p>Notez le passager <strong><%= t.getPassager().getPrenom() %></strong> suite à votre trajet vers <%= t.getDestination() %>.</p>
                <form action="<%= contextPath %>/utilisateur/noter" method="post">
                    <input type="hidden" name="trajetId" value="<%= t.getId() %>">
                    <input type="hidden" name="evalueId" value="<%= t.getPassager().getId() %>">
                    <select name="note" required>
                        <option value="5">5 - Excellent passager</option>
                        <option value="4">4 - Très bien</option>
                        <option value="3">3 - Bien</option>
                        <option value="2">2 - Moyen</option>
                        <option value="1">1 - Mauvais</option>
                    </select>
                    <input type="text" name="commentaire" placeholder="Un petit commentaire ?" maxlength="255">
                    <button type="submit" style="background:#ffc107; color:#000;">Noter</button>
                </form>
            </div>
            <% } %>
        <% } %>

        <% if(trajetsPasses != null && !trajetsPasses.isEmpty()) { %>
            <hr/>
            <h3>Mes trajets passés</h3>
            <table border="1" cellpadding="10" cellspacing="0" style="width:100%; text-align:left;">
                <tr style="background:#f2f2f2;">
                    <th>Date</th>
                    <th>Point de RDV</th>
                    <th>Destination</th>
                    <th>Passager</th>
                    <th>Note donnée</th>
                    <th>Note reçue</th>
                </tr>
                <% for(Trajet t : trajetsPasses) {
                    Notation noteDonnee = notationDao.trouverParTrajetEtEvaluateur(t.getId(), u.getId());
                    Notation noteRecue = notationDao.trouverParTrajetEtEvaluateur(t.getId(), t.getPassager().getId());
                %>
                <tr>
                    <td><%= t.getDateCreation() %></td>
                    <td><%= t.getPointRendezVous().getNom() %></td>
                    <td><%= t.getDestination() %></td>
                    <td><%= t.getPassager().getPrenom() %></td>
                    <td><%= noteDonnee != null ? noteDonnee.getNote() + "/5" : "-" %></td>
                    <td><%= noteRecue != null ? noteRecue.getNote() + "/5" : "-" %></td>
                </tr>
                <% } %>
            </table>
        <% } %>
    </div>
    <script>
        var sansPlaces = <%= sansPlaces %>;
        var contextPath = "<%= contextPath %>";
        var dernieresAlertesIds = (function() {
            var ids = [];
            document.querySelectorAll("#alertes-container input[name=\"signalementId\"]").forEach(function(el) {
                ids.push(el.value);
            });
            return ids.join(",");
        })();

        function idsAlertes(alertes) {
            return alertes.map(function(a) { return String(a.id); }).join(",");
        }

        function codesSaisis() {
            var codes = {};
            document.querySelectorAll("#alertes-container input[name=\"codeSecret\"]").forEach(function(input) {
                var form = input.closest("form");
                if (form) {
                    var id = form.querySelector("input[name=\"signalementId\"]").value;
                    if (input.value) {
                        codes[id] = input.value;
                    }
                }
            });
            return codes;
        }

        function escapeAttr(value) {
            return String(value).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/</g, "&lt;");
        }

        function rafraichirAlertes() {
            fetch(contextPath + "/api/alertes", { credentials: "same-origin" })
                .then(function(response) {
                    if (!response.ok) {
                        throw new Error("alertes " + response.status);
                    }
                    return response.json();
                })
                .then(function(data) {
                    sansPlaces = data.placesRestantes <= 0;
                    document.getElementById("places-restantes").textContent = data.placesRestantes;
                    var msg = document.getElementById("sans-places-msg");
                    if (sansPlaces) {
                        msg.textContent = "Aucune place disponible. Vous ne pouvez pas prendre de passager.";
                        msg.style.display = "block";
                        msg.style.color = "red";
                    } else {
                        msg.style.display = "none";
                    }

                    var alertes = data.alertes;
                    var nouveauxIds = idsAlertes(alertes);

                    if (nouveauxIds === dernieresAlertesIds) {
                        return;
                    }
                    dernieresAlertesIds = nouveauxIds;

                    var codes = codesSaisis();
                    var container = document.getElementById("alertes-container");
                    if (alertes.length === 0) {
                        container.innerHTML = "<p id=\"alertes-vide\">Aucun passager en attente dans votre zone pour le moment.</p>";
                        return;
                    }
                    var html = "<table id=\"alertes-table\" border=\"1\" cellpadding=\"10\" cellspacing=\"0\" style=\"width:100%; text-align:left;\">" +
                        "<tr style=\"background:#f2f2f2;\"><th>Passager</th><th>Lieu (Point de RDV)</th><th>Destination</th><th>Action (Prise en charge)</th></tr><tbody id=\"alertes-body\">";
                    for (var i = 0; i < alertes.length; i++) {
                        var s = alertes[i];
                        var codeValue = codes[s.id] ? " value=\"" + escapeAttr(codes[s.id]) + "\"" : "";
                        html += "<tr><td>" + s.passagerPrenom + " (" + s.notePassager + "/5 ⭐)</td>" +
                            "<td>" + s.pointNom + " (" + s.zone + ")</td>" +
                            "<td>" + s.destination + "</td><td>";
                        if (!sansPlaces) {
                            html += "<form action=\"" + contextPath + "/trajet/demarrer\" method=\"post\" style=\"display:inline;\">" +
                                "<input type=\"hidden\" name=\"signalementId\" value=\"" + s.id + "\">" +
                                "<input type=\"text\" name=\"codeSecret\" placeholder=\"Code secret\" required maxlength=\"6\" style=\"width:100px;\"" + codeValue + ">" +
                                "<button type=\"submit\" style=\"background:#2ecc71;\">Prendre en charge</button></form>";
                        } else {
                            html += "-";
                        }
                        html += "</td></tr>";
                    }
                    html += "</tbody></table>";
                    container.innerHTML = html;
                })
                .catch(function(err) {
                    console.error("Erreur polling conducteur:", err);
                });
        }

        setInterval(rafraichirAlertes, 3000);
    </script>
</body>
</html>
