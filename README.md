# mobsoftstore-spring-mvc

A simple software store application created with Spring MVC framework and related technologies.

## Tech Stack

Spring Web MVC, Spring Security, Spring Data JPA, Hibernate, H2 DB, Thymeleaf, Bootstrap, Spring Test,
JUnit, Maven, Tomcat

## Building and deploying

### Standard approach

1. Open terminal and build the app with the following command:  `mvn clean package`  
   This will create __mobsoftstore.war__ within the __target__ directory.
2. Place the war file in the Tomcat's __webapps__ directory ($TOMCAT_HOME/webapps)

### Via .sh file

The file __buildDeploy.sh__ can be used to automatically build and deploy the app on the server. This approach will skip
tests execution, and assumes there is an environment variable __TOMCAT_HOME__ pointing to the Tomcat installation
directory.

## Usage

- After deployment the app will be available on the path __/mobsoftstore/__ (http://localhost:8080/mobsoftstore/ if the
  Tomcat is available locally on the default port 8080)

- After the app start the DB will contain 4 users, 10 applications and 20 ratings

- The available users are: __developer1, developer2, user1, user2__ all with the same password which is __password__.
  The roles are matched with the usernames, i.e. developer1 and developer2 have the __role Developer__ and user1 and
  user2 the __role User__

- For downloading and rating the user has to be authenticated, whereas the Developer role is required for adding a new
  application

- Rating is implemented with the rule "one user one rating per application" that means a user can update its rating but
  the rating count will stay the same. The initial DB contains only ratings from users __user1__ and __user2__ so
  __developer1__ and __developer2__ can be used for new ratings

- For adding a new application there are two valid sample archives under _src/main/resources/app-archive/upload_ -
  _circle.zip_ which contains images while _random.zip_ doesn't, that means the default ones will be added before saving
  into the DB. The application name on the form and the archive file name must match the values in the archive's txt
  file, or the validation will fail. In the case of the provided archives the name on the form must be "Circle" and "
  Random"

- H2 DB console is available on the path __/mobsoftstore/console__ and the credentials are the default ones - username
  __sa__ without password
