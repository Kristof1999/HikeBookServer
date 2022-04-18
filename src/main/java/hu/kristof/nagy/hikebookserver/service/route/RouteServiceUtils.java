package hu.kristof.nagy.hikebookserver.service.route;

public class RouteServiceUtils {
    public static String POINTS_NOT_UNIQE = "Az útvonal pontjai nem egyediek! " +
            "Kérem, hogy más pontokat használjon.";

    public static String getRouteNameNotUniqueString(String routeName) {
        return "A(z) " + routeName + " nevű útvonal már létezik! " +
                "Kérem, hogy válasszon másik nevet.";
    }
}
