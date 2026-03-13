import java.util.*;

class DNSEntry {
    String ipAddress;
    long expiryTime;

    DNSEntry(String ip, int ttlSeconds) {
        this.ipAddress = ip;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {

    private int capacity;

    private LinkedHashMap<String, DNSEntry> cache;

    private int hits = 0;
    private int misses = 0;

    public DNSCache(int capacity) {
        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };

        startCleanupThread();
    }

    public synchronized String resolve(String domain) {

        DNSEntry entry = cache.get(domain);

        if (entry != null) {

            if (!entry.isExpired()) {
                hits++;
                System.out.println("Cache HIT → " + entry.ipAddress);
                return entry.ipAddress;
            } else {
                cache.remove(domain);
                System.out.println("Cache EXPIRED");
            }
        }

        misses++;

        String newIP = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(newIP, 5));

        System.out.println("Cache MISS → Query upstream → " + newIP);

        return newIP;
    }

    // Simulate upstream DNS query
    private String queryUpstreamDNS(String domain) {

        Random r = new Random();

        return "172.217.14." + (100 + r.nextInt(50));
    }

    public void getCacheStats() {

        int total = hits + misses;
        double hitRate = total == 0 ? 0 : ((double) hits / total) * 100;

        System.out.println("Cache Hits: " + hits);
        System.out.println("Cache Misses: " + misses);
        System.out.println("Hit Rate: " + String.format("%.2f", hitRate) + "%");
    }

    private void startCleanupThread() {

        Thread cleaner = new Thread(() -> {

            while (true) {
                try {
                    Thread.sleep(3000);

                    Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry<String, DNSEntry> entry = it.next();

                        if (entry.getValue().isExpired()) {
                            it.remove();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        cleaner.setDaemon(true);
        cleaner.start();
    }
}

public class PS03 {

    public static void main(String[] args) throws Exception {

        DNSCache cache = new DNSCache(3);

        cache.resolve("google.com");
        cache.resolve("google.com");

        Thread.sleep(6000); // wait for TTL expiry

        cache.resolve("google.com");

        cache.resolve("github.com");
        cache.resolve("openai.com");
        cache.resolve("stackoverflow.com");

        cache.getCacheStats();
    }
}
