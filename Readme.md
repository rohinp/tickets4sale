# Tickets for sale

### Table of Contents:
1.  [Project structure](#project-structure)
2.  [Tech stack](#tech-stack)
3.  [Domain Objects](#domain-objects)
4.  [Business Logic](#business-logic)
5.  [Error handling](#error-handling)
6.  [Future Improvements](#future-improvements)
7.  [How to run](#hot-to-run)
8.  [System Requiremnts](#system-requirements)



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

Business logic is scatered in the domain and the service implementations. And most of it is tested using the MUnit.

### Error handling
```





```

### Future Improvements
```





```

### How to run
```





```

### System Requirements
```





```