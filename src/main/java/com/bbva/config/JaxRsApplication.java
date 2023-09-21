package com.bbva.config;

import com.bbva.resources.*;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class JaxRsApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(AuthenticationResources.class);
        classes.add(BoardResources.class);
        classes.add(BucResources.class);
        classes.add(BuiResources.class);
        classes.add(HelloResource.class);
        classes.add(UserResource.class);
        classes.add(CatalogResources.class);
        classes.add(ExternoResources.class);
        classes.add(IssueTicketResources.class);
        classes.add(ProjectResources.class);
        classes.add(SourceResources.class);
        classes.add(TemplateResources.class);
        classes.add(LogResource.class);
        classes.add(MeshResources.class);
        classes.add(RolResource.class);
        classes.add(CorsResponseFilter.class);
        classes.add(GovernmentResources.class);
        return classes;
    }
}