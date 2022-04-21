package hu.kristof.nagy.hikebookserver.api.routes;

import hu.kristof.nagy.hikebookserver.model.BrowseListItem;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.model.routes.EditedUserRoute;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import hu.kristof.nagy.hikebookserver.service.route.userroute.UserRouteCreateService;
import hu.kristof.nagy.hikebookserver.service.route.userroute.UserRouteDeleteService;
import hu.kristof.nagy.hikebookserver.service.route.userroute.UserRouteEditService;
import hu.kristof.nagy.hikebookserver.service.route.userroute.UserRouteLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users/routes/")
public class UserRouteController {

    @Autowired
    private UserRouteCreateService userRouteCreateService;

    @Autowired
    private UserRouteLoadService userRouteLoadService;

    @Autowired
    private UserRouteDeleteService routeDelete;

    @Autowired
    private UserRouteEditService routeEdit;

    @PutMapping("{userName}/{routeName}")
    public ResponseResult<Boolean> createUserRoute(
            @PathVariable String userName,
            @PathVariable String routeName,
            @RequestBody UserRoute userRoute
    ) {
        return userRouteCreateService.createRoute(userRoute);
    }

    @GetMapping("{userName}")
    public ResponseResult<List<UserRoute>> loadUserRoutes(
            @PathVariable String userName
    ) {
        return userRouteLoadService.loadUserRoutes(userName);
    }

    @GetMapping("{userName}/{routeName}")
    public ResponseResult<UserRoute> loadUserRoute(
            @PathVariable String userName,
            @PathVariable String routeName
    ) {
        return userRouteLoadService.loadUserRoute(userName, routeName);
    }

    @DeleteMapping("{userName}/{routeName}")
    public ResponseResult<Boolean> deleteUserRoute(
            @PathVariable String userName,
            @PathVariable String routeName
    ) {
        return routeDelete.deleteUserRoute(userName, routeName);
    }

    @PutMapping("edit/{userName}/{oldRouteName}")
    public ResponseResult<Boolean> editUserRoute(
        @PathVariable String userName,
        @PathVariable String oldRouteName,
        @RequestBody EditedUserRoute editedUserRoute
    ) {
        return routeEdit.editRoute(editedUserRoute);
    }

    @GetMapping("browse/{userName}")
    public ResponseResult<List<BrowseListItem>> listUserRoutes(
            @PathVariable String userName
    ) {
        return userRouteLoadService.listUserRoutes(userName);
    }
}
