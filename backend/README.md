# Backend
To run your own backend you need a server with docker installed and mysql server running. 
Also, you have to create a database `mzenly`.

Change `DBPASS` and `DATABASE` in the Dockerfile to your own database password and database ip.
And then execute the following commands:
```bash
docker build -t backend .
docker run --add-host=host.docker.internal:host-gateway -p 0.0.0.0:80:84 -d backend
```
This will build a docker image and run it on port 80. 

`--add-host=host.docker.internal:host-gateway` is used to allow the container to access 
the host network where the database is running
