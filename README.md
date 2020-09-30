### General notes

- The specification is [here](docs/Crypto_v.1.0.md).
- Java 8 will be used
- The solution will be written using a ATDD approach.
- Lombok will be used to reduce boilerplate code.
- AssertJ and Mockito will be used for testing
- The example in the spec for the summary is only for one symbol, but my decision is to make it so that the summary will contain data about all the symbols and both sell and buy sides.
- The first version will be single threaded, after that works I will add multi-threading.
- I won't make too many performace enhacements initially, the first version will focus on the functional requirements.

## Commit details

 - Basic Maven structure 
 - One dummy test to make sure it builds and runs the test
---  
 - Created a integration test with the data in the spec with no implementation apart from empty methods and things to make it compile in general
 - Added README