package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.*;

import javax.naming.spi.DirStateFactory.Result;

public class AccountDAO {
    /**
     * TODO: change code to check for duplicate user
     */
    public Account createAccount(Account account) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String checkSql = "SELECT account_id FROM account WHERE username = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setString(1, account.getUsername());
            ResultSet checkResult = checkStatement.executeQuery();

            if (checkResult.next()) {
                return null;
            }
            String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());

            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                int generatedAccountId = rs.getInt(1);
                return new Account(generatedAccountId, account.getUsername(), account.getPassword());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    /**
     * TODO: VERIFY LOGIN
     */
    public Account loginAccount(Account account) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM account where username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                String username = rs.getString("username");
                String password = rs.getString("password");

                if (username.equals(account.getUsername()) && password.equals(account.getPassword())) {
                    return new Account(accountId, username, password);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean doesAccountExist(Account account) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT account_id FROM account WHERE username = ?";
            PreparedStatement ps= connection.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
