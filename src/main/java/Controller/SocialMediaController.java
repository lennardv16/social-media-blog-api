package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
/**
 * Endpoints:
 *
 * POST localhost:8080/register: post a new user account. a new account should
 * be contained in the body of the request as a JSON representation, but without
 * an account_id. example:
 *      {
 *          "username": "John",
 *          "password": "Doe"
 *      }
 *
 * POST localhost:8080/login
 *
 * POST localhost:8080/messages
 *
 * GET localhost:8080/messages: retrieve all messages
 *
 * GET localhost:8080/messages/{message_id}: retrieve all messages by id:
 *
 * DELETE localhost:8080/messages/{message_id}
 *
 * PATCH localhost:8080/messages/{message_id}
 *
 * GET localhost:8080/accounts/{account_id}/messages
 */
public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;

    public SocialMediaController() {
        // Instantiate service class
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::postAccountHandler);
        app.post("/login", this::postLoginHandler);
        app.post("/messages", this::postMessagesHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessagesByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessagesByIdHandler);
        app.patch("/messages/{message_id}", this::updateMessagesByIdHandler);
        app.get("accounts/{account_id}/messages", this::getAllMessagesByUserHandler);

        return app;
    }

    public void postAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Account acc = om.readValue(ctx.body(), Account.class);
        if (acc.getUsername().isBlank() || acc.getPassword().length() < 4 || acc.getAccount_id() != 0) {
            ctx.status(400);
        } else {
            Account addedAcc = accountService.createAccount(acc);
            if (addedAcc == null) {
                ctx.status(400);
            } else {
                ctx.status(200).json(addedAcc);
            }
        }
    }

    public void postLoginHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Account acc = om.readValue(ctx.body(), Account.class);
        Account loggedInAccount = accountService.loginAccount(acc);
        if (loggedInAccount != null && loggedInAccount.getUsername().equals(acc.getUsername()) && loggedInAccount.getPassword().equals(acc.getPassword())) {
            ctx.status(200).json(loggedInAccount);
        } else {
            ctx.status(401);
        }
    }

    public void postMessagesHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Message message = om.readValue(ctx.body(), Message.class);
        if (message.getMessage_text().isBlank() || message.getMessage_text().length() >= 255 || message.getPosted_by() == 0) {
            ctx.status(400);
        } else {
            Message createdMessage = messageService.createMessage(message);
            if (createdMessage != null) {
                ctx.status(200).json(createdMessage);
            } else {
                ctx.status(400);
            }
        }
    }

    public void getAllMessagesHandler(Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        ctx.status(200).json(messages);
    }

    public void getMessagesByIdHandler(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("message_id"));
            Message message = messageService.getMessageById(id);
            ctx.status(200).json(message != null ? message : "");
        } catch (NumberFormatException e) {
            ctx.status(400);
        }
    }

    public void deleteMessagesByIdHandler(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("message_id"));
            Message deletedMessage = messageService.deleteMessageById(id);
            ctx.status(200).json(deletedMessage != null ? deletedMessage : "");
        } catch (NumberFormatException e) {
            ctx.status(400);
        }
    }

    public void updateMessagesByIdHandler(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("message_id"));
            Message message = messageService.getMessageById(id);
            if (message == null) {
                ctx.status(400);
                return;
            }
            ObjectMapper om = new ObjectMapper();
            Message updatedMessage;
            try {
                updatedMessage = om.readValue(ctx.body(), Message.class);
            } catch (JsonProcessingException e) {
                ctx.status(400);
                return;
            }
            if (updatedMessage.getMessage_text().isBlank() || updatedMessage.getMessage_text().length() >= 255) {
                ctx.status(400);
                return;
            }
            updatedMessage.setMessage_id(id);
            updatedMessage.setPosted_by(message.getPosted_by());
            updatedMessage.setTime_posted_epoch(message.getTime_posted_epoch());
            messageService.updateMessageById(id, updatedMessage);
            ctx.status(200).json(updatedMessage);
        } catch (NumberFormatException e) {
            ctx.status(400);
    }
}

    public void getAllMessagesByUserHandler(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("account_id"));
            List<Message> message = messageService.getAllMessagesByUser(id);
            ctx.status(200).json(message);
        } catch (NumberFormatException e) {
            ctx.status(400);
        }
    }

}