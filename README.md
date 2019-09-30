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

## Design decisions

### How to store currency codes?

Where was 2 options on how to represent the codes in SQL schema:
 - Store values as `ENUM`
 - Store them in a separate EAV table
Due to a few amount of currently supported currencies the second
way has been considered more appropriate.
Use of the 1st option would lead to a lot of ALTER TYPE
operations in future which involves high risks and reduces flexibility.

Also, assume the similar implementation of the operation in PostgreSQL:
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
