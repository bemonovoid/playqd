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
#### Built-in music player
* Playqd comes with built-in music player available at ``http://localhost:9585/playqd/index.html``
This the best example that demonstrates how the Playqd music server api is used

### Application properties
Playqd provided the following properties for customized the application:


| Key | Type | Default | Description |
| --- | --- | --- | --- |
|`playqd.library.musicDir` | `String` | *Required* | Absolute path to the root directory with audio files collection  |
|`playqd.remote.spotify.enabled` | `boolean` | `false` |  Enables the Spotify service. The service is used to get audio file metadata (artist image, album image, etc.)  |
|`playqd.remote.spotify.clientId` | `String` | *Required* |  Your account application client id  |
|`playqd.remote.spotify.clientSecret` | `String` | *Required* |  Your account application client secret  |
|`playqd.security.tokenSecret` | `String` | See `application.yaml` |  A token secret used as a signing key to sign the jwt  |
|`playqd.security.user.login` | `String` | `admin` |  Application root user  |
|`playqd.security.user.password` | `String` | `admin` |  Application root user password  |
|`playqd.workDir` | `String` | `user_home_dir/.playqd` | Absolute path to application working directory where some library metadata (like artist image, etc.) will be stored. If not provided the default working dir is resolved to system's user dir under `.playqd` folder |


[application.yaml]: <https://github.com/bemonovoid/playqd/blob/main/playqd-web-app/src/main/resources/application.yaml>