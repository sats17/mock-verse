# API Usage Documentation

This Spring Boot application provides a MockServer with various API endpoints to store and retrieve mock API responses. The following is a list of available API endpoints with their descriptions and usage:

## 1. Insert Data to Map

#### Endpoint: /api/map/insert
#### Method: POST
#### Description: 
Inserts data into an in-memory map for a given API path and method.

#### Query Parameters:

* **apiPath (Required):** The API path that you want to mock. It should start with a /.
* **apiMethod (Required):** The HTTP method that you want to mock (e.g., GET, POST, PUT, DELETE).
* **apiQueryParams (Optional):** Any query parameters that should be considered for the mocked API.
* **apiHeaders (Optional):** The API accepts apiHeaders query parameter to specify the Content-Type (e.g., 'application/json', 'text/plain'). The mock server returns a response with the same content type. If not provided, the default is 'application/json'.

#### Request Body: 
The mock response body in any supported format.

```curl
Example usage:
- curl -X POST "http://localhost:8080/api/map/insert?apiPath=/test&apiMethod=GET&apiQueryParams=param1=value1;param2=value2&apiHeaders=content-type=application/json" -d '{"key": "value"}'
```
