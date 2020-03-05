# INFINITY project

This is a multi-project repository holding all services related to infinity distribution project.

```
project root/
|
├──demo-playground/ 
|       Contains docker-compose files and configurations needed to setup a demo environment
│
├──doc/
|       Technical documentation about the project
|
├──docker/
|       Contains docker-compose files and configurations used to setup development environment
|
├──json-schema/
|       ...
|
└──projects/
    |   Projects source code root directory
    |
    ├─ java/
    |       Source code of all java services and Intellij IDEA project root
    |
    ├─ react/
    |       Editorial app and PHPStorm project root
    |
    ├─ terraform/
    |       AWS services infrastructure configuration as code
    |
    └─ node-test-server/
            A node echo server used for listener-stub dev-env service 
```

## Development environment setup

Please refer to [doc/dev-env/README.md](doc/dev-env/README.md) for complete documentation.

### Quick start

1) Clone this repo and move into it `docker/` sub-folder
   ```
   git clone https://github.audienceview.io/Infinity/Experience.git ~/Development/infinity
   cd ~/Development/infinity/docker
   ```
   
1) Copy `.env.dist` as `.env`
   ```
   cp .env.dist .env
   ```
   
1) Run services
   ```
   docker-compose up -d --build
   ```
   
1) Add local resolution into `/etc/hosts`
   ```
   cat << EOF | sudo tee -a /etc/hosts
   
   # Added for Infinity dev-env
   127.0.0.1 edit.infinity.local.audienceview.com
   127.0.0.1 proxy.infinity.local.audienceview.com
   127.0.0.1 edit.api.infinity.local.audienceview.com
   127.0.0.1 input.api.infinity.local.audienceview.com
   127.0.0.1 localstack.infinity.local.audienceview.com
   127.0.0.1 database.infinity.local.audienceview.com
   127.0.0.1 listener-stub.infinity.local.audienceview.com
   # End of section
   
   EOF
   ```

1) Open browser and navigate to editorial-app [http://edit.infinity.local.audienceview.com](http://edit.infinity.local.audienceview.com) (?!?)




