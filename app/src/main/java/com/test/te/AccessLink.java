package com.test.te;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccessLink {
    public static void main(String[] args) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            Connection conn = DriverManager.getConnection("jdbc:ucanaccess://C:\\Users\\LovelyWhite\\Documents\\WeChat Files\\Q1847357321\\FileStorage\\File\\2019-06\\CV3100ParameterTable.mdb","","hngddqxy67758837");
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM FunctionTableP4_ex");
            while (rs.next()) {
               System.out.println(rs.getObject(6));
           }
        } catch (Exception ex) {
            Logger.getLogger(AccessLink.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
