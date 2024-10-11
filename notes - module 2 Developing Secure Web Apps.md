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


    return ResponseEntity.ok(Iterable<CashCard>);
}

```

note the ResponseEntity stores iterable objects instead of a single object type






