/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lacal
 */
@WebServlet(urlPatterns = {"/main"})
public class main extends HttpServlet {

    String driver = "com.mysql.jdbc.Driver";
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    String userName = "root";
    String password = "";
    String URL = "jdbc:mysql://localhost/obchod";
    HttpSession session;
    Integer id_usera = 0;

    // ziska spojenie len raz a to pri stlačení tlačidla na prihlásenie
    public void ZiskajConnection() {
        try {
            if (con == null) {
                Class.forName(driver);
                con = DriverManager.getConnection(URL, userName, password);
                session.setAttribute("conn", con);
                System.out.println("Ziskam spojenie");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void vypisTovaru(PrintWriter out) {
        Integer zlava = (Integer) session.getAttribute("zlava");
        boolean striedanie = true;
        int aktCena = 0;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from sklad");
            while (rs.next()) {
                aktCena = rs.getInt("cena") * (100 - zlava) / 100;
                out.println("<form action='main' method='post'>");
                out.println("<input type='hidden' name='ID' value='" + rs.getString("ID") + "'>");
                out.println("<input type='hidden' name='cena' value='" + aktCena + "'>");

                // premenná striedanie mení dizajn
                if (striedanie) {
                    out.println("<div id='novinky' class='w3-row-padding w3-padding-64 w3-container'>");
                    out.println("<div class='w3-content'>");
                    out.println("<div class='w3-twothird'>");
                    out.println("<h1>" + rs.getString("nazov") + "</h1>");
                    out.println("<h5 class='w3-padding-32'>" + "Cena : " + aktCena + " EUR");
                    out.println("<p>Na sklade : " + rs.getString("ks") + " ks" + "</p></h5>");
                    out.println("<p class='w3-text-grey'>" + rs.getString("poznamky") + "</p>");
                    out.println("<input class='w3-button w3-black w3-padding-large w3-large w3-margin-top' type='submit' name='tlacidlo' value='Do košíka'>");
                    out.println("</div>");
                    out.println("<div class='w3-third w3-center'>");
                    out.println("<img src='images/games/" + rs.getString("obrazok") + "' width='280' height='360' >");
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                    striedanie = false;
                } else {
                    out.println("<div id='prihlasenie' class='w3-row-padding w3-light-grey w3-padding-64 w3-container'>");
                    out.println("<div class='w3-content'>");
                    out.println("<div class='w3-third w3-center'>");
                    out.println("<img src='images/games/" + rs.getString("obrazok") + "' width='280' height='360' >");
                    out.println("</div>");
                    out.println("<div class='w3-twothird'>");
                    out.println("<h1>" + rs.getString("nazov") + "</h1>");
                    out.println("<h5 class='w3-padding-32'>" + "Cena : " + aktCena + " EUR");
                    out.println("<p>Na sklade : " + rs.getString("ks") + " ks" + "</p></h5>");
                    out.println("<p class='w3-text-grey'>" + rs.getString("poznamky") + "</p>");
                    out.println("<input class='w3-button w3-black w3-padding-large w3-large w3-margin-top' type='submit' name='tlacidlo' value='Do košíka'>");
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                    striedanie = true;
                }

                out.println("</form>");

            }
            stmt.close();
        } catch (Exception e) {
            out.println("Problém s čítaním " + e.toString());
        }
    }
    //**********************************************************************

    public int OverUsera(String meno, String heslo) {
        int vysledok = 0;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("select max(id) as iid,count(id) as pocet from pouzivatelia "
                    + "where login='" + meno + "' and heslo='" + heslo + "'");

            rs.next();
            if (rs.getInt("pocet") == 1) {
                vysledok = rs.getInt("iid");
            }
            stmt.close();
        } catch (Exception ex) {
            return 0;
        }
        return vysledok;
    }

    //**********************************************************************
    public void ZapamatajUdajeOUserovi(int id_usera) {
        // nacitam potrebne udaje z databazy
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from pouzivatelia "
                    + "where id='" + id_usera + "'");
            rs.next();
            // vlozim data do session
            // session uz bola vytvorena v processRequest

            // vlozime ID
            session.setAttribute("ID", (Integer) id_usera);
            // vlozime meno a priezvisko ako jeden celok :)
            session.setAttribute("meno", rs.getString("meno") + " " + rs.getString("priezvisko"));
            // vlozime zlavu ako cislo
            session.setAttribute("zlava", (Integer) rs.getInt("zlava"));
            // vlozime adresu
            session.setAttribute("adresa", rs.getString("adresa"));
            // vlozime mail
            session.setAttribute("mail", rs.getString("mail"));
            // vlozi poznamku
            session.setAttribute("poznamky", rs.getString("poznamky"));
            // nastavim dlzku platnosti session
            session.setMaxInactiveInterval(600); // 10 minut

            stmt.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ZobrazNeopravnenyPristup(PrintWriter out) {
        try {
            out.println("Neoprávnený prístup");
        } catch (Exception ex) {
            out.println(ex.toString());
        }
    }

    //*******************************************************************************
    public void ZapisDoKosika(Integer id_usera, String id_tovaru, String cena) {
        try {
            stmt = con.createStatement();
            // zistim, ci uz tovar tam je,
            rs = stmt.executeQuery("select count(ID) as pocet from kosik where "
                    + "(ID_pouzivatela='" + id_usera + "') and "
                    + "(id_tovaru ='" + id_tovaru + "')");
            rs.next();
            int pocet = rs.getInt("pocet");

            rs = stmt.executeQuery("SELECT * FROM `sklad` WHERE ID = " + id_tovaru);
            rs.next();
            int pocet_kusov_sklad = rs.getInt("ks");

            int pocet_kusov_kosik = 0;
            if (pocet != 0) {
                rs = stmt.executeQuery("SELECT * FROM kosik WHERE ID_pouzivatela = " + id_usera);
                rs.next();
                pocet_kusov_kosik = rs.getInt("ks");
            }

            if (pocet_kusov_kosik < pocet_kusov_sklad) {

                if (pocet == 0) {
                    // ak nie vlozim ho
                    String sstr = "insert into kosik (ID_pouzivatela, id_tovaru, cena, ks) values ("
                            + "'" + id_usera + "', "
                            + "'" + id_tovaru + "', "
                            + "'" + cena + "', "
                            + "'1') ";
                    stmt.executeUpdate(sstr);
                } else {
                    // ak ano, len zvysim pocet ks
                    stmt.executeUpdate("update kosik set ks=ks+1, cena ='" + cena + "' where "
                            + "(ID_pouzivatela='" + id_usera + "') and "
                            + "(id_tovaru ='" + id_tovaru + "')");
                }
            }
            stmt.close();
        } catch (Exception e) {
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            // načíta id usera a connection ak existuje session
            session = request.getSession();
            con = (Connection) session.getAttribute("conn");
            id_usera = (Integer) session.getAttribute("ID");
                       
            if (id_usera == null) {
                if (request.getContentType() == null) {
                    ZobrazNeopravnenyPristup(out);
                    return;
                }

                String tlacidlo = (request.getParameter("tlacidlo")).substring(0, 1);
                if (tlacidlo.equals("V")) {
                    ZiskajConnection();
                    id_usera = OverUsera(request.getParameter("meno"), request.getParameter("heslo"));
                    if (id_usera == 0) {
                        ZobrazNeopravnenyPristup(out);
                        return;
                    }

                    ZapamatajUdajeOUserovi(id_usera);
                    System.out.println("Prihlásil sa uživateľ ID = " + id_usera);

                    // presmerovanie na admin rozhranie
                    if (session.getAttribute("poznamky").equals("admin")) {
                        response.sendRedirect("admin");
                    }

                }
            }

            // pustí len userov
            if (!(session.getAttribute("poznamky").equals("user"))) {
                ZobrazNeopravnenyPristup(out);
                return;
            }

            // ak nieje volane naprazdno zostava pridanie tovaru
            if (request.getContentType() != null) {
                String tlacidlo = (request.getParameter("tlacidlo")).substring(0, 1);

                // alebo o pridanie do kosika - "Do kosika"
                if (tlacidlo.equals("D")) {
                    ZapisDoKosika(id_usera, request.getParameter("ID"), request.getParameter("cena"));
                }
            }

            out.println("<html lang='en'>");
            out.println("<title>ProGamerShop.sk</title>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("<link rel='stylesheet' href='https://www.w3schools.com/w3css/4/w3.css'>");
            out.println("<link rel='stylesheet' href='https://fonts.googleapis.com/css?family=Lato'>");
            out.println("<link rel='stylesheet' href='https://fonts.googleapis.com/css?family=Montserrat'>");
            out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css'>");
            out.println("<style>");
            out.println("body,h1,h2,h3,h4,h5,h6 {font-family: 'Lato', sans-serif}");
            out.println(".w3-bar,h1,button {font-family: 'Montserrat', sans-serif}");
            out.println(".fa-anchor,.fa-coffee {font-size:200px}");
            out.println("</style>");
            out.println("<body>");

            String meno = (String) session.getAttribute("meno");

            out.println("<div class='w3-top'>");
            out.println("<div class='w3-bar w3-red w3-card w3-left-align w3-large'>");
            out.println("<a class='w3-bar-item w3-button w3-hide-medium w3-hide-large w3-right w3-padding-large w3-hover-white w3-large w3-red' href='javascript:void(0);' onclick='myFunction()' title='Toggle Navigation Menu'><i class='fa fa-bars'></i></a>");
            out.println("<a href='main' class='w3-bar-item w3-button w3-padding-large w3-white'>Obchod</a>");
            // meno
            out.println("<a href='user_objednavky' class='w3-bar-item w3-button w3-hide-small w3-padding-large w3-hover-white'>" + meno + "</a>");
            out.println("<a href='#footer' class='w3-bar-item w3-button w3-hide-small w3-padding-large w3-hover-white'>Kontakt</a>");

            // odhlasenie
            out.println("<a href='logout' class='w3-bar-item w3-button w3-hide-small w3-padding-large w3-hover-white ' >Odhlásenie</a>");

            // kosik
            out.println("<a href='kosik' class='w3-bar-item w3-hide-small w3-padding-large '><img src=\"images/shopping_cart.png\" width=\"20\" height=\"15\" ></a>");
            out.println("</div>");

            out.println("<div id='navDemo' class='w3-bar-block w3-white w3-hide w3-hide-large w3-hide-medium w3-large'>");
            out.println("<a href='user_objednavky' class='w3-bar-item w3-button w3-padding-large'>" + meno + "</a>");
            out.println("<a href='#footer' class='w3-bar-item w3-button w3-padding-large'>Kontakt</a>");
            out.println("<a href='logout' class='w3-bar-item w3-button w3-padding-large'>Odhlásenie</a>");
            out.println("<a href='kosik' class='w3-bar-item w3-button w3-padding-large'>Košík</a>");
            out.println("</div>");
            out.println("</div>");

            out.println("<header class='w3-container w3-red w3-center' style='padding:128px 16px' >");
            out.println("<h1 class='w3-margin w3-jumbo'>ProGamerShop.sk</h1>");
            out.println("<p class='w3-xlarge'>Vitajte!</p>");
            out.println("<div class='w3-xlarge w3-hide-small w3-padding-large'>" + meno + "</div> <br />");
            out.println("</header>");
            // zoznam tovaru s moznostou objednat
            vypisTovaru(out);

            out.println("<div class='w3-container w3-black w3-center w3-opacity w3-padding-64'>");
            out.println("<h1 class='w3-margin w3-xlarge'>I dont have birthdays I level up!</h1>");
            out.println("</div>");

            out.println("<footer id='footer' class='w3-container w3-padding-64 w3-center w3-opacity'>");
            out.println("<div class='w3-xlarge w3-padding-32'>");
            out.println("<a href='https://www.facebook.com/nitraprogamingshop/' target='_blank'><i class='fa fa-facebook-official w3-hover-opacity' ></i></a>");
            out.println("<a href='https://twitter.com/ProGamingShopsk' target='_blank'><i class='fa fa-instagram w3-hover-opacity'></i></a>");
            out.println("<a href='https://www.instagram.com/progamingshop_sk/' target='_blank'><i class='fa fa-twitter w3-hover-opacity'></i></a>");
            out.println("</div>");
            out.println("</footer>");
            
            out.println("<script>");
            // Used to toggle the menu on small screens when clicking on the menu button
            out.println("function myFunction() {");
            out.println("var x = document.getElementById('navDemo');");
            out.println("if (x.className.indexOf('w3-show') == -1) {");
            out.println("x.className += ' w3-show';");
            out.println("} else {");
            out.println("x.className = x.className.replace(' w3-show', '');");
            out.println("}");
            out.println("}");
            out.println("</script>");
            out.println("</body>");

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
