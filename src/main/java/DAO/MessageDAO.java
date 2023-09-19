package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    /**
     * TODO: fix code to send errors when messages are >254 and when messages are created when user isnt in db
     */
    public Message createMessage(Message message) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int messageId = generatedKeys.getInt(1);
                    message.setMessage_id(messageId);
                    return message;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Message> getAllMessages() {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();

        try {
            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int messageId = rs.getInt("message_id");
                int postedBy = rs.getInt("posted_by");
                String messageText = rs.getString("message_text");
                long timePostedEpoch = rs.getLong("time_posted_epoch");

                Message message = new Message(messageId, postedBy, messageText, timePostedEpoch);
                messages.add(message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

    public Message getMessageById(int message_id) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                int postedBy = rs.getInt("posted_by");
                String messageText = rs.getString("message_text");
                long timePostedEpoch = rs.getLong("time_posted_epoch");
                return new Message(message_id, postedBy, messageText, timePostedEpoch);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Message deleteMessageById(int message_id) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            Message message = getMessageById(message_id);
            if (message != null) {
                String sql = "DELETE FROM message WHERE message_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, message_id);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    return message;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public Message updateMessageById(int message_id, Message message) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            Message existingMessage = getMessageById(message_id);
            if (existingMessage != null) {
                String sql = "UPDATE message SET posted_by = ?, message_text = ?, time_posted_epoch = ? WHERE message_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, message.getPosted_by());
                preparedStatement.setString(2, message.getMessage_text());
                preparedStatement.setLong(3, message.getTime_posted_epoch());
                preparedStatement.setInt(4, message_id);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    return getMessageById(message_id);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Message> getAllMessagesByUser(int account_id) {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();

        try {
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, account_id);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int messageId = rs.getInt("message_id");
                String messageText = rs.getString("message_text");
                long timePostedEpoch = rs.getLong("time_posted_epoch");

                Message message = new Message(messageId, account_id, messageText, timePostedEpoch);
                messages.add(message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }
}