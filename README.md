#dynamic-multilevel-caching-system

1. Cache System Design
I started by designing a system with multiple cache levels. Each cache level had its own size and eviction policy:

L1 Cache: This is the highest priority cache level where all new data is first inserted.
L2 Cache and Below: These are lower cache levels that hold data when it is evicted from L1.


2. Eviction Policies
Implemented two eviction policies for each cache level:
LRU (Least Recently Used): Evicts the least recently accessed item.
LFU (Least Frequently Used): Evicts the least frequently accessed item.
Each cache level supports one eviction policy (either LRU or LFU) and behaves according to that policy when it becomes full.

3. Cache Structure Setup
   
Each cache level contains:

Cache Map: A LinkedHashMap to store key-value pairs.
LRU Queue: A Deque to track the order of usage for the LRU eviction policy.
Frequency Map: A HashMap to track how often each item is accessed for the LFU eviction policy.


4. Adding Cache Levels
   Provided a function addCacheLevel(size, evictionPolicy) to dynamically add new cache levels. The user specifies the size and eviction policy for each cache level:
 
5. Insert Data into the Cache (put method)
When inserting data into the cache:

The data is inserted into the L1 cache.
If L1 is full, an item is evicted based on the eviction policy.
The evicted item is demoted to the L2 cache if space is available. If L2 is full, its eviction policy is triggered to make space.

6. Eviction Logic
LRU Eviction: The least recently accessed item is removed by using a queue (lruQueue). The first item in the queue is evicted.
LFU Eviction: The least frequently accessed item is evicted using the frequencyMap. The item with the lowest access count is removed.

7. Data Retrieval (get method)
   
When retrieving data from the cache:

The system checks the L1 cache first. If the data is found, it is returned.
If not found, it checks L2 and other lower levels sequentially.
If data is found in a lower level, it is promoted to L1.
If data is not found in any cache, it simulates a cache miss and returns null.

8. Demotion and Promotion between Cache Levels
   
Demotion: If an item is evicted from L1, it is demoted to L2 if space is available.
Promotion: When an item is found in L2 or below, it is promoted back to L1 to make sure frequently accessed data stays in the highest priority cache.


9 Cache Display (displayCache method)
We added a function displayCache() to print the current state of all cache levels. This function loops through all levels and displays the keys and values in each level.

By following this step-by-step process, implemented a dynamic, multilevel cache system with efficient eviction and retrieval mechanisms.

