# Mangekyo API Mock Server

### How to start server
1) Clone repository
2) Open shell script
3) Run this command -> "sh run_me.sh"

### How to add mock behaviour to server
1) In memory storage -> This will store your mock behaviour inside in memory server storage. Every restart of server will clear the storage, hence you need to add mock behaviour again.
API Path = http://localhost:80/api/map/insert
Query Parameters - 
a) apiPath = Your mock API Path, it can include your query parameters or without query parameters.

3) File based storage -> This will create files of your mock behaviours 



Future scope -
1) Query parameter based mock support -> Done
2) Headers based mock support
3) Mock response HTTP status support
4) Server port support from command line
5) Pick content type 


Note: This is just a tool which helps developer while developing application, it will not support any backward compatibility.
