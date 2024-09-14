# HTTP Server with Sockets

This is a custom HTTP server built using sockets, currently designed to handle **GET** requests. 
By using Java annotations, you can create controller classes and define route methods in a simple, intuitive way.

## Quick Start Guide

## 1. Create a Controller Class
Define your controller class and annotate it with **@Controller**

```
@Controller
public class MyController{}
```

## 2. Initialize the HTTP Server
Create an instance of the HttpServer, passing in the **port**  number and your **controller** instance:

```
new HttpServer(8080, new MyController());
```

## 3. Define Your Route Method
Within your controller, define methods to handle specific HTTP requests. 
Annotate these methods with **@Route**, specifying the HTTP method and the path.

```
@Route(method = "GET", path = "/")
public String myMethod() {
    return "Hello, World!";
}
```
- When a GET request is sent to the specified path, the server responds with the return value of the method.


## Features

- **Annotation-based routing**: Easily define route methods with the `@Route` annotation.
- **Simple to extend**: Add more routes and controllers as needed.
- **GET request handling**: The server currently supports GET requests but can be extended to handle other HTTP methods.

## Future Enhancements

- Support for other HTTP methods (POST, PUT, DELETE).
- Middleware for handling common tasks like logging, authentication, etc.
- Error handling and custom status codes.

---

Feel free to clone, customize, and experiment with the code to suit your needs!