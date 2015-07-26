/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import credentials.Database;
import static credentials.Database.getConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import services.ProductsREST;

/**
 *
 * @author Wayne
 */

@ApplicationScoped
public class ProductList {
    private List<Product> productList;
    
    public ProductList() {
        productList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM products");
           
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("productId"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getInt("quantity")
                );
                productList.add(p);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ProductsREST.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public JsonArray toJSON() {
        JsonArrayBuilder json = Json.createArrayBuilder();
        for (Product p : productList)
            json.add(p.toJSON());
        return json.build();
    }
    
    public void add(Product product) throws Exception {
        int rowsUpdated = Database.doUpdate("INSERT INTO products VALUES (?, ?, ?, ?)",
                String.valueOf(product.getProductId()),
                product.getName(),
                product.getDescription(),
                String.valueOf(product.getQuantity())
        );
        if (rowsUpdated ==1) {
            productList.add(product);
        } else throw new Exception("Error inserting product.");
    }
    
    public void remove(Product product) {
        remove(product.getProductId());
    }
    
    public void remove(int id) {
        Product original = get(id);
        int rowsUpdated = Database.doUpdate("DELETE FROM products WHERE productId = ?", String.valueOf(id));
        if (rowsUpdated > 0) {
            productList.remove(original);
        }
    }
    
    public void set(int id, Product product) throws Exception {
        int rowsUpdated = Database.doUpdate("UPDATE products SET name = ?, description = ?, quantity = ? WHERE productId = ?",
                product.getName(),
                product.getDescription(),
                String.valueOf(product.getQuantity()),
                String.valueOf(id)
        );
        if (rowsUpdated == 1) {
            Product original = get(id);
            int productId = original.getProductId();
            //Copy values to memory
            original.setName(product.getName());
            original.setDescription(product.getDescription());
            original.setQuantity(product.getQuantity());
        } else throw new Exception("Error updating product");
    }
    
    public Product get(int id) {
        Product result = null;
        for (int i = 0; i <productList.size() && result == null; i++) {
            Product p = productList.get(i);
            if (productList.get(i).getProductId() == id) {
                result = p;
            }
        }
        return result;
    }
}
