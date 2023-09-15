# FGA Back Archetype for Java 11 Applications

## Overview

This section covers the basics of how to use the `FGA Back Archetype for Java 11 Applications` archetype and deploy it
on Google App Engine using the [Java Standard Environment]. This archetype contains a set of classes and configuration
files that will help you with the development of this kind of applications.

## Prerequisites

These are the necessary tools in order to use this archetype.

* [Docker]
* [BBVA Artifactory]
* [BBVA Bitbucket]
* A [Google Project] (by environment)

## Migration from Java 8

If you come from Java 8 runtime, you can use this [guide](upgrade_from_java8.md) in order to upgrade your project to
Java 11 runtime.

## Project creation

First we need to create a new project based on the archetype. For that, we'll use the FGA CLI command line.

* [FGA-CLI Project creation]

In the wizard, we'll choose `Backend application` and `Java 11` options.

## Configuration

Once that you have the archetype code, you need to customize your pom.xml file.

* Artifact ID and version
  ```xml
  <project>
    ...
    <artifactId>my-artifact</artifactId>
    <version>version</version>
    ...
  </project>
  ```

* Name and description
  ```xml
  <project>
    ...
    <name>My Application</name>
    <description>Awesome description about my application</description>
    ...
  </project>
  ```

* Google project ID (for each environment)
  ```xml
  <project>
    ...
    <properties>
      ...
      <dev.appengine.app.id>dev-bbva-app-id</dev.appengine.app.id>
      <au.appengine.app.id>au-bbva-app-id</au.appengine.app.id>
      <pr.appengine.app.id>bbva-app-id</pr.appengine.app.id>
      ...
    </properties>
    ...
  </project>
  ```

## RESTful Web Services

By default, web services are located in the package `com.bbva.resources`. After create new web services, you need to 
register them into `com.bbva.config.JaxRsApplication` class.

```java
...

@Override
public Set<Class<?>> getClasses() {
    final Set<Class<?>> classes = new HashSet<>();
    classes.add(SampleResource.class);
    // Add more here
    return classes;
}

...
```

Those web services are exposed by the RESTful framework ([RESTEasy] in our case) in the 
[web.xml](src/main/webapp/WEB-INF/web.xml) file.

```xml
<servlet>
    <servlet-name>RESTEasyServlet</servlet-name>
    <servlet-class>org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher</servlet-class>
    <init-param>
        <param-name>javax.ws.rs.Application</param-name>
        <param-value>com.bbva.config.JaxRsApplication</param-value>
    </init-param>
    <init-param>
        <param-name>resteasy.servlet.mapping.prefix</param-name>
        <param-value>/v1</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>RESTEasyServlet</servlet-name>
    <url-pattern>/v1/*</url-pattern>
</servlet-mapping>
```

Where:
* `javax.ws.rs.Application` value for the Java class that register all the web services 
  (`com.bbva.config.JaxRsApplication` by default).
* `resteasy.servlet.mapping.prefix` prefix for all the web services exposed by the framework (`/v1` by default).
* `url-pattern` path regular expresion that match all the web services (`/v1/*` by default).

## Local environment

In order to work with our project in the local environment, we need FGA CLI command line.

* [FGA-CLI Running on local environment]
* [FGA-CLI Deploying to App Engine]

## Identity-Aware Proxy

This archetype uses [Identity-Aware Proxy] in order to control access to the application. In order to work with it, we
need to set the value for the property `iap.client_id` on each environment. This value can be found inside Google Cloud 
Console (APIs & Services -> Credentials) with the name `IAP-App-Engine-app`. This action have to be done for each
environment.

## Swagger

[Swagger] is the API documentation and testing tool used for GCP projects. It's based on [Open API Specification] 
(OAS 3.0).

Swagger is available in this archetype in the following path.

```http request
{HOST}/swagger/index.html
```
By default, it's only available on the DEV environment. We need to have our project running (locally or in App Engine) 
in order to have Swagger tool available.

## CI/CD

In order to use CI/CD, you have to configure the [Jenkinsfile](Jenkinsfile) file.
* Set the “UUAA” environment variable to your project UUAA.
* Set the “SAMUEL_PROJECT_NAME” environment variable to the name that it will have in [Samuel] console.

The global timeout is set by default to 60 minutes for the entire pipeline. You can modify it if necessary.

The `fga-cli` image version used by default is `latest`. If you prefer, you can set a [specific version]. This change
has to be applied in both [Jenkinsfile](Jenkinsfile) and [cloudbuild.yaml](cloudbuild.yaml) files.

## Versioning Rules

Given a version number MAJOR.MINOR.PATCH, increment the:

* MAJOR version when you make incompatible API changes,
* MINOR version when you add functionality in a backwards-compatible manner, and
* PATCH version when you make backwards-compatible bug fixes.

Additional labels for pre-release and build metadata are available as extensions to the MAJOR.MINOR.PATCH format.
Snapshot versions should have '-SNAPSHOT' suffix.

For more information, see [SemVer].

## Change log

See [CHANGELOG](CHANGELOG.md).

## Support

For any problem or bug, please contact with BBVA Google Cloud Platform Team following this [procedure].


[Java Standard Environment]: https://cloud.google.com/appengine/docs/standard/java/
[Docker]: https://docs.docker.com/get-docker/
[BBVA Artifactory]: https://platform.bbva.com/en-us/developers/engines/gcp/documentation/aditional-documentation/procedures/artifactory
[BBVA Bitbucket]: https://platform.bbva.com/gcp/documentation/1ORjud_IkSWnbawHbSpggSY2Uk0RrH1iTFQqPDQ7F-04/developer-tools/bitbucket
[Google Project]: https://cloud.google.com/docs/overview#projects
[FGA-CLI Running on local environment]: https://docs.google.com/document/d/1Gm9zsfKE5DtY7IipECjP6SBWgffBjQrfC9OD-8sqqgs/edit#heading=h.asos8mhjvuxq
[FGA-CLI Deploying to App Engine]: https://docs.google.com/document/d/1Gm9zsfKE5DtY7IipECjP6SBWgffBjQrfC9OD-8sqqgs/edit#heading=h.9r69xn7gegu2
[FGA-CLI Project creation]: https://docs.google.com/document/d/1Gm9zsfKE5DtY7IipECjP6SBWgffBjQrfC9OD-8sqqgs/edit#heading=h.ibaocs9u3dga
[specific version]: https://docs.google.com/document/d/1tRa9_N4Pk8vsJpgkvzu6baEfSo-vBMSsuAnz_B2EzaM
[SemVer]: http://semver.org/
[procedure]: https://platform.bbva.com/en-us/developers/engines/gcp/documentation/procedures/issue-support-request
[RESTEasy]: https://resteasy.github.io/
[Swagger]: https://swagger.io/
[Open API Specification]: https://swagger.io/resources/open-api/
[Samuel]: https://globaldevtools.bbva.com/samuel/ps/index.html
[Identity-Aware Proxy]: https://cloud.google.com/iap