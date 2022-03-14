package hu.kristof.nagy.hikebookserver.config;

import hu.kristof.nagy.hikebookserver.service.RouteCreate;
import hu.kristof.nagy.hikebookserver.service.RouteDelete;
import hu.kristof.nagy.hikebookserver.service.RouteEdit;
import hu.kristof.nagy.hikebookserver.service.RouteLoad;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    @Bean
    public RouteCreate provideRouteCreate() {
        return new RouteCreate();
    }

    @Bean
    public RouteLoad provideRouteLoad() {
        return new RouteLoad();
    }

    @Bean
    public RouteDelete provideRouteDelete() {
        return new RouteDelete();
    }

    @Bean
    public RouteEdit provideRouteEdit() {
        return new RouteEdit();
    }
}
