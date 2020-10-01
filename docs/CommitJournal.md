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
---
- Implemented threaded support, made the integration test **PASS** 
- At this point the code fulfils order operations functional requirements
- Tomorrow I will :
  - Look and see if there are any refactorings that I can make to make it better
  - Look at how a client will use it 
  - Look at how to make it easy to use plus document the usage
  - Look at possible performance improvements
---
- Refactored the code to create better separation between the threading support and functional logic, and to make it easier to use as a library
- Refactored README (moved this section to a separate file)
- Moved the integration test in a separate directory
- Added usage notes in the README
- Added small performance improvements (initialized some maps with initial capacity to prevent premature rehashing)
- Added javadoc in some places
