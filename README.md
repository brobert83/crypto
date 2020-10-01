# General notes

- The specification is [here](docs/Crypto_v1.0.md).
- Java 8 will be used
- The solution will be written using a ATDD approach.
- I have kept a journal as I wrote the code, it is [here](docs/CommitJournal.md)

## Design Decisions
- The example in the spec for the summary is only for one symbol and it does not specify a particular structure, so I decided to make it so that the summary will contain data about all the symbols and both sell and buy sides, see [Code section](#Code)
- The first version will be single threaded, after that works I will add multi-threading (because a UI is mentioned I assume this code should be threadsafe)
- I won't make too many performance enhancements initially, the first version will focus on the functional requirements.
- The code is not written in a protective style, there are null checks added using Lombok's @NonNull annotation
  - the IDE will show warnings (when it can) when nulls are passed

## Usage
### Maven
To use this code as a library:
 - First package it using: `mvn clean package`
 - Then it must be published to whatever Maven repository the client app is using, if on the same machine then just `mvn install`
 - Then, if using Maven, include the dependency in your pom.xml
 ```xml
    <dependencies>
        ...
        <dependency>
            <groupId>io.github.brobert83</groupId>
            <artifactId>crypto</artifactId>
            <version>1.0</version>
        </dependency>    
        ...
    </dependencies>
```
### Code
 - The driver code needs to create a instance of the [CryptoBoard](src/main/java/io/github/brobert83/crypto/board/CryptoBoard.java) class, and for that there are 2 utility methods in [CryptoBoardApi](src/main/java/io/github/brobert83/crypto/CryptoBoardApi.java)
 ```java
    //single threaded     
    CryptoBoard cryptoBoard = CryptoBoard.newCryptoBoard();

    //multi-threaded
    CryptoBoard cryptoBoard = CryptoBoard.newCryptoBoardThreaded();
 ```
- To add, remove or get a summary use the methods in [CryptoBoard](src/main/java/io/github/brobert83/crypto/board/CryptoBoard.java) like so:
```java

    //add order (it will return a order object with the id filled in)
    Order createdOrder = cryptoBoard.addOrder(
        Order.builder()
            .side(Side.SELL)
            .symbol(new Symbol("ethereum"))
            .quantity(new BigDecimal("350.1"))
            .price(new BigDecimal("13.6"))
            .build()
    )   
    
    //remove order
    cryptoBoard.removeOrder(12345L);

    //get summary
    BoardSummary summary = cryptoBoard.getBoardSummary();

    //to get the sell levels for a symbol from the summary
    List<Level> ethereumSellLevels = summary.getSellLevels().get(new Symbol("ETHEREUM"));
    
    //to print the quantity and price for a level list
    ethereumSellLevels
        .stream()
        .map(level-> String.format("Price: '%s' Quantity: '%s'", level.getPrice(), level.getQuantity()))
        .forEach(System.out::println);
```

