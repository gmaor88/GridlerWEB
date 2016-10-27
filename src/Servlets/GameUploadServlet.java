package Servlets;

import Logic.GameManager;
import Utils.*;
import com.google.gson.Gson;
import jaxb.GameDescriptor;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * Created by Maor Gershkovitch on 10/27/2016.
 */
@WebServlet(name = "GameUploadServlet", urlPatterns = "/GameUploadServlet")
@MultipartConfig
public class GameUploadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response){
        try (PrintWriter out = response.getWriter()) {
            Collection<Part> parts = request.getParts();
            Part fileContent = parts.iterator().next();
            InputStream fileContentStream = fileContent.getInputStream();
            String usernameFromSession = SessionUtils.getUsername(request);
            try {
                GameDescriptor gameDescriptor = JaxBGridlerClassGenerator.FromXmlStreamToObject(fileContentStream);
                String gameTitle = gameDescriptor.getDynamicMultiPlayers().getGametitle();

                if (isGameExist(gameTitle)){
                    throw new GameLoadException("Game already exists");
                }

                GameLoader loader = new GameLoader();
                GameManager gameManager = ServletUtils.getGameManager(getServletContext());
                gameManager.addGameRoom(gameTitle, loader.loadGameRoom(gameDescriptor, usernameFromSession));
            } catch (GameLoadException e) {
                setError(response, e.getMessage());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setError(HttpServletResponse response, String i_Message) {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            ErrorResponse errorResponse = new ErrorResponse(i_Message);
            String json = gson.toJson(errorResponse);
            out.println(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isGameExist(String i_GameTitle) {
        GameManager gameManager = ServletUtils.getGameManager(getServletContext());

        return gameManager.m_GameRooms.containsKey(i_GameTitle);
    }

    class ErrorResponse{
        private final String errorText;

        public ErrorResponse(String i_Message){
            errorText = i_Message;
        }
    }
}

