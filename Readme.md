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

Few design decissions and details.

1. `TicketsUpload.scala` is a service responsible for reading the user file. Parsing it using apache commons csv parser and creating flattern mongo structure and storing it in mongodb. The `scala3 trait` now can have context bound and which make life easy as without harcoding effect we can compose behaviours. *Note*: There is a filter to check if the recorde was already inserted, this helps in case same file is uploaded multiple times.

2. Also `TicketsUpload.scala` service takes help of `Performace` domain object to generate records to insert. The idea is for every title entry in the file uploaded there are 100 entries done in the mongo containing all the information about the show/performance. Flattended structure makes the mongo shema read and write friendly. Though if it were an RDBMS the favrouite marking could have been in a seperate table but in this case we have everything in all the doccuments.

3. `InventoryService` is prety straight forward to understands as it only does a find and converts the `Performace` records into `Inventory` objects to be send to UI/front end.

4. To make the application easy to use, the UI has an extra (optional) date field to select query date so that user dont need to go post man or browser window to get and view the data. Also there is an upload file button which can be used to upload data to application.

5. Apart from this it's a bundeled scala and typescript repo and both the developments and testing can be done together.



### Future Improvements
> There is lot to improve.

1. Client side written in typeScript (this was my first time) would have been well modeled and lot of effects can be managed in a better way. I mean more FP, but unfortunately I didnt had enough time to learn and make the code better.

2. Error handling can be improved a lot, though the server side error are automatically managed by custom errors and Effects but on client side it coukd have been better. Same goes for logging.

3. Modeling on server side would have been much better by using scala 3 enums rathar than creating independent case classes at some places.

4. Missing tests from client side completely due to shortage of time. Although server side tests are also not complete though those are enough to test the business.

5. Gracefull shutdown of a stream is always a problem and it can be seen here as well when you try to shutdown the application. Could have been handled in a better way.

6. Optional but instead of trait and impl for services, could have created service as indepedent functions and used something like this(Context types) as function type signature to inject dependencies.
    ```
    type ReaderWithConfig = Conf ?=> RecordReader //or
    type ReaderWithConfig = Mongo ?=> RecordReader //or, depend on situation
    //could have given more control on how we compose programs in scala 3
    ```

7. As a clean up activity, could have given a button to clear up tickets which are 100 + days old. Or may be if used databases support ttl would have used those.

8. I didnt had enough time to look in to the bonus case :-), but was tempted to implement it.

### How to run

Make sure you have latest version of java(11+), sbt and node for starters.

If you want to build/install the UI and serverside both together and start the application do.
(Imp: You might see mongo hickups to start on console but it'll eventually pick a port and start.
After below instruction is success, open your browser and try http://localhost:8080)
```
sbt buildAndRun
```

If the UI is already build then you can just run the  Application with
```
sbt runApp
```

In case there is a UI change, no need to restart server side application just do 
```
npm run-script dbuild 
```


Note: 
1. Reccomend to run the first command, if the application has never build and run ou your machine.
2. The sbt custome tasks take care of running/compiling both server and client side.
3. To make the application easy to use, the UI has an extra (optional) date field to select query date so that user dont need to go post man or browser window to get and view the data. Also there is an upload file button which can be used to upload data to application.

Last for running tests
```
sbt test
```

