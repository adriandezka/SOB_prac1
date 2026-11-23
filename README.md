# Pràctica 1: Back-end RESTful (Sistemes Oberts - URV)

**Autors:** Lucas Sierra & Adrián Rusu  
**Assignatura:** Sistemes Oberts (SOB) - Grau en Enginyeria Informàtica  
**Curs acadèmic:** 2026-2027  

---

## 1. Descripció General del Projecte

Aquesta pràctica implementa el **back-end RESTful** d'un repositori centralitzat de prompts d'Inteligència Artificial i eines (tools) d'execució (en Python). Els serveis estan dissenyats per ser consumits tant per usuaris humans com per clients automatitzats.

L'API Web està versionada sota el context `/rest/api/v1` i gestiona tres recursos principals:
1. **Prompts:** Plantilles reutilitzables per a models de llenguatge (LLMs).
2. **Tools (Eines):** Programes i scripts reutilitzables (actualment Python).
3. **Customers (Usuaris):** Creadors i gestors dels recursos amb autenticació i privacitat.

---

## 2. Arquitectura i Decisions de Disseny

* **Runtime:** Java JDK 17.
* **Servidor d'Aplicacions:** Eclipse GlassFish 6.2.1 (Jakarta EE 9.1 / Web Profile).
* **Persistència (JPA):** EclipseLink connectat a Apache Derby / Java DB (`localhost:1527/homework1`).
* **Serveis Web:** Jakarta RESTful Web Services (JAX-RS).
* **Serialització JSON:** Jakarta JSON Binding (JSON-B) amb formatat amb sagnat automàtic (`JsonbContextResolver`).
* **Seguretat:** Autenticació HTTP Basic Authentication mitjançant l'anotació `@Secured` i el filtre `RESTRequestFilter`.

### Decisions Clau Adoptades:
1. **Unificació d'Usuaris en `Customer`:** En lloc de mantenir dues taules separades (`Credentials` i `Customer`), s'ha unificat la seguretat perquè `RESTRequestFilter` consulti directament l'entitat `Customer` mitjançant la query `@NamedQuery("Customer.findByUser")`.
2. **Filtratge 100% en Base de Dades (JPQL):** Complint estrictament el requisit de l'enunciat, cap filtre es fa programàticament en Java. Es construeixen consultes JPQL dinàmiques (ex. filtratge d'etiquetes amb `:tag MEMBER OF p.etiquetes`).
3. **Suport HATEOAS a Customer:** A `GET /rest/api/v1/customer/{id}`, es calculen i s'inclouen enllaços `_links` a l'últim prompt i a l'última eina afegits per l'usuari.
4. **Visibilitat Pública / Privada:**
   * En llistats generals (`/prompt`, `/tool`), si la petició és anònima, els recursos privats es retornen projectats només amb els camps públics bàsics.
   * En detall (`/{id}`), si el recurs és privat i no s'aporten credencials vàlides, es retorna `401 Unauthorized`.
5. **Protecció de Credencials:** El camp de la contrasenya a `Customer` està anotat amb `@JsonbTransient`, impedint que s'exposi en cap resposta JSON.

---

## 3. Resum d'Endpoints de l'API (`/rest/api/v1`)

### Customers (`/customer`)
* `GET /rest/api/v1/customer`: Llista tots els usuaris (sense contrasenyes).
* `GET /rest/api/v1/customer/{id}`: Detall de l'usuari amb enllaços HATEOAS (`_links`). Admet cerca per ID numèric o per username.
* `PUT /rest/api/v1/customer/{id}`: *(Opcional)* Modificació d'usuari (`@Secured`).

### Tools (`/tool`)
* `GET /rest/api/v1/tool?author={author}&language={language}`: Llista eines ordenades per nom. Filtres a BD. Projecció reduïda si és privada i no autenticat.
* `GET /rest/api/v1/tool/{id}`: Detall de l'eina. `401 Unauthorized` si és privada sense login. `404 Not Found` si no existeix.
* `POST /rest/api/v1/tool`: Crea una eina (`@Secured`). Valida nom, codi i autor existent. Retorna `201 Created` amb header `Location`.
* `PUT /rest/api/v1/tool/{id}`: *(Opcional)* Modifica eina existent (`@Secured`).
* `DELETE /rest/api/v1/tool/{id}`: *(Opcional)* Esborra eina (`@Secured`).

### Prompts (`/prompt`)
* `GET /rest/api/v1/prompt?author={author}&tag={tag}`: Llista prompts ordenats per títol. Filtres a BD. Projecció reduïda si és privat i no autenticat.
* `GET /rest/api/v1/prompt/{id}`: Detall del prompt. `401 Unauthorized` si és privat sense login. `404 Not Found` si no existeix.
* `POST /rest/api/v1/prompt`: Crea un prompt (`@Secured`). Valida títol, plantilla, autor i que les eines associades existeixin a la BD. Retorna `201 Created`.
* `PUT /rest/api/v1/prompt/{id}`: *(Opcional)* Modifica prompt existent (`@Secured`).
* `GET /rest/api/v1/prompt/{id}/tools`: Llista les eines associades al prompt. `404 Not Found` si no té eines o no existeix.
* `PUT /rest/api/v1/prompt/{id}/tools`: Actualitza les eines associades a partir d'un array d'IDs (`@Secured`).

---

## 4. Guia d'Instal·lació i Execució (Pas a Pas)

Per executar i provar el projecte a NetBeans:

1. **Iniciar la Base de Dades (Apache Derby):**
   * A NetBeans, pestanya **Services** -> **Databases** -> Clic dret a **Java DB** -> **Start Server**.
2. **Iniciar GlassFish Server:**
   * Pestanya **Services** -> **Servers** -> Clic dret a **GlassFish Server** -> **Start** (esperar uns segons fins a veure la fletxa verda).
3. **Desplegar el Projecte:**
   * Pestanya **Projects** -> Clic dret a **Homework1** -> **Deploy** (o prémer **Run** / F6).
4. **Poblar les Dades de Prova Inicials:**
   * Obrir al navegador:  
     `http://localhost:8080/Homework1/install.jsp`  
     *(Això carregarà els usuaris `sob:sob`, `lucas`, `adrian`, i les eines/prompts de prova).*
5. **Comprovar l'estat de les taules (Diagnòstic):**
   * Obrir al navegador:  
     `http://localhost:8080/Homework1/test_db.jsp`

---

## 5. Bateria de Proves amb Postman (Suite Automatitzada)

A l'arrel d'aquest repositori trobaràs el fitxer:
📁 **`SOB_Prac1_Postman_Collection.json`**

### Com importar i executar les proves a Postman:
1. Obre l'aplicació **Postman**.
2. Fes clic al botó **Import** (a dalt a l'esquerra).
3. Arrossega o selecciona el fitxer `SOB_Prac1_Postman_Collection.json`.
4. Veuràs la col·lecció **"SOB - Pràctica 1 (RESTful API Tests)"** a la barra lateral.
5. Fes clic sobre els tres punts `...` de la col·lecció -> **Run collection**.
6. Fes clic a **Run SOB - Pràctica 1**.
7. Totes les asercions i codis d'estat (`200 OK`, `201 Created`, `401 Unauthorized`, `400 Bad Request`, `404 Not Found`) s'executaran automàticament en verd!
