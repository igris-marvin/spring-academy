# Implementing POST

In this lesson, you’ll add the Create endpoint to the API.

## Idempotence and HTTP

An idempotent operation is defined as one which, if performed more than once, results in the same outcome.

In a REST API, an idempotent operation is one that even if it were to be performed several times, the resulting data on the server would be the same as if it had been performed only once.

For each method, the HTTP standard specifies whether it is idempotent or not. GET, PUT, and DELETE are idempotent, whereas POST and PATCH are not.

## The POST Request and Response

Now let’s talk about the content of the POST Request, and the Response.

### The Request

The POST method allows a Body, so we'll use the Body to send a JSON representation of the object:

```js
Request:

Method: POST
URI: /cashcards/
Body:

    {
        "amount": 123.45
    }

```

In contrast, if you recall from a previous lesson, the GET operation includes the ID of the Cash Card in the URI, but not in the request Body.

### The Response

On successful creation, what HTTP Response Status Code should be sent? We could use 200 OK (the response that Read returns), but there’s a more specific, more accurate code for REST APIs: 201 CREATED.

By returning the 201 CREATED status, the API is specifically communicating that data was added to the data store on the server.

### Headers

In a previous lesson you learned that an HTTP Response contains two things: a Status Code, and a Body. But that’s not all! A Response also contains Headers.

Headers have a name and a value.

The HTTP standard specifies that the Location header in a 201 CREATED response should contain the URI of the created resource.

This is handy because it allows the caller to easily fetch the new resource using the GET endpoint (the one we implemented prior).

Here is the complete Response:

```js
Response:

Status Code: 201 CREATED
Header: Location=/cashcards/42

```

## Spring Web Convenience Methods

Spring Web provides methods which are geared towards the recommended use of HTTP and REST.

For example, we'll use the ResponseEntity.created(uriOfCashCard) method to create the above response.

This method requires you to specify the location, ensures the Location URI is well-formed (by using the URI class), adds the Location header, and sets the Status Code for you. And by doing so, this saves us from using more verbose methods.
 
For example, the following two code snippets are equivalent (as long as uriOfCashCard is not null):

```java

return ResponseEntity
        .created(uriOfCashCard)
        .build();

// VS

return ResponseEntity
        .status(HttpStatus.CREATED)
        .header(HttpHeaders.LOCATION, uriOfCashCardtoASCIIString())
        .build();

```

Note that by returning nothing at all, Spring Web will automatically generate an HTTP Response Status code of 200 OK.

Our CashCardController now implements the expected input and results of an HTTP POST.

```java
createCashCard(@RequestBody CashCard newCashCardRequest, ...)

```

Unlike the GET we added earlier, the POST expects a request "body". This contains the data submitted to the API. Spring Web will deserialize the data into a CashCard for us.

```java

    URI locationOfNewCashCard = ucb
        .path("cashcards/{id}")
        .buildAndExpand(savedCashCard.id())
        .toUri();

```

This is constructing a URI to the newly created CashCard. This is the URI that the caller can then use to GET the newly-created CashCard.

Note that savedCashCard.id is used as the identifier, which matches the GET endpoint's specification of cashcards/<CashCard.id>.

Where did UriComponentsBuilder come from?

We were able to add UriComponentsBuilder ucb as a method argument to this POST handler method and it was automatically passed in. How so? It was injected from our now-familiar friend, Spring's IoC Container. Thanks, Spring Web!

```java
    return ResponseEntity.created(locationOfNewCashCard).build();
```

Finally, we return 201 CREATED with the correct Location header.


## Returning a list with GET

### Requesting a List of Cash Cards

The API should be able to return multiple Cash Cards in response to a single REST request.

we’ll need a new data contract. Instead of a single Cash Card, the new contract should specify that the response is a JSON Array of Cash Card objects:

```json

[
  {
    "id": 1,
    "amount": 123.45
  },
  {
    "id": 2,
    "amount": 50.0
  }
]

```

Get all cash cards as a list

```java

@GetMapping
    private ResponseEntity<Iterable<CashCard>> getAllCashCards(

    ) {
        Iterable<CashCard> list = cardServ.getAllCashCard();

        for (CashCard x : list) {
            System.out.println(x.toString());
        }

        return ResponseEntity.ok(list);
    }

```

note the ResponseEntity stores iterable objects instead of a single object type

# Simple Spring Security

## What Is Security?

how HTTP Authentication and Authorization work, common ways in which the web ecosystem is vulnerable to attacks, and how we can use Spring Security to prevent unauthorized access to our Family Cash Card service.

## Authentication

A user of an API can actually be a person or another program, so often we’ll use the term Principal as a synonym for “user”.

Authentication is the act of a Principal proving its identity to the system.

One way to do this is to provide credentials (e.g. a username and password using Basic Authentication).

We say that once the proper credentials have been presented, the Principal is authenticated.

HTTP is a stateless protocol, so each request must contain data that proves it’s from an authenticated Principal.

lthough it’s possible to present the credentials on every request, doing so is inefficient because it requires more processing on the server.

Instead, an Authentication Session (or Auth Session, or just Session) is created when a user gets authenticated.

Sessions can be implemented in many ways. We’ll use a common mechanism: A Session Token (a string of random characters) that is generated, and placed in a Cookie.

A Cookie is a set of data stored in a web client (such as a browser), and associated with a specific URI.

A couple of nice things about Cookies:

> Cookies are automatically sent to the server with every request (no extra code needs to be written for this to happen). As long as the server checks that the Token in the Cookie is valid, unauthenticated requests can be rejected.

> Cookies can persist for a certain amount of time even if the web page is closed and later re-visited. This ability typically improves the user experience of the web site.


## Spring Security and Authentication

Spring Security implements authentication in the Filter Chain. The Filter Chain is a component of Java web architecture which allows programmers to define a sequence of methods that get called prior to the Controller. 

Each filter in the chain decides whether to allow request processing to continue, or not. Spring Security inserts a filter which checks the user’s authentication and returns with a 401 UNAUTHORIZED response if the request is not authenticated.

## Authorization

authentication is only the first step. Authorization happens after authentication, and allows different users of the same system to have different permissions.

Spring Security provides Authorization via *Role-Based Access Control (RBAC)*. This means that a Principal has a number of *Roles*.

Each resource (or operation) specifies which Roles a Principal must have in order to perform actions with proper authorization.

example, a user with an Administrator Role is likely to be authorized to perform more actions than a user with a Card Owner Role. 

You can configure role-based authorization at both a global level and a per-method basis.

## Same Origin Policy

The web is a dangerous place, where bad actors are constantly trying to exploit security vulnerabilities. The most basic mechanism of protection relies on HTTP clients and servers implementing the Same Origin Policy (SOP). This policy states that only scripts which are contained in a web page are allowed to send requests to the origin (URI) of the web page.

SOP is critical to the security of web sites because without the policy, anyone could write a web page containing a script which sends requests to any other site.

## Cross-Origin Resource Sharing

Sometimes a system consists of services running on several machines with different URIs (i.e. Microservices). 

Cross-Origin Resource Sharing (CORS) is a way that browsers and servers can cooperate to relax the SOP. 

A server can explicitly allow a list of “allowed origins” of requests coming from an origin outside the server’s.

Spring Security provides the *@CrossOrigin annotation*, allowing you to specify a list of allowed sites. Be careful! If you use the annotation without any arguments, it will allow all origins, so bear this in mind!

## Common Web Exploits

Spring Security provides a powerful tool set to guard against common security exploits.

### Cross-Site Request Forgery

One type of vulnerability is a *Cross-Site Request Forgery (CSRF) which is often pronounced “Sea-Surf”*, and also known as Session Riding. Session Riding is actually enabled by Cookies. CSRF attacks happen when a malicious piece of code sends a request to a server where a user is authenticated. When the server receives the Authentication Cookie, it has no way of knowing if the victim sent the harmful request unintentionally.

To protect against CSRF attacks, you can use a *CSRF Token*. A CSRF Token is different from an Auth Token because a unique token is generated on each request. This makes it harder for an outside actor to insert itself into the “conversation” between the client and the server.

### Cross-Site Scripting

Perhaps even more dangerous than CSRF vulnerability is Cross-Site Scripting (XSS). This occurs when an attacker is somehow able to “trick” the victim application into executing arbitrary code. There are many ways to do this. A simple example is saving a string in a database containing a <script> tag, and then waiting until the string is rendered on a web page, resulting in the script being executed. </script>

XSS is potentially more dangerous than CSRF. In CSRF, only actions that a user is authorized to do can be executed. However in XSS, arbitrary malicious code executes on the client or on the server. Additionally, XSS attacks don’t depend on Authentication. Rather, XSS attacks depend on security “holes” caused by poor programming practices.

The main way to guard against XSS attacks is to properly process all data from external sources (like web forms and URI query strings). In the case of our <script> tag example, attacks can be mitigated by properly escaping the special HTML characters when the string is rendered. </script>

## SECURITY [ PRACTICAL ]

1: Understand our Security Requirements
Who should be allowed to manage any given Cash Card?

In our simple domain, let's state that the user who created the Cash Card "owns" the Cash Card. Thus, they are the "card owner". Only the card owner can view or update a Cash Card.

The logic will be something like this:

IF the user is authenticated

... AND they are authorized as a "card owner"

... ... AND they own the requested Cash Card

THEN complete the users's request

BUT don't allow users to access Cash Cards they do not own.

---

In this lab we'll secure our Family Cash Card API and restrict access to any given Cash Card to the card's "owner".

To prepare for this, we introduced the concept of an *owner* in the application.

The *owner* is the unique identity of the person who created and can manage a given Cash Card.

Let's review the following changes we made on your behalf:

    > owner added as a field to the CashCard Java record.

    > owner added to all .sql files in src/main/resources/ and src/test/resources/.

    > owner added to all .json files in src/test/resources/example/cashcard.

All application code and tests are updated to support the new owner field. No functionality has changed as a result of these updates.

---

3: Add the Spring Security Dependency

When we added the Spring Security dependency to our application, security was enabled by default.

Since we haven't specified how authentication and authorization are performed within our Cash Card API, Spring Security has completely locked down our API.

4: Satisfy Spring Security's Dependencies

1. providing the minimum configuration needed by Spring Security.

under ../configuration package, create a file names "SecurityConfig", this will be the java bean where we'll configure Spring Security for our application.

Notice the filterChain(HttpSecurity) method returns http.build(), which is the minimum needed for now.

2. Enable Spring Security.

At the moment SecurityConfig is just an un-referenced Java class as nothing is using it.

Let's turn SecurityConfig into our configuration Bean for Spring Security by annotating SecurityConfig class with the @Configuration annotation

```java

@Configuration
class SecurityConfig {...}

```
The @Configuration annotation tells Spring to use this class to configure Spring and Spring Boot itself. Any Beans specified in this class will now be available to Spring's Auto Configuration engine.

```java

    @Bean
    ...SecurityFilterChain filterChain...

```

Spring Security expects a Bean to configure its Filter Chain, which you learned about in the Simple Spring Security lesson. Annotating a method returning a SecurityFilterChain with the @Bean satisfies this expectation.

## 5: Configure Basic Authentication

Thus far we've bootstrapped Spring Security, but not actually secured our application.

secure our application by configuring basic authentication.

Update SecurityConfig.filterChain with the following to enable basic authentication:

```java

@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     http
             .authorizeHttpRequests(request -> request
                     .requestMatchers("/cashcards/**")
                     .authenticated())
             .httpBasic(Customizer.withDefaults())
             .csrf(csrf -> csrf.disable());
     return http.build();
}

```

All HTTP requests to cashcards/ endpoints are required to be authenticated using HTTP Basic Authentication security (username and password). 

This does not include do not require CSRF security.

We've enabled basic authentication, requiring that requests must supply a username and password.

## 6: Testing Basic Authentication

Configure a test-only UserDetailsService.

Which username and password should we submit in our test HTTP requests?

When you reviewed changes to src/test/resources/data.sql you should've seen that we set an OWNER value for each CashCard in the database to the username sarah1. For example:

Let's provide a test-only UserDetailsService with the user sarah1.

Add the following Bean to SecurityConfig.

```java

    @Bean
    private UserDetailsService testOnlyUsers(
        PasswordEncoder passwordEncoder
    ) {
        User.UserBuilder users = User.builder();
        UserDetails sarah = users
            .username("sarah1")
            .password(passwordEncoder.encode("abc123"))
            .roles() // No roles for now
            .build();
        return new InMemoryUserDetailsManager(sarah);
    }

```

3. Update the POST endpoint in the Controller.

Once again we'll use the provided Principal to ensure that the correct owner is saved with the new CashCard.

```java

@PostMapping
private ResponseEntity<Void> createCashCard(
    @RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb, 
    Principal principal
) {
    CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());
    
    CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);

    //...
}

```
When should you use CSRF protection? Our recommendation is to use CSRF protection for any request that could be processed by a browser by normal users. If you are only creating a service that is used by non-browser clients, you will likely want to disable CSRF protection.








