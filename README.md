# Development environment setup

* [Introduction](##Introduction)
* [Prerequisites](##Prerequisites)
* [Suggested tools](##Suggested tools)

## Introduction

All development environment configurations are stored into the [../../docker/](../../docker/) folder.

Developer environment can be managed using docker-compose and docker standard commands.

* Main docker-compose definition file [../../docker/docker-compose.yml](../../docker/docker-compose.yml): declare projects 3rd party dependencies required
  for project execution and define ambassador services for Infinity editorial-app, input-api and editorial-api expected 
  to be running on the host machine.
  
  Developer will be able to access services using urls:
    
  * `edit.infinity.local.audienceview.com:80`: editorial app
  * `proxy.infinity.local.audienceview.com:80`: reverse proxy dashboard (for debugging purpose) 
  * `edit.api.infinity.local.audienceview.com:80`: ambassador to Java service editorial-api running on host or in docker container
  * `input.api.infinity.local.audienceview.com:80`: ambassador to Java service input-api running on host or in docker container
  * `localstack.infinity.local.audienceview.com:80`: localstack dashboard (for debugging purpose)
  * `localstack.infinity.local.audienceview.com:4575`: localstack SNS
  * `localstack.infinity.local.audienceview.com:4576`: localstack SQS
  * `database.infinity.local.audienceview.com:3306`: mysql database
  * `listener-stub.infinity.local.audienceview.com:80`: a logger service listening on port 80 and echoing requests to docker logs  
  
* [../../docker/.env.dist](../../docker/.env.dist) declare base environment required by docker-compose for proper environment deployment.
  This file is expected to be copied as `.env` file and modified based on per-dev preferences.
  `COMPOSE_FILE=` env variable configured in the file can be used to change development-environment shape, declaring
  which services to be built and ran as docker container and which services to run on the host machine for development
  
  By default `.env.dist` declares all services as docker containers.
  
  Common configuration based on development role:
  
  * Front-end developer: execute java-services as built container and react application on the host  
      ```
      COMPOSE_FILE=docker-compose.yml:built/api-editorial.yml:built/api-input.yml:built/processor-input.yml:built/processor-output.yml
      ```
      
  * Java developer: execute java-services as built container and react application on the host  
      ```
      COMPOSE_FILE=docker-compose.yml:built/app-editorial.yml
      ```

  * Full-stack developer: execute all services on host  
      ```
      COMPOSE_FILE=docker-compose.yml
      ```

* [../../docker/built/](../../docker/built/) folder contains docker-compose yml definition to merge into main one to declare services to run as docker containers      

* [../../docker/config](../../docker/config) contains configuration files used by docker dependencies

  Notable ones:

  * [../../docker/config/database/](../../docker/config/database/) folder contains mysql database initialization scripts and schema definition      

## Prerequisites

### Prerequisites for Windows host

* __Docker Desktop for Windows__

  Installed and configured for linux container usage. Please refer to official documentation for instructions
  
* __Git for Windows__

  Installed using default configuration. Installation should include `Git BASH` 

### Prerequisites for Mac

* __Docker Desktop for Mac__

  Installed and configured for linux container usage. Please refer to official documentation for instructions

## Suggested tools

* __IntelliJ IDEA 2019.3__ for java services development

  project provides running configuration and declared plugin dependencies. If you plan to use a different IDE
  (e.g. Eclipse) you have to take care to setup run configuration by yourself. 
  Appendix provides instruction to manually run gradle using `Bash` (or `Git BASH` / `MINGW64` shell bundled in Git for Windows)
  as alternative.

* __PHPStorm__ for react app development


## First-run steps

1) Choose a root folder to use for project checkout. We'll refer to this as PROJECT_ROOT folder.
 
    e.g. `$USERPROFILE/Development/infinity` for Windows
    e.g. `~/Development/infinity` for Windows
   
1) Open a `Bash` shell (`GIT Bash` for Windows)
   
1) Clone infinity repository into PROJECT_ROOT folder and move into it `docker/` sub-directory

    For Windows:
    ```
    User@WinPC MINGW64 ~
    $ git clone https://github.audienceview.io/Infinity/Experience.git $USERPROFILE/Development/infinity
    $ cd $USERPROFILE/Development/infinity/docker
    ```
   
    For Mac/Linux:
    ```
    ~
    $ git clone https://github.audienceview.io/Infinity/Experience.git ~/Development/infinity
    $ cd ~/Development/infinity/docker
    ```
   
 
