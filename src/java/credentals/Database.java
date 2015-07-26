/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package credentals;


import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
//import services.ProductsREST;

/**
 *
 * @author Wayne
 */
public class Database {
    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String jdbc = "jdbc:mysql://mysql3.000webhost.com/a3551530_gan";
            conn = DriverManager.getConnection(jdbc, "a3551530_gan", "passw0rd"); 
        } catch (ClassNotFoundException ex) {
          //  Logger.getLogger(ProductsREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return conn;
    }
    
    public static String getResults(String query, String... params) {
        String result = "";
        
        try (Connection conn = getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            
            ResultSet rs = pstmt.executeQuery();
            StringWriter out = new StringWriter();
            JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
            JsonGenerator gen = factory.createGenerator(out);
            
            gen.writeStartArray();
            while (rs.next()) {
                gen.writeStartObject()
                    .write("productId", rs.getInt("productID"))
                    .write("name", rs.getString("name"))
                    .write("description", rs.getString("description"))
                    .write("quantity", rs.getInt("quantity"))
                    .writeEnd();
            }
            gen.writeEnd();
            gen.close();
            result = out.toString();
            
        } catch (SQLException ex) {
         //   Logger.getLogger(ProductsREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public static int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection conn = getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
        //    Logger.getLogger(ProductsREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }
    
    public static int getLastInsertID() {
        int id = 1;
        try (Connection conn = getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT `productId` FROM products ORDER BY `productId` DESC LIMIT 1");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                id = rs.getInt("productId");
            }
        } catch (SQLException ex) {
       //     Logger.getLogger(ProductsREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }
}
