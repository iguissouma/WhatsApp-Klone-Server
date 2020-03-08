# Whatsapp Klone Server

[//]: # (head-end)

### Running locally

To start the server:

* Run `WhatsappApplication.kt` directly from your IDE
* Alternatively you can also use the spring boot plugin from the command line in the root examples directory.
From the root WhatsApp-Clone-Server directory you can run the following:
```shell script
./mvnw spring-boot:run
```


## Using Docker to simplify development (optional)

You can use Docker to improve your development experience.
to start a postgresql database in a docker container, run:

```shell script
docker-compose up -d
```
    
To stop it and remove the container, run:
```shell script
docker-compose down
``` 

Once the app has started you can explore the example schema by opening the GraphQL Playground endpoint at http://localhost:4000/playground.

If you want to use the ready React application with it, after finishing those steps, follow the instructions on the on the [React repo](https://github.com/iguissouma/WhatsApp-Clone-Client-React).


###Resources
* [graphql kotlin getting started](https://expediagroup.github.io/graphql-kotlin/docs/getting-started.html)
* [WhatsApp Clone Tutorial](https://www.tortilla.academy/Urigo/WhatsApp-Clone-Tutorial/master)
* [authentication and authorization using jwt on spring webflux](https://medium.com/@ard333/authentication-and-authorization-using-jwt-on-spring-webflux-29b81f813e78)
