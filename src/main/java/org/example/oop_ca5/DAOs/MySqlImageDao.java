package org.example.oop_ca5.DAOs;

import org.example.oop_ca5.DTOs.ImageMetadata;
import org.example.oop_ca5.Exceptions.DaoException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MySqlImageDao extends MySqlDao {
    
    public List<ImageMetadata> getAllImages() throws DaoException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<ImageMetadata> imagesList = new ArrayList<>();
        
        try {
            conn = this.getConnection();
            stmt = conn.createStatement();
            String query = "SELECT * FROM image";
            rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String filename = rs.getString("filename");
                int carID = rs.getInt("carID");
                
                ImageMetadata image = new ImageMetadata(id, name, filename, carID);
                imagesList.add(image);
            }
            
        } catch (Exception e) {
            throw new DaoException("Error getting images from database: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                throw new DaoException("Error closing database resources: " + e.getMessage());
            }
        }
        
        return imagesList;
    }
}