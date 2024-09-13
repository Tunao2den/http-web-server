# Server Request Handling

The server now handles **three types of requests**!

## 1. Basic Request

You can send a basic request and receive a `200 OK` response.

- **URI for basic request:** `http://localhost:4221`

## 2. Unimplemented URIs

For all other unimplemented URIs, the server sends a `404 Not Found` response.

- **Example:**
    - Request: `http://localhost:4221/hello`
    - Response: `404 Not Found`

## 3. Echo Request

The server sends a `200 OK` response with a body that includes the variable from the request path.

- **Example:**
    - Request: `http://localhost:4221/echo/message`
    - Response: `200 OK` with the body containing `"message"`
