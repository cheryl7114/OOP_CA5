package org.example.oop_ca5.DAOs;

import org.example.oop_ca5.DTOs.ImageMetadata;
import org.example.oop_ca5.Exceptions.DaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MySqlImageDao extends MySqlDao {
    
    public List<ImageMetadata> getAllImages() throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<ImageMetadata> imagesList = new ArrayList<>();
        
        try {
            connection = this.getConnection();
            String query = "SELECT * FROM image";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                int id = resultSet.getInt("imageID");
                String name = resultSet.getString("name");
                String filename = resultSet.getString("filename");
                int carID = resultSet.getInt("carID");
                
                ImageMetadata image = new ImageMetadata(id, name, filename, carID);
                imagesList.add(image);
            }
            
        } catch (Exception e) {
            throw new DaoException("Error getting images from database: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                throw new DaoException("Error closing database resources: " + e.getMessage());
            }
        }
        
        return imagesList;
    }
}