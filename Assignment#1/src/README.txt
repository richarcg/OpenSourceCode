README

//Author: Caleb Richard
//Date: 2/12/2014
//CS 283 Assignment 1


1.   In designing the benchmark client, the general idea is to spawn a few threads that can make concurrent requests
to the server, and allow the server to handle those requests however it can. The Benchmark Client times how long it
takes from when the threads are spawned to when they are joined back together, meaning all requests have been served.

2.Multi-threaded Server benchmarks for 3 Threads and 1000 requests per thread:

Total time taken:295085934 ns
Average time to serve request:98361 ns

Simple Server benchmarks for 3000 requests:

Total time taken:506000877 ns
Average time to serve request:168666 ns

3. The Simple Server has to deal with all requests in a serial manner, which accounts for why it is much slower in 
average time taken per request. The MT Server can spawn as many threads as there are clients that which to make requests.
Since the Benchmark Client acts as 3 seperate clients (through threading) this means the MT Server can be serving 3 requests
at once.
