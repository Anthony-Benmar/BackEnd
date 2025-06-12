package com.bbva.config;

import com.bbva.resources.*;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class JaxRsApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(HealthResource.class);
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
        classes.add(AdaResources.class);
        classes.add(DictionaryGenerationResources.class);
        classes.add(UseCaseTrackingResources.class);
        classes.add(JobResources.class);
        classes.add(JiraValidatorResources.class);
        classes.add(DocumentGeneratorResources.class);
        classes.add(ReliabilityResource.class);
        classes.add(UseCaseResources.class);
        return classes;
    }
}