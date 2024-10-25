# Implementing PUT

ow to implement the Update operation in a RESTful API is somewhat of a hot topic, which is what we’ll tackle in this lesson.

> Creating a new endpoint to receive an HTTP request with a verb, URI, and body.

> Returning an appropriate response from the endpoint for success and error conditions

## PUT and PATCH

1. If you need the server to return the URI of the created resource (or the data you use to construct the URI), then you must use POST.

2. Alternatively, when the resource URI is known at creation time (as is the case in our example Invoice API), you can use PUT.

> POST creates a sub-resource (child resource) under (after), or within the request URI.

> PUT creates or replaces (updates) a resource at a specific request URI.

### Response Body and Status Code

> Return 201 CREATED (if you created the object), or 200 OK (if you replaced an existing object). In this case, it's recommended to return the object in the response body.

> Return 204 NO CONTENT, and an empty response body. The rationale in this case is that since a PUT simply places an object at the URI in the request, the client doesn't need any information back - it knows that the object in the request has been saved, verbatim, on the server.

## POST, PUT, PATCH and CRUD Operations - Summary

| HTTP Method | Operation | Definition of Resource URI | What does it do? | Response Status Code | Response Body |
|-------------|-----------|----------------------------|-------------------|----------------------|----------------|
| POST        | Create    | Server generates and returns the URI | Creates a sub-resource ("under" or "within" the passed URI) | 201 CREATED              | The created resource    |
| PUT         | Create    | Client supplies the URI              | Creates a resource (at the Request URI)                     | 201 CREATED              | The created resource    |
| PUT         | Update    | Client supplies the URI              | Replaces the resource: The entire record is replaced by the object in the Request | 204 NO CONTENT | (empty) |
| PATCH       | Update    | Client supplies the URI              | Partial Update: modify only fields included in the request on the existing record | 200 OK                   | The updated resource    |


# Implementing Put Mapping

```java

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(
        @PathVariable Long requestedId, 
        @RequestBody CashCard cashCardUpdate,
        Principal principal
    ) {
        // retrieve the resource of the owner.
        
        // update the resource
        
        // just return 204 NO CONTENT for now.
        return ResponseEntity.noContent().build();
    }

```

> The @PutMapping supports the PUT verb and supplies the target requestedId.

> The @RequestBody contains the updated CashCard data.

3. Don't crash.

Though we're thankful to Spring Security, our application shouldn't crash - we shouldn't allow our code to throw a NullPointerException. Instead, we should handle the condition when *cashCard == null*, and return a generic *404 NOT_FOUND HTTP response*.

Update CashCardController.putCashCard to return 404 NOT_FOUND if no existing CashCard is found.

```java 

    PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(
        @PathVariable Long requestedId, 
        @RequestBody CashCard cashCardUpdate, 
        Principal principal
    ) {

        CashCard cashCard = cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
        
        if (cashCard != null) {

            CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());

            cashCardRepository.save(updatedCashCard);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

```


# Implementing DELETE

```js

Request:

    Verb: DELETE
    URI: /cashcards/{id}
    Body: (empty)

Response:

    Status code: 204 NO CONTENT
    Body: (empty)

```

| Response Code | Use Case                                                                                  |
|---------------|-------------------------------------------------------------------------------------------|
| 204 NO CONTENT| The record exists, and the Principal is authorized, and the record was successfully deleted.|
| 404 NOT FOUND | The record does not exist (a non-existent ID was sent).                                   |
| 404 NOT FOUND | The record does exist but the Principal is not the owner.                                 |

## Additional Options

### Hard and Soft Delete

*Hard delete* is removing a record from the database completely.

*Soft delete* is marking the record as 'deleted' in the database, adding a field is_deleted and a timestamp for deletion date.

A customer service representative might need to know when a customer deleted their Cash Card.
There may be data retention compliance regulations which require deleted data to be retained for a certain period of time.

Archive (move) the deleted data into a different location.

Maintain an audit trail. The audit trail is a record of all important operations done to a record. It can contain not only Delete operations, but Create and Update as well.

We could implement soft delete, then have a separate process which hard-deletes or archives soft-deleted records after a certain time period, like once per year.

We could implement hard delete, and archive the deleted records.

In any of the above cases, we could keep an audit log of which operations happened when.

## Implement the DELETE Endpoint

Now we need to write a Controller method which will be called when we send a DELETE request with the proper URI.

Add code to the Controller to delete the record.

```java

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(
        @PathVariable Long id,
        Principal principal //add principal to the parameter list
    ) {
        if (/* cash card does not exist and belongs tot the right owner */) {
            return ResponseEntity.notFOund().build();
        }

        cashCardRepository.deleteById(id); // remove record with the specified ID

        return ResponseEntity.noContent().build();
    }

```

> We use the @DeleteMapping with the "{id}" parameter, which Spring Web matches to the id method parameter.

We need to check whether the record exists. If not, we should not delete the Cash Card, and return 404 NOT FOUND.















































































