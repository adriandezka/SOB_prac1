<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head><title>Diagnóstico Base de Datos</title></head>
<body>
<h2>Contenido Actual de la Base de Datos</h2>
<%
    Class.forName("org.apache.derby.jdbc.ClientDriver");
    Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/sob_grup_06", "root", "root");
    Statement stmt = con.createStatement();

    out.println("<h3>1. Tabla CUSTOMER:</h3><ul>");
    ResultSet rs = stmt.executeQuery("SELECT ID, USERNAME, NOMBRE FROM ROOT.CUSTOMER ORDER BY ID");
    while (rs.next()) {
        out.println("<li>ID: <b>" + rs.getLong("ID") + "</b> | User: <b>" + rs.getString("USERNAME") + "</b> | Nom: " + rs.getString("NOMBRE") + "</li>");
    }
    rs.close();
    out.println("</ul>");

    out.println("<h3>2. Tabla TOOL:</h3><ul>");
    rs = stmt.executeQuery("SELECT ID, NOM, PRIVAT, AUTOR_ID FROM ROOT.TOOL ORDER BY ID");
    while (rs.next()) {
        out.println("<li>ID: <b>" + rs.getLong("ID") + "</b> | Nom: <b>" + rs.getString("NOM") + "</b> | Privat: " + rs.getBoolean("PRIVAT") + " | Autor_ID: " + rs.getLong("AUTOR_ID") + "</li>");
    }
    rs.close();
    out.println("</ul>");

    out.println("<h3>3. Tabla PROMPT:</h3><ul>");
    rs = stmt.executeQuery("SELECT ID, TITOL, PRIVAT, AUTOR_ID FROM ROOT.PROMPT ORDER BY ID");
    while (rs.next()) {
        out.println("<li>ID: <b>" + rs.getLong("ID") + "</b> | Titol: <b>" + rs.getString("TITOL") + "</b> | Privat: " + rs.getBoolean("PRIVAT") + " | Autor_ID: " + rs.getLong("AUTOR_ID") + "</li>");
    }
    rs.close();
    out.println("</ul>");
    con.close();
%>
</body>
</html>
