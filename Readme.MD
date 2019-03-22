# TukuTiket
Tukutiket is a ticketing system. It's far from perfection compared to existing ticketing system, but the point of this repository is to learn about microservice architecture.

# How to build?
Execute `build-tukutiket.sh`
```
./build-tukutiket.sh
```

# How to run?
Go to `target` directory inside `server` and `gerbang` then run the corresponding `.jar` files using
```
java -jar xxx.jar
```
For other services, go to `/main/target` then you'll find the corresponding `.jar` files and run using the command mentioned above.

# Tech stack
* Spring boot framework
* Eureka for service registry
* Zuul for gateway
* Feign client for HTTP call
* RabbitMQ as message broker

# API documentation
Find the API documentation [here](http://bit.ly/tukutiket-api-docs)

# Architecture
![Architecture](https://github.com/axellageraldinc/TukuTiket-SpringBoot-Microservice/blob/master/Architecture.png)

# Get Order Details Flow
![Get order details](https://github.com/axellageraldinc/TukuTiket-SpringBoot-Microservice/blob/master/GetOrderDetails.png)

# Confirm Payment Flow
![Confirm payment](https://github.com/axellageraldinc/TukuTiket-SpringBoot-Microservice/blob/master/ConfirmPayment.png)
