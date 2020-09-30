### General notes

- The specification is [here](docs/Crypto_v1.0.md).
- Java 8 will be used
- The solution will be written using a ATDD approach.
- Lombok will be used to reduce boilerplate code.
- AssertJ and Mockito will be used for testing
- The example in the spec for the summary is only for one symbol, but my decision is to make it so that the summary will contain data about all the symbols and both sell and buy sides.
- The first version will be single threaded, after that works I will add multi-threading.
- I won't make too many performance enhancements initially, the first version will focus on the functional requirements.

## Commit details

 - Basic Maven structure 
 - One dummy test to make sure it builds and runs the test
---  
 - Created a integration test with the data in the spec with no implementation apart from empty methods and things to make it compile in general
 - Added README
---
 - Enhanced the integration test with BUY orders and a second symbol
--- 
 - Started to build the order placement logic
 - My idea is to maintain a structure like this:             
```
   OrderBooks                             |
     Map<Symbol, OrderBook> orderBooks    |     The data structure will be like this 
                                          |      
   OrderBook                              |
     Symbol symbol                        |     Ethereum  ->  sellLevels(quantity, price)  
     Collection<Level> sellLevels         |                   buyLevels(quantity, price)  
     Collection<Level> buyLevels          |                              
                                          |     Litecoin  -> sellLevels(quantity, price)  
   Level                                  |                  buyLevels(quantity, price)   
     quantity                             |     
     price                                |     ...
```
---
- Implemented the order place logic
- Used TreeSet to maintain the order and perform easy search
---
- Added the board summary logic, made the integration test **PASS**
---
- Enhanced the integration test with a order removal
---
- Implemented order removal logic, made the integration test **PASS**
---
- Added integration test for threaded calls
---
- Changed the index maps to accept concurrent modifications and improved the threaded integration test assertions
- My idea to implement concurrency is to execute updates on specific threads like this
```
                       SYMBOL_1                       SYMBOL_2                                     SYMBOL_N        
                   BUY        SELL                BUY        SELL                              BUY          SELL    
                    |            |                 |            |                               |            |    
                    |            |                 |            |            ...                |            |    
                    |            |                 |            |                               |            |    
                    V            V                 V            V                               V            V    
            ----> thread       thread <--        thread       thread                          thread       thread
           |                             | 
           |                             | 
   sell 1 -|                             |- buy 1
   sell 2 -|                             |- buy 2
     .     |                             |   .
     .     |                             |   .
     .     |                             |   .
   sell x -|                             |- buy x                                            
```