1) Append the following local resolution into hosts file

    For Windows: `C:/Windows/System32/drivers/etc/hosts`
    For Mac/Linux: `/etc/hosts`

    ```
    # Added for Infinity dev-env
    127.0.0.1 edit.infinity.local.audienceview.com
    127.0.0.1 proxy.infinity.local.audienceview.com
    127.0.0.1 edit.api.infinity.local.audienceview.com
    127.0.0.1 input.api.infinity.local.audienceview.com
    127.0.0.1 localstack.infinity.local.audienceview.com
    127.0.0.1 database.infinity.local.audienceview.com
    127.0.0.1 listener-stub.infinity.local.audienceview.com
    # End of section
    ```
1) Clone `.env.dist` file as `.env` and edit `COMPOSE_FILE=` env value based on your needs

    ```
    ~/Development/infinity/docker
    $ cp .env.dist .env
    $ vi .env
    ```
   
1) Startup dev-env dependencies with docker-compose
 
    ```
    ~/Development/infinity/docker
    $ docker-compose up -d --build
    ```
   
    Images will be built and container created.
   
    **PLEASE NOTE for Windows users:** based on your current _Docker Desktop_ you might be asked to include directory into Docker Desktop
    shared folder. It's mandatory to allow it. (Ref. https://docs.docker.com/docker-for-windows/#file-sharing)
   
    In case previous command fails due to volume permission failure, make sure to execute it again after shared folders
    have been properly configured.
   
    Expected output command is
   
    ```
    $ docker-compose up -d --build
    Creating network "infinity_default" with the default driver
    Creating volume "infinity_localstack-data" with default driver
    Building app-editorial
    Step 1/12 : FROM node:10-alpine as react-build
   
    ...omissis...
   
    Successfully built 7abcd21f0e7b
    Successfully tagged infinity/app-editorial:dev
    Creating infinity_localstack_1      ... done
    Creating infinity_init-localstack_1 ... done
    Creating infinity_database_1        ... done
    Creating infinity_api-editorial_1   ... done
    Creating infinity_app-editorial_1   ... done
    Creating infinity_api-input_1       ... done
    Creating infinity_proxy_1           ... done
    Creating infinity_listener-stub_1   ... done

    ```

## Build and Run Java services from shell (discouraged)

Projects use gradle to manage build and execution. IntelliJ IDEA offers useful plugin to manage builds and projects execution.
IDE setup instruction are attached in appendix and replace the following section. 
As common quick references, the following section provides instructions to manually build and run using gradle from command line. 

Operations assume commands to be execute in a `Bash` shell (`GIT Bash` shell for Window) having current working directory 
in PROJECT_ROOT `projects/java` sub-folder. 

The following sections assumes OpenJDK 8+ to be installed and $JAVA_HOME properly defined.

E.g.

1) Open `Bash` shell

1) Move in `projects/java` sub-folder

    ```
    ~
    $ cd ~/Development/infinity/projects/java
    ```
   
1) load required environment variables

    ```
    ~/Development/infinity/projects/java
    $ set -o allexport 
    $ source ../../docker/built/java-common.env
    $ set +o allexport
    ```

1) Startup services

    ```
    ~/Development/infinity/projects/java
    $ ./gradlew bootRun --parallel
    ```

## Common operations on dependency containers     

Operations assume command to be execute in a `Bash` shell (`GIT Bash` shell for Window) having current working directory in PROJECT_ROOT 
`docker/` sub-folder.

