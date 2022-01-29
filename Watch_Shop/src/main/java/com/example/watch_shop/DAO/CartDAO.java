package com.example.watch_shop.DAO;

import com.example.watch_shop.Connection.ConnectionDB;
import com.example.watch_shop.DTO.CartDTO;
import com.example.watch_shop.DTO.ProductDTO;

import java.sql.*;
import java.util.ArrayList;

public class CartDAO {

    public ArrayList<CartDTO> getAllCart(int id) throws SQLException, ClassNotFoundException {
        ArrayList<CartDTO> listCart = new ArrayList<>();
        Statement stm = ConnectionDB.connection("jdbc:mysql://localhost:3306/watchshop", "root", "1")
                .createStatement();
        ResultSet rs = stm.executeQuery("SELECT * FROM cart WHERE AccID='" + id + "' && CartStatus = 1");
        while (rs.next()) {
            CartDTO dto = new CartDTO();
            dto.setId(rs.getInt(1));
            dto.setACid(rs.getInt(2));
            dto.setPCid(rs.getInt(3));
            dto.setQuanity(rs.getInt(4));
            dto.setStatus(rs.getInt(5));

            listCart.add(dto);
        }
        return listCart;
    }

    public ArrayList<ProductDTO> getItemByID(int AccID) throws SQLException, ClassNotFoundException {
        ArrayList<CartDTO> listCart = getAllCart(AccID);
        ArrayList<ProductDTO> listItem = new ArrayList<>();
        ProductDAO dao = new ProductDAO();
        for (CartDTO dtoCart :
                listCart) {
            for (ProductDTO dtoProduct :
                    dao.GetAllProduct()) {
                if (dtoProduct.getProductID() == dtoCart.getPCid()) {
                    listItem.add(dtoProduct);
                }
            }
        }
        return listItem;
    }

    public void AddToCart(int ProductID, int AccID) throws SQLException, ClassNotFoundException {
        Connection cnn = ConnectionDB.connection("jdbc:mysql://localhost:3306/watchshop", "root", "1");
        boolean check = false;
        int quanity = 0;
        ArrayList<CartDTO> listCart = getAllCart(AccID);
        for (CartDTO dto :
                listCart) {
            if (dto.getPCid() == ProductID && dto.getACid() == AccID && dto.getStatus() == 1) {
                check = true;
                quanity = dto.getQuanity();
                break;
            } else {
                check = false;
            }
        }

        if (check) {
            PreparedStatement stm = cnn.prepareStatement("UPDATE cart SET CartQuanity= ? WHERE ProductID=? AND AccID=?");
            stm.setInt(1, quanity + 1);
            stm.setInt(2, ProductID);
            stm.setInt(3, AccID);
            stm.executeUpdate();
        } else {
            PreparedStatement stm = cnn.prepareStatement("INSERT INTO cart VALUE (?,?,?,?,?)");
            stm.setInt(1, 0);
            stm.setInt(2, AccID);
            stm.setInt(3, ProductID);
            stm.setInt(4, 1);
            stm.setInt(5, 1);
            stm.executeUpdate();
        }
    }

    public int getQuanity(int ProductID, int AccID) throws SQLException, ClassNotFoundException {
        ArrayList<CartDTO> listCart = getAllCart(AccID);
        for (CartDTO dto :
                listCart) {
            if (dto.getPCid() == ProductID && dto.getACid() == AccID) {
                return dto.getQuanity();
            }
        }
        return 0;
    }

    public void UpdateQuanity(int ProductID, int AccID, String Operator) throws SQLException, ClassNotFoundException {
        ArrayList<CartDTO> listCart = getAllCart(AccID);
        Connection cnn = ConnectionDB.connection("jdbc:mysql://localhost:3306/watchshop", "root", "1");
        int quanity = getQuanity(ProductID, AccID);
        if (Operator.equals("+")) {
            quanity++;
        } else {
            quanity--;
        }
        PreparedStatement stm = cnn.prepareStatement("UPDATE cart SET CartQuanity= ? WHERE ProductID=? AND AccID=?");
        stm.setInt(1, quanity);
        stm.setInt(2, ProductID);
        stm.setInt(3, AccID);
        stm.executeUpdate();

    }
    public void Checkout(int AccID) throws SQLException, ClassNotFoundException {
        Connection cnn = ConnectionDB.connection("jdbc:mysql://localhost:3306/watchshop", "root", "1");
        PreparedStatement stm = cnn.prepareStatement("UPDATE cart SET CartStatus= ? WHERE CartStatus=1 AND AccID=?");
        stm.setInt(1, 2);
        stm.setInt(2, AccID);
        stm.executeUpdate();

    }
}
