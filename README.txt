README

//Author: Caleb Richard
//Date: 2/12/2014
//CS 283 Assignment 1


1.	 In designing the benchmark client, the general idea is a for-loop which will make many connections and requests from the server based
on the global variable SAMPLE_SIZE. This of course does not fully simulate a real server load, as none of the requests are truly happening
concurrently. I used the System.nanoTime() method in order to calculate how much time passed between making a new socket that connected to 
the server and receiving an upper case string back from the server. Then, after all the requests had been served, I displayed the shortest 
time a request took, the longest time, and the average time for all requests.

2.Multi-threaded Server benchmarks for 100 connections:

Shortest time to serve request:873227 ns
Longest time to serve request:29678946 ns
Average time to serve request:6532306 ns

Simple Server benchmarks for 100 connections:

Shortest time to serve request:4911134 ns
Longest time to serve request:23850409 ns
Average time to serve request:8729448 ns

3.   At first, I was getting odd results that were suggesting the Simple Server was completing requests faster than the Multi-threaded Server.
I believe that the reason this was occuring was because the requests were simply not taking long enough. The time it took for the Benchmark 
Client to make a new connection/request was longer than the amount of time it was taking the server to complete the request. This meant that
the advantage of having a Multi-threaded Server was lost in the overhead of creating the threads. 

	Because of this, I added a simple for loop that iterates through many times, to add some weight to each request. This shows in the above
benchmark outputs. Since the request time was not minimal, the Multi-threaded Server took, on average, less time per request, because new 
connections could be made while other threads worked asynchronously on older requests. Contrasting, the Simple Server has to service requests
one at a time, meaning a slower average time.
