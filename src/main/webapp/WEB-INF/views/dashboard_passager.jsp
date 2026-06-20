<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.covoit.model.Utilisateur" %>
<%@ page import="com.covoit.model.PointRendezVous" %>
<%@ page import="com.covoit.model.Signalement" %>
<%@ page import="com.covoit.model.Trajet" %>
<%@ page import="com.covoit.model.Notation" %>
<%@ page import="com.covoit.dao.NotationDao" %>
<%@ page import="java.util.List" %>
<% 
   Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
   List<PointRendezVous> pointsRdv = (List<PointRendezVous>) request.getAttribute("pointsRendezVous");
   Signalement actif = (Signalement) request.getAttribute("signalementActif");
   List<Trajet> trajetsEnCours = (List<Trajet>) request.getAttribute("trajetsEnCours");
   List<Trajet> trajetsARater = (List<Trajet>) request.getAttribute("trajetsARater");
   List<Trajet> trajetsPasses = (List<Trajet>) request.getAttribute("trajetsPasses");
   Double moyenne = (Double) request.getAttribute("maMoyenne");
   NotationDao notationDao = (NotationDao) request.getAttribute("notationDao");
   String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard Passager</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/components/navbar.jsp" />
    <div class="main-content">
        <h2>Bonjour <%= u.getPrenom() %> (Passager) - Note globale : <%= moyenne %>/5 ⭐</h2>
        <% if("les_deux".equals(u.getRole())) { %>
            <p><a href="<%= contextPath %>/dashboard?force_role=conducteur">Passer en mode conducteur</a></p>
        <% } %>
        <% if(request.getParameter("msg") != null) { %>
            <p style="color:green;"><%= request.getParameter("msg") %></p>
        <% } %>
        <% if(request.getParameter("err") != null) { %>
            <p style="color:red;"><%= request.getParameter("err") %></p>
        <% } %>

        <hr/>
        <div id="zone-signalement">
        <% if(actif == null) { %>
            <h3>Se signaler à un Point de Rendez-vous</h3>
            <form action="<%= contextPath %>/signalement/creer" method="post">
                <input type="hidden" name="latitude" id="latitude">
                <input type="hidden" name="longitude" id="longitude">
                <button type="button" onclick="utiliserGPS()">Utiliser ma position GPS</button>
                <span id="gpsStatus"></span><br/><br/>
                <label>Point de rendez-vous :
                    <select name="pointRdvId" id="pointRdvId" onchange="selectionManuelle()">
                        <% for(PointRendezVous p : pointsRdv) { %>
                            <option value="<%= p.getId() %>" data-lat="<%= p.getLatitude() != null ? p.getLatitude() : "" %>" data-lon="<%= p.getLongitude() != null ? p.getLongitude() : "" %>"><%= p.getNom() %> (<%= p.getZone() %>)</option>
                        <% } %>
                    </select>
                </label><br/><br/>
                <label>Destination souhaitée : 
                    <input type="text" name="destination" required placeholder="Ex: Paris Centrale">
                </label><br/><br/>
                <button type="submit">Signaler ma présence</button>
            </form>
        <% } else { %>
            <div style="background-color:#d4edda; padding: 15px; border-radius:5px;">
                <h3>Signalement en cours de diffusion...</h3>
                <p>Vous êtes au point : <strong><%= actif.getPointRendezVous().getNom() %></strong></p>
                <p>Destination : <strong><%= actif.getDestination() %></strong></p>
                <p>Les conducteurs autour de vous sont alertés.</p>
                <hr>
                <h4>Code d'identification secret à échanger avec le conducteur :</h4>
                <div style="font-size:2em; font-weight:bold; letter-spacing: 5px; color:#2c3e50;"><%= actif.getCodeValidation() %></div>
            </div>
        <% } %>
        </div>

        <hr/>
        <div id="zone-trajets-en-cours">
        <% if(trajetsEnCours != null && !trajetsEnCours.isEmpty()) { %>
            <h3>Trajets en cours</h3>
            <% for(Trajet t : trajetsEnCours) { %>
            <div style="background-color:#fff3cd; padding: 15px; border-radius:5px; margin-bottom:10px;">
                <p>Trajet avec le conducteur <strong><%= t.getConducteur().getPrenom() %></strong> vers <strong><%= t.getDestination() %></strong>.</p>
                <form action="<%= contextPath %>/trajet/terminer" method="post">
                    <input type="hidden" name="trajetId" value="<%= t.getId() %>">
                    <button type="submit" style="background:#dc3545;">Terminer le trajet</button>
                    <small>Cliquez ici une fois arrivé à destination.</small>
                </form>
            </div>
            <% } %>
        <% } %>
        </div>

        <% if(trajetsARater != null && !trajetsARater.isEmpty()) { %>
            <hr/>
            <h3>Laisser un avis (Trajets terminés)</h3>
            <% for(Trajet t : trajetsARater) { %>
            <div style="background-color:#e2e3e5; padding: 15px; border-radius:5px; margin-bottom:10px;">
                <p>Notez le conducteur <strong><%= t.getConducteur().getPrenom() %></strong> pour votre trajet vers <%= t.getDestination() %>.</p>
                <form action="<%= contextPath %>/utilisateur/noter" method="post">
                    <input type="hidden" name="trajetId" value="<%= t.getId() %>">
                    <input type="hidden" name="evalueId" value="<%= t.getConducteur().getId() %>">
                    <select name="note" required>
                        <option value="5">5 - Excellent</option>
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
                    <th>Conducteur</th>
                    <th>Note donnée</th>
                    <th>Note reçue</th>
                </tr>
                <% for(Trajet t : trajetsPasses) {
                    Notation noteDonnee = notationDao.trouverParTrajetEtEvaluateur(t.getId(), u.getId());
                    Notation noteRecue = notationDao.trouverParTrajetEtEvaluateur(t.getId(), t.getConducteur().getId());
                %>
                <tr>
                    <td><%= t.getDateCreation() %></td>
                    <td><%= t.getPointRendezVous().getNom() %></td>
                    <td><%= t.getDestination() %></td>
                    <td><%= t.getConducteur().getPrenom() %></td>
                    <td><%= noteDonnee != null ? noteDonnee.getNote() + "/5" : "-" %></td>
                    <td><%= noteRecue != null ? noteRecue.getNote() + "/5" : "-" %></td>
                </tr>
                <% } %>
            </table>
        <% } %>
    </div>
    <script>
        var contextPath = "<%= contextPath %>";
        var enAttenteSignalement = <%= actif != null %>;

        function trouverPlusProche(lat, lon) {
            var select = document.getElementById("pointRdvId");
            var plusProche = null;
            var minDist = Number.MAX_VALUE;
            for (var i = 0; i < select.options.length; i++) {
                var opt = select.options[i];
                var optLat = parseFloat(opt.getAttribute("data-lat"));
                var optLon = parseFloat(opt.getAttribute("data-lon"));
                if (isNaN(optLat) || isNaN(optLon)) {
                    continue;
                }
                var dist = Math.pow(optLat - lat, 2) + Math.pow(optLon - lon, 2);
                if (dist < minDist) {
                    minDist = dist;
                    plusProche = opt;
                }
            }
            return plusProche;
        }

        function selectionManuelle() {
            document.getElementById("latitude").value = "";
            document.getElementById("longitude").value = "";
            document.getElementById("gpsStatus").textContent = "";
        }

        function utiliserGPS() {
            var status = document.getElementById("gpsStatus");
            if (!navigator.geolocation) {
                status.textContent = "GPS non disponible sur ce navigateur.";
                return;
            }
            status.textContent = "Localisation en cours...";
            navigator.geolocation.getCurrentPosition(function(position) {
                var lat = position.coords.latitude;
                var lon = position.coords.longitude;
                document.getElementById("latitude").value = lat;
                document.getElementById("longitude").value = lon;

                var pointProche = trouverPlusProche(lat, lon);
                if (pointProche) {
                    document.getElementById("pointRdvId").value = pointProche.value;
                    status.textContent = "Point le plus proche sélectionné : " + pointProche.text + ".";
                } else {
                    status.textContent = "Position GPS enregistrée (" + lat.toFixed(4) + ", " + lon.toFixed(4) + ").";
                }
            }, function() {
                status.textContent = "Impossible d'obtenir la position. Choisissez un point manuellement.";
            });
        }

        function afficherSignalementEnAttente(data) {
            document.getElementById("zone-signalement").innerHTML =
                "<div style=\"background-color:#d4edda; padding: 15px; border-radius:5px;\">" +
                "<h3>Signalement en cours de diffusion...</h3>" +
                "<p>Vous êtes au point : <strong>" + data.pointNom + "</strong></p>" +
                "<p>Destination : <strong>" + data.destination + "</strong></p>" +
                "<p>Les conducteurs autour de vous sont alertés.</p>" +
                "<hr><h4>Code d'identification secret à échanger avec le conducteur :</h4>" +
                "<div style=\"font-size:2em; font-weight:bold; letter-spacing: 5px; color:#2c3e50;\">" + data.code + "</div>" +
                "</div>";
        }

        function afficherTrajetEnCours(data) {
            document.getElementById("zone-trajets-en-cours").innerHTML =
                "<h3>Trajets en cours</h3>" +
                "<div style=\"background-color:#fff3cd; padding: 15px; border-radius:5px; margin-bottom:10px;\">" +
                "<p>Un conducteur vous a pris en charge ! Trajet avec <strong>" + data.conducteurPrenom + "</strong> vers <strong>" + data.destination + "</strong>.</p>" +
                "<form action=\"" + contextPath + "/trajet/terminer\" method=\"post\">" +
                "<input type=\"hidden\" name=\"trajetId\" value=\"" + data.id + "\">" +
                "<button type=\"submit\" style=\"background:#dc3545;\">Terminer le trajet</button>" +
                "<small>Cliquez ici une fois arrivé à destination.</small></form></div>";
        }

        function rafraichirStatutPassager() {
            fetch(contextPath + "/api/statut-passager", { credentials: "same-origin" })
                .then(function(response) {
                    if (!response.ok) {
                        throw new Error("statut " + response.status);
                    }
                    return response.json();
                })
                .then(function(data) {
                    if (data.enAttente) {
                        enAttenteSignalement = true;
                        afficherSignalementEnAttente(data);
                    } else if (data.trajetEnCours) {
                        if (enAttenteSignalement) {
                            document.getElementById("zone-signalement").innerHTML =
                                "<div style=\"background-color:#cce5ff; padding: 15px; border-radius:5px;\">" +
                                "<h3>Conducteur trouvé !</h3>" +
                                "<p>Votre code a été validé. Bon voyage avec <strong>" + data.trajetEnCours.conducteurPrenom + "</strong>.</p></div>";
                            enAttenteSignalement = false;
                        }
                        afficherTrajetEnCours(data.trajetEnCours);
                    }
                })
                .catch(function(err) {
                    console.error("Erreur polling passager:", err);
                });
        }

        rafraichirStatutPassager();
        setInterval(rafraichirStatutPassager, 3000);
    </script>
</body>
</html>
