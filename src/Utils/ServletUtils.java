package Utils;

import Logic.GameManager;

import javax.servlet.ServletContext;

/**
 * Created by Maor Gershkovitch on 10/19/2016.
 */
public class ServletUtils {
    private static final String Game_MANAGER_ATTRIBUTE_NAME = "GameManager";

    public static GameManager getGameManager(ServletContext servletContext) {
        if (servletContext.getAttribute(Game_MANAGER_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(Game_MANAGER_ATTRIBUTE_NAME, new GameManager());
        }
        return (GameManager) servletContext.getAttribute(Game_MANAGER_ATTRIBUTE_NAME);
    }
}
