# Tickets for sale

### Table of Contents:
1.  [Project structure](#project-structure)
2.  [Tech stack](#tech-stack)
3.  [Domain Objects](#domain-objects)
4.  [Business Logic](#business-logic)
5.  [Future Improvements](#future-improvements)
6.  [How to run](#hot-to-run)



### Project Structure

>> server-side
    
> domain
``` 
contains domain object and respective business logic
```
> db
```
Contains interfaces and implementations for embeded Mongo to do CRUD operations on ticketing system.
```
> services
```
Interfaces and implementations of services like Creating , reading and updating an Inventory of tickets.
```
> errors
```
Simple error enum to push custom errors when there is a failing effect
```
> routes
```
As the same suggest this is the place where routes are created and the services are used.
```
> server
```
It contains the initialisation script plus http4s server creation with given host and port. The routes are combined and injected here in server.
```
> Main
```
This is where what effect to use is decided and server is started.
```

>> client-side

```
There are just handfull of components and no packaging to make things as simple as possible.
FileUpload, Display inventory, and the index page are the only components.
```

### Tech stack

1. Scala 3
2. http4s server
3. Circie Json encode/decode
4. MongoDB (embeded) for store
5. sbt build tool
6. typeSafe config
7. Apache commons CSV
8. MUnit for testing
10. ReactJs with TypeScript
11. PaperCSS for UI
12. webpack 
11. babel

### Business Logic

Business logic is scatered in the domain and the service implementations. And most of it is tested using the MUnit. Tests will be sufficient enough to explain the logic implemented.


### Future Improvements
> There is lot to improve.

1. Client side written in typeScript (this was my first time) would have been well modeled and lot of effects can be manages in a better way. I mean more FP, but unfortunately I didnt had enough time to learn and make the code better.

2. Error handling can be improved a lot, though the server side error are automatically managed by custom errors and Effects but on client side it coukd have been better.

3. Modeling on server side would have been much better by using scala 3 enums rathar than creating independent case classes at some places.

4. Missing tests from client side completely due to shortage of time. Although server side tests and also not complete though those are enough to test the business.


### How to run

Make sure you have latest version of sbt and node for starters.

to run the Application just do
```
sbt runApp
```

In case there is a UI change, no need to restart server side application just do 
```
npm run-script dbuild 
```

If you want to build/install the UI and serverside both together and start the application do
```
sbt buildAndRun
```

Note: The sbt custome tasks take care of running/compiling both server and client side.

Last for running tests
```
sbt test
```

