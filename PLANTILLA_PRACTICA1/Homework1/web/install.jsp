<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import = "java.sql.*" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Database SQL Load</title>
    </head>
    <style>
        .error {
            color: red;
        }
        pre {
            color: green;
        }
    </style>
    <body>
        <h2>Database SQL Load</h2>
        <%
            /* How to customize:
             * 1. Update the database name on dbname.
             * 2. Create the list of tables, under tablenames[].
             * 3. Create the list of table definition, under tables[].
             * 4. Create the data into the above table, under data[]. 
             * 
             * If there is any problem, it will exit at the very first error.
             */
            String dbname = "homework1";
            String schema = "ROOT";
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            /* this will generate database if not exist */
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/" + dbname, "root", "root");
            Statement stmt = con.createStatement();
            
            /* inserting data */
            String data[] = new String[]{
                // Usuarios iniciales (sob es obligatorio por enunciado)
                "INSERT INTO " + schema + ".CUSTOMER (NOMBRE, USERNAME, PASSWORD) VALUES ('SOB Admin', 'sob', 'sob')",
                "INSERT INTO " + schema + ".CUSTOMER (NOMBRE, USERNAME, PASSWORD) VALUES ('Lucas Sierra', 'lucas', 'lucas123')",
                "INSERT INTO " + schema + ".CUSTOMER (NOMBRE, USERNAME, PASSWORD) VALUES ('Adrian Rusu', 'adrian', 'adrian123')",

                // Herramientas iniciales (Tool)
                "INSERT INTO " + schema + ".TOOL (NOM, DESCRIPCIO, LLENGUATGE, CODI, VERSIO, DATACREACIO, PRIVAT, AUTOR_ID) VALUES ('Calculadora Python', 'Realitza calculs matemátics basiques', 'Python', 'def calcular(a, b): return a + b', '1.0', DATE('2026-11-20'), 0, 1)",
                "INSERT INTO " + schema + ".TOOL (NOM, DESCRIPCIO, LLENGUATGE, CODI, VERSIO, DATACREACIO, PRIVAT, AUTOR_ID) VALUES ('Web Scraper', 'Eina per extreure dades web', 'Python', 'import urllib.request\ndef scrape(url): return urllib.request.urlopen(url).read()', '1.2', DATE('2026-11-21'), 1, 2)",
                "INSERT INTO " + schema + ".TOOL (NOM, DESCRIPCIO, LLENGUATGE, CODI, VERSIO, DATACREACIO, PRIVAT, AUTOR_ID) VALUES ('Formatejador JSON', 'Formateja cadenes JSON', 'Python', 'import json\ndef format_json(d): return json.dumps(d, indent=2)', '2.0', DATE('2026-11-22'), 0, 1)",

                // Prompts iniciales
                "INSERT INTO " + schema + ".PROMPT (TITOL, DESCRIPCIO, PLANTILLA, DATACREACIO, PRIVAT, AUTOR_ID) VALUES ('Generador de Resums', 'Genera resums clars d un text donat', 'Resumeix el seguent text en 3 punts clau:\n{text}', DATE('2026-11-21'), 0, 1)",
                "INSERT INTO " + schema + ".PROMPT (TITOL, DESCRIPCIO, PLANTILLA, DATACREACIO, PRIVAT, AUTOR_ID) VALUES ('Analitzador de Codi Segur', 'Prompt privat per analitzar vulnerabilitats de seguretat', 'Analitza el seguent codi cercant forats de seguretat:\n{codi}', DATE('2026-11-22'), 1, 2)",

                // Etiquetas de los prompts
                "INSERT INTO " + schema + ".PROMPT_ETIQUETES (PROMPT_ID, ETIQUETA) VALUES (1, 'ia')",
                "INSERT INTO " + schema + ".PROMPT_ETIQUETES (PROMPT_ID, ETIQUETA) VALUES (1, 'resum')",
                "INSERT INTO " + schema + ".PROMPT_ETIQUETES (PROMPT_ID, ETIQUETA) VALUES (1, 'text')",
                "INSERT INTO " + schema + ".PROMPT_ETIQUETES (PROMPT_ID, ETIQUETA) VALUES (2, 'seguretat')",
                "INSERT INTO " + schema + ".PROMPT_ETIQUETES (PROMPT_ID, ETIQUETA) VALUES (2, 'python')",

                // Relación ManyToMany: Prompt <-> Tool
                "INSERT INTO " + schema + ".PROMPT_TOOL (PROMPT_ID, TOOLS_ID) VALUES (1, 1)",
                "INSERT INTO " + schema + ".PROMPT_TOOL (PROMPT_ID, TOOLS_ID) VALUES (2, 2)",

                // Compatibilidad con la plantilla original
                "INSERT INTO " + schema + ".TOPIC VALUES (NEXT VALUE FOR TOPIC_GEN, 'Computer Science')",
                "INSERT INTO " + schema + ".COMMENT VALUES (NEXT VALUE FOR COMMENT_GEN, 'Skeleton code', 1)",
                "INSERT INTO " + schema + ".COMMENT VALUES (NEXT VALUE FOR COMMENT_GEN, 'for homework1', 1)",
                "INSERT INTO " + schema + ".CREDENTIALS VALUES (NEXT VALUE FOR CREDENTIALS_GEN, 'sob', 'sob')"
            };
            for (String datum : data) {
                try {
                    stmt.executeUpdate(datum);
                    out.println("<pre> -> " + datum + "<pre>");
                } catch (SQLException sqle) {
                    out.println("<span style='color: orange;'>[Ya existe] " + datum + "</span><br/>");
                }
            }
        %>
        <button onclick="window.location='<%=request.getSession().getServletContext().getContextPath()%>'">Go home</button>
    </body>
</html>
