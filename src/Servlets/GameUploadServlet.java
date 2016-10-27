package Servlets;

import Logic.GameManager;
import Utils.GameLoadException;
import Utils.JaxBGridlerClassGenerator;
import Utils.ServletUtils;
import Utils.SessionUtils;
import jaxb.GameDescriptor;

import javax.servlet.ServletException;
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
@WebServlet(name = "GameUploadServlet")
public class GameUploadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response){
        try (PrintWriter out = response.getWriter()) {
            Collection<Part> parts = request.getParts();
            Part fileContent = parts.iterator().next();
            String fileName = fileContent.getName();
            InputStream fileContentStream = fileContent.getInputStream();
            String usernameFromSession = SessionUtils.getUsername(request);
            try {
                GameDescriptor gd = JaxBGridlerClassGenerator.FromStreamToObject(fileName, fileContentStream);
                String title = gd.getDynamicMultiPlayers().getGametitle();
                if (isGameExist(title)){
                    throw new GameLoadException("Game already exists");
                }
                GameManager gameManager = ServletUtils.getGameManager(getServletContext());

                //GameEntry gameEntry = new GameEntry(gd, usernameFromSession);
                //addGameEntry(gameEntry);
            } catch (GameLoadException gle) {
                setError(response, gle.getMessage());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

