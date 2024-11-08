# README

+ This project is a backend part for FindYourPet. It was written by Spring Boot and Postgresql, deployed on Oracle Cloud.



# Features

+ The backend provides comprehensive management for lost pet information, allowing users to add, update, and search for lost pets effectively. This functionality is crucial for assisting pet owners in finding their pets quickly.

+ User authentication and authorization are managed through Firebase, ensuring that only authenticated users can add or modify data. This level of security protects user data and ensures a reliable experience for all users.

+ With PostGIS extension support, the backend enables geographic search features, allowing users to find lost pets based on their location. This is particularly helpful for narrowing down search results to a specific area.



## API doc

+ `/api/v1/health` GET
  
  health check

+ `/api/v1/users/login` POST
  
  save user into database

+ `/api/v1/users/updateInfo` POST
  
  update profile

+ `/api/v1/pets/lost` POST
  
  report a lost or found pet

+ `/api/v1/pets/update/{id}` PUT
  
  update a record

+ `/api/v1/pets/completed/{id}` PUT
  
  mark a record completed (maybe the pet is found)

+ `/api/v1/pets/delete/{id}` DELETE
  
  delete a record

+ `/api/v1/search/lost-pets/map` GET
  
  returns a list of pets within the specified radius, and display on the map

+ `/api/v1/search/lost-pets/list` GET
  
  get detailed pet information with pagination

+ `/api/v1/search/lost-pets/detail/{id}` GET
  
  Get detailed information of a specific pet



## Deploy

+ use cloud-init.sh as the user data to create environment

+ run `sudo systemctl reload nginx` to reload nginx

+ `docker run ... qu9972/findyourpet-backend:latest`

 
