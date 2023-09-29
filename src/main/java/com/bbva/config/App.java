package com.bbva.config;

import com.bbva.resources.*;

import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class App extends Application {
    private final Set<Class<?>> classes = new HashSet<>();
    private final Set<Object> singletons = Collections.emptySet();

    public App() {
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
        classes.add(ProcessResources.class);
        classes.add(BatchResources.class);
        classes.add(DictionaryGenerationResources.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
