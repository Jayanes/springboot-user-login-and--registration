# springboot-user-login-and--registration
User Management system

Get the project repository from the git master branch(JDK should be 11)
Using this link   https://github.com/Jayanes/springboot-user-login-and--registration  


Enable import maven dependency. After the import create the database as a user_management  then run the command “ mvn clean install “

Create a new folder as config and inside the config folder paste the applications.properties and change database credentials 

 Put the jar file outside the config file ( config folder and jar file are the same location)
Run the command(replace with correct jar name) “ java -jar jarname.jar “

If jar run successfully then execute the following queries
INSERT INTO roles(name) VALUES('ROLE_ADMIN'); 
INSERT INTO roles(name) VALUES('ROLE_USER_MANAGE');
INSERT INTO roles(name) VALUES('ROLE_VIEW');

Register the user using postman
http://localhost:8080/api/auth/signup
Request body ( for a normal user)

{
    "name":"name",
    "username":"username",
    "email":"email", //should be valid email
    "password":"password"  // minimum 6 charactor


}



If we need to add special users like admin or manage user roles then add roles in the request body. If not the system will register normal user  Ex:
            For Admin

                {
    "name":"name",
    "username":"username",
    "email":"email", //should be valid email
    "password":"password",  // minimum 6 charactor
           “userRole”:”ROLE_ADMIN”
}
For User manage
                
{
    "name":"name",
    "username":"username",
    "email":"email", //should be valid email
    "password":"password",  // minimum 6 charactor
           “userRole”:”ROLE_USER_MANAGE”


}

Then use the following route to login the system and keep the token(try to login three different users)
http://localhost:8080/api/auth/signin
{
"username":"username",
"password":"password"
    
}
If we login uses admin role it will set automatically active user. other roles are needed to activate by admin such as “ROLE_VIEW” and “ROLE_USER_MANAGE” until those users status are pending.

If we entered three-time wrong credentials then account will be locked.
Then account will be unlocked by the admin.




To add privilege to the user please add the Bearer token into authorization header which is provided by after the login. Each user has a different token which depends on  user role


Get all pending accounts (get request)
http://localhost:8080/api/user/get-all-pending-account (only accessible by admin .)

Activate account by Id
http://localhost:8080/api/user/activate_pending_account_by_id(only accessible by admin .)

{
    "id":id(Integer)
}


Get all locked accounts (get request)
http://localhost:8080/api/user/get-all-locked-account (only accessible by admin )
    



Unlock account by id
http://localhost:8080/api/user/unlock_account_by_id (only accessible by admin .)


{
    "id":id(Integer)
}



Delete account by id (only accessible by admin and user_manage role)
http://localhost:8080/api/user/delete
{
    "id":id(Integer)
}


Edit account by id (only accessible by admin and user_manage role) name and email can be editable
http://localhost:8080/api/user/edit
{
    "id":id(Integer),
“name”:”name”,
“email”:”email”
}


The public route to  get all user without a role as role admin and role user manage (No need token for accessing this  route)
http://localhost:8080/api/user/get-all-user (get request)

