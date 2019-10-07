[![Build Status](https://travis-ci.com/xotonic/mts.svg?token=F6qbtxyqHpqyfLyrcR2K&branch=master)](https://travis-ci.com/xotonic/mts)
[![codecov](https://codecov.io/gh/xotonic/mts/branch/master/graph/badge.svg?token=ggYiC1FP97)](https://codecov.io/gh/xotonic/mts)

# Money Transfer Service

## Original assignment

Design and implement a RESTful API (including data model and the backing implementation) for money transfers between accounts. 
Explicit requirements: 
1. You can use Java or Kotlin. 
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users. 
4. You can use frameworks/libraries if you like (except Spring), but don't forget about 
requirement #2 and keep it simple and avoid heavy frameworks. 
5. The datastore should run in-memory for the sake of this test. 
6. The final result should be executable as a standalone program (should not require a 
pre-installed container/server). 
7. Demonstrate with tests that the API works as expected.

Implicit requirements: 
1. The code produced by you is expected to be of high quality. 
2. There are no detailed requirements, use common sense. 


Please put your work on github or bitbucket. 

## About the project

 - There was intention to keep the minimal list of dependencies.
   Eventually, this project got only some dependencies for logging,
   parsing JSON and embedded relational database
 - The embedded database is MariaDB (MySQL compatible).
   There is no need to install dependencies. The daemon spins up automatically on application startup.
 - A tiny HTTP framework was written for this project.
   It uses only what really needed - e.g. it does not even know about the full list of HTTP methods and
   status codes. See the sources in `src/main/java/com/revolut/mts/http`, usage in `Application.java`.
 - API is minimalistic as well.
 
## API

- Create user

       POST /users => 201
       
       Request object:
       {
           "name": "alice"
       } 
       
       Response object:
       {
           "id": 1,
           "name": "alice"
       }

- Deposit money to user account

       POST /deposits
       
       Request object:
       {
           "username": "alice",
           "amount": {
              "currency" : "USD",
              "amount": 1.0
           }
       }
       
       Response is empty
 
 - Get user and their balance
       
       GET /users/{user-name} => 201
       
       Response object:
       {
          "id": 1,
          "name": "alice",
          "wallet": [
              {
                 "currency": "USD",
                 "amount": 122.0
              },
              ...
          ]
       }
 
 - Create transaction 
 
       POST /transactions => 201
       
       Request object:
       {
           "sender": "alice",
           "receiver": "bob",
           "target_currency": "USD",
           "source_money": {
               "amount": 1.0,
               "currency": "BTC"
           }
       }
       
       Response object:
       {
           "id": 123
       }

- Commit transaction

       PUT /transcation/{txid} => 200
       
       Respose is empty
       
## Design decisions

### How to store currency codes?

Where was 2 options on how to represent the codes in SQL schema:
 - Store values as `ENUM`
 - Store them in a separate EAV table
 - Store its in the application layer
The 3rd option might lead to inconsistency of the data
due to potential bugs in application logic.
Use of the 1st option would lead to a lot of ALTER TYPE
operations in future which involves high risks and reduces flexibility.


Due to a few amount of currently supported currencies the hybrid of 1st and 2nd 
ways has been considered to be more appropriate. Currently, MySQL MEMORY engine does not
support foreign keys checks on inserts and updates so the testing for existence of 
a currency is moved on application layer.

Also, consider the similar implementation of the operation in PostgreSQL:
>The problem is that changing the member list for an ENUM column
>restructures the entire table with ALTER TABLE, which can be very
>expensive on resources and time. If you have ENUM('red', 'blue', 'black')
>but need to change it to ENUM('red', 'blue', 'white'), MySQL needs to
>rebuild your table and look through every record to check for
>the now-invalid value 'black'. MySQL is literally dumb and will
>even do this when all you did was add a new value to the end of
>the member list! (It is rumored that appending an ENUM member list
>will be handled better in the future, but I doubt that this
>is a high priority feature.)

[Source](https://stackoverflow.com/a/31308166/4186817)