E.g.

1) Open `Bash` shell

2) Move in `docker/` sub-folder

    ```
    User@WinPC MINGW64 ~
    $ cd ~/Development/infinity/docker/
    ```
   
Standard docker / docker-compose command may be used. Please refer to official documentation. The following are just 
common commands for fast reference.
   
### Get containers status

* `docker-compose ps`
   
### Cleanup dependencies state (database, queues state...)

* `docker-compose down -v --remove-orphans && docker-compose up -d --build --remove-orphans`

### Inspect (and follow) containers logs

* from containers creation: `docker-compose logs -f`

* from command execution: `docker-compose logs -f --tail=0`

The above commands allow to specify a specific service to inspect logs. (e.g. `docker-compose logs -f proxy`)

### Stop containers

* `docker-compose stop`

### Start containers

* `docker-compose up -d --build --remove-orphans`

### Delete environment

* `docker-compose down -v --remove-orphans`

## Setup IntelliJ IDEA project

1) Import a new Project

1) Select `projects/java` sub-folder of PROJECT_ROOT in 'Import project' screen as root folder for IntelliJ IDEA and click OK

1) Select `import project from external model` with `Gradle` as type

1) Click Finish

1) A popup with text "Gradle project need to be import" will appear. Select "Import project". It might take some time.

1) A popup will notify about __missing plugins__ eventually. Make sure to install all suggested plugins and the restart IntelliJ IDEA

1) Click on __File > Settings__ and move into settings section __Build, Execution, Deployment > Compiler__ section. 

1) Make sure `Build project automatically` and `Compile indipendent modules in parallel` to be checked and save changes.

1) Type __CTRL + Shift + A__ to open action popup and type "Registry"

1) Search for `compiler.automake.allow.when.app.running` param and enable it. Close once done.

### Run configurations

The following run configurations are provided:

* `java [bootRun]`: build and execute all services. Configuration is the equivalent of `./gradlew bootRun` command.

   __This is the suggested command to execute.__ The following 'java`-prefixed run commands replace this one running just one of the services
   separately
      
* `java [:editorial-api:bootRun]`: build and execute editorial-api service only. Run configuration is the equivalent of `./gradlew :editorial-api:bootRun` command.

   __Might not be executed when `java [bootRun]` is running.__

* `java [:input-api:bootRun]`: build and execute input-api service only. Run configuration is the equivalent of `./gradlew :input-api:bootRun` command.

   __Might not be executed when `java [bootRun]` is running.__

* `java [:input-processor:bootRun]`: build and execute input-processor service only. Run configuration is the equivalent of `./gradlew :input-processor:bootRun` command.

   __Might not be executed when `java [bootRun]` is running.__

* `java [:output-processor:bootRun]`: build and execute editorial-api service only. Run configuration is the equivalent of `./gradlew :output-processor:bootRun` command.

   __Might not be executed when `java [bootRun]` is running.__

* `docker: dependencies-only`: allow to deploy docker dependencies-only through IntelliJ IDEA Docker plugin.
 
   To be used currently in Windows requires to expose Docker Engine API rest api (not secure) or to configure TLS. For more info
   
   Ref: https://www.jetbrains.com/help/idea/docker-connection-settings.html
   
   Ref: https://docs.microsoft.com/it-it/virtualization/windowscontainers/manage-docker/configure-docker-daemon
   
   Ref: https://stefanscherer.github.io/protecting-a-windows-2016-docker-engine-with-tls/
   

* `docker: dependencies-and-editorial-app`: allow to deploy docker dependencies-only AND editorial-app through IntelliJ IDEA Docker plugin.

* `docker: all-services-built`: allow to deploy all services as docker container through IntelliJ IDEA Docker plugin.


### Debug instructions

While running one of the Run configurations provided, java running processes are attachable.

Please select __Run > Attach to process__ to perform a debug session
