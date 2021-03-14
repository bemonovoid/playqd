## About
Playqd is a local music server designed to serve local music library and expose its content via REST API.
Playqd reads audio file tags and builds a local database containing artists, albums, songs, etc.

### Built With
Spring Boot Framework, Hibernate 
## Get Started
Build executable spring boot jar
```shell
gradle clean bootjar
 ```
or for Windows:
```shell
gradlew clean bootjar
```
## Usage
By default, no additional configuration is required but a path to a local music folder by setting `playqd.library.location` property
```shell
java -jar playqd-{version}.jar -Dplayqd.library.location=path/to/music/folder
```
This will start a local server on port `9585` and context path `/playqd`

On start-up the application will initialize in-memory database, create schemas and then start scanning music library at location set by
`playqd.library.location` property

Default properties are defined in [application.yaml]. You can provide an external application.yaml with customized properties and run the application as follows:
```shell
java -jar playqd-{version}.jar --spring.config.location=path/to/external/application.yaml
```

#### Security
Playqd uses JWT auth to secure it's api. All requests must include`Authorization`request header with a Bearer token value. 
```
curl --location --request GET 'http://localhost:9585/playqd/api/library/songs/?albumId=6' \
--header 'Authorization: Bearer iIsImV4cCI6MTYxNTY0NjkzNH0.VOvisiCw6ho5jWshvnvJdk8wXZeRuMNE96I623y5t1s'
```
To obtain a token perform POST request to`/playqd/login`endpoint with basic auth`username:password`credentials 
Default username and password are available in [application.yaml] as`playqd.security.user.login`and`playqd.security.user.password`.
>When playqd server is exposed to public it is highly recommended you change default credentials and jwt signing key secret `playqd.security.tokenSecret`

#### API 
* Api base url: `/playqd/api`
* Api documentation (swagger-ui):`/playqd/swagger-ui.html`
#### Remote services
- `Spotify`

    Playqd supports limited Spotify api mainly used to find artist and album images
    The following `application.yaml`configuration enables the service:
    ```shell
    playqd:
      spotify:
        enabled: true
        clientId: "paste your spotify client id here"
        clientSecret: "paste your spotify client secret here"
    ```

[application.yaml]: <https://github.com/bemonovoid/playqd/blob/main/playqd-web-app/src/main/resources/application.yaml>