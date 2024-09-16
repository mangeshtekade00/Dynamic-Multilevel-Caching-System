import java.util.*;


class CacheLevel {
    int size;
    String evictionPolicy;
    Map<String, String> cacheMap;
    Deque<String> lruQueue;
    Map<String, Integer> frequencyMap;

    CacheLevel(int size, String evictionPolicy) {
        this.size = size;
        this.evictionPolicy = evictionPolicy;
        this.cacheMap = new LinkedHashMap<>();
        this.lruQueue = new LinkedList<>();
        this.frequencyMap = new HashMap<>();
    }
}


class MultiLevelCache {
    List<CacheLevel> cacheLevels;

    MultiLevelCache() {
        cacheLevels = new ArrayList<>();
    }


    public void addCacheLevel(int size, String evictionPolicy) {
        cacheLevels.add(new CacheLevel(size, evictionPolicy));
    }


    public void removeCacheLevel(int level) {
        if (level >= 1 && level <= cacheLevels.size()) {
            cacheLevels.remove(level - 1);
        }
    }


    private void evictLRU(CacheLevel cache) {
        String leastUsedKey = cache.lruQueue.pollFirst();
        cache.cacheMap.remove(leastUsedKey);
    }


    private void evictLFU(CacheLevel cache) {
        String leastFreqKey = null;
        int minFreq = Integer.MAX_VALUE;
        for (String key : cache.cacheMap.keySet()) {
            int freq = cache.frequencyMap.getOrDefault(key, 0);
            if (freq < minFreq) {
                minFreq = freq;
                leastFreqKey = key;
            }
        }
        cache.cacheMap.remove(leastFreqKey);
        cache.frequencyMap.remove(leastFreqKey);
    }


    public void put(String key, String value) {
        CacheLevel l1 = cacheLevels.get(0);


        if (l1.cacheMap.size() == l1.size) {
            String evictedKey = null;
            if (l1.evictionPolicy.equals("LRU")) {
                evictedKey = l1.lruQueue.pollFirst();
            } else if (l1.evictionPolicy.equals("LFU")) {
                evictedKey = evictAndReturnLFUKey(l1);
            }


            if (evictedKey != null) {
                demoteToLowerCache(evictedKey, l1.cacheMap.get(evictedKey));
                l1.cacheMap.remove(evictedKey);
            }
        }

        l1.cacheMap.put(key, value);


        if (l1.evictionPolicy.equals("LRU")) {
            l1.lruQueue.remove(key);
            l1.lruQueue.addLast(key);
        } else if (l1.evictionPolicy.equals("LFU")) {
            l1.frequencyMap.put(key, l1.frequencyMap.getOrDefault(key, 0) + 1);
        }
    }


    private String evictAndReturnLFUKey(CacheLevel cache) {
        String leastFreqKey = null;
        int minFreq = Integer.MAX_VALUE;
        for (String key : cache.cacheMap.keySet()) {
            int freq = cache.frequencyMap.getOrDefault(key, 0);
            if (freq < minFreq) {
                minFreq = freq;
                leastFreqKey = key;
            }
        }
        cache.frequencyMap.remove(leastFreqKey);
        return leastFreqKey;
    }


    private void demoteToLowerCache(String key, String value) {
        for (int i = 1; i < cacheLevels.size(); i++) {
            CacheLevel lowerCache = cacheLevels.get(i);


            if (lowerCache.cacheMap.size() < lowerCache.size) {
                lowerCache.cacheMap.put(key, value);
                if (lowerCache.evictionPolicy.equals("LRU")) {
                    lowerCache.lruQueue.addLast(key);
                } else if (lowerCache.evictionPolicy.equals("LFU")) {
                    lowerCache.frequencyMap.put(key, lowerCache.frequencyMap.getOrDefault(key, 0) + 1);
                }
                return;
            } else {

                if (lowerCache.evictionPolicy.equals("LRU")) {
                    String evictedKey = lowerCache.lruQueue.pollFirst();
                    lowerCache.cacheMap.remove(evictedKey);
                } else if (lowerCache.evictionPolicy.equals("LFU")) {
                    String evictedKey = evictAndReturnLFUKey(lowerCache);
                    lowerCache.cacheMap.remove(evictedKey);
                }


                lowerCache.cacheMap.put(key, value);
                if (lowerCache.evictionPolicy.equals("LRU")) {
                    lowerCache.lruQueue.addLast(key);
                } else if (lowerCache.evictionPolicy.equals("LFU")) {
                    lowerCache.frequencyMap.put(key, lowerCache.frequencyMap.getOrDefault(key, 0) + 1);
                }
                return;
            }
        }
    }


    public String get(String key) {

        for (int i = 0; i < cacheLevels.size(); i++) {
            CacheLevel level = cacheLevels.get(i);

            if (level.cacheMap.containsKey(key)) {
                String value = level.cacheMap.get(key);


                if (i > 0) {
                    promoteToL1(key, value);
                    level.cacheMap.remove(key);
                }


                if (level.evictionPolicy.equals("LRU")) {
                    level.lruQueue.remove(key);
                    level.lruQueue.addLast(key);
                } else if (level.evictionPolicy.equals("LFU")) {
                    level.frequencyMap.put(key, level.frequencyMap.getOrDefault(key, 0) + 1);
                }

                return value;
            }
        }

        return null;
    }


    private void promoteToL1(String key, String value) {
        CacheLevel l1 = cacheLevels.get(0);


        if (l1.cacheMap.size() == l1.size) {
            if (l1.evictionPolicy.equals("LRU")) {
                evictLRU(l1);
            } else if (l1.evictionPolicy.equals("LFU")) {
                evictLFU(l1);
            }
        }


        l1.cacheMap.put(key, value);


        if (l1.evictionPolicy.equals("LRU")) {
            l1.lruQueue.addLast(key);
        } else if (l1.evictionPolicy.equals("LFU")) {
            l1.frequencyMap.put(key, l1.frequencyMap.getOrDefault(key, 0) + 1);
        }
    }


    public void displayCache() {
        for (int i = 0; i < cacheLevels.size(); i++) {
            CacheLevel level = cacheLevels.get(i);
            System.out.println("Cache Level " + (i + 1) + ": " + level.cacheMap);
        }
    }
}


public class Main {
    public static void main(String[] args) {
        MultiLevelCache cache = new MultiLevelCache();
        cache.addCacheLevel(3, "LRU");
        cache.addCacheLevel(2, "LFU");


        cache.put("A", "1");
        cache.put("B", "2");
        cache.put("C", "3");


        System.out.println("Get A: " + cache.get("A"));
        cache.put("D", "4");
        System.out.println("Get C: " + cache.get("C"));


        cache.displayCache();
    }
}
