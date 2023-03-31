# API Usage Documentation

This Spring Boot application provides a MockServer with various API endpoints to store and retrieve mock API responses. The following is a list of available API endpoints with their descriptions and usage:

## 1. Insert Data to Map

#### Endpoint: /api/map/insert
#### Method: POST
#### Description: 
Inserts data into an in-memory map for a given API path and method.

#### Parameters:

* **apiPath (Required):** The API path that you want to mock. It should start with a /.
* **apiMethod (Required):** The HTTP method that you want to mock (e.g., GET, POST, PUT, DELETE).
* **apiQueryParams (Optional):** Any query parameters that should be considered for the mocked API.
* **apiHeaders (Optional):** Any headers that should be considered for the mocked API.
