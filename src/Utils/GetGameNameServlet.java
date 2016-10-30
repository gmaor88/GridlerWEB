package Utils;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Maor Gershkovitch on 10/30/2016.
 */
@WebServlet(name = "GetGameNameServlet", urlPatterns = "/GetGameNameServlet")
public class GetGameNameServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String gameName = SessionUtils.getChosenGame(request);
            GameRoomName gameRoomName = new GameRoomName(gameName);
            String json = gson.toJson(gameRoomName);
            out.println(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class GameRoomName{
        private String name;

        public GameRoomName(String i_name){
            name = i_name;
        }

    }

}
