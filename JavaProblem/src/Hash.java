import java.util.*;

class TokenBucket {

    int tokens;
    int maxTokens;
    long lastRefillTime;
    int refillRate;

    TokenBucket(int maxTokens, int refillRate) {
        this.tokens = maxTokens;
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.lastRefillTime = System.currentTimeMillis();
    }

    synchronized boolean allowRequest() {

        refill();

        if(tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    void refill() {

        long now = System.currentTimeMillis();
        long elapsed = (now - lastRefillTime)/1000;

        int newTokens = (int)(elapsed * refillRate);

        if(newTokens > 0) {
            tokens = Math.min(maxTokens, tokens + newTokens);
            lastRefillTime = now;
        }
    }
}

class RateLimiter {

    HashMap<String, TokenBucket> clients = new HashMap<>();

    int LIMIT = 1000;

    public boolean checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId,new TokenBucket(LIMIT, LIMIT/3600));

        TokenBucket bucket = clients.get(clientId);

        if(bucket.allowRequest()) {
            System.out.println("Allowed ("+bucket.tokens+" requests remaining)");
            return true;
        }
        else {
            System.out.println("Denied (Rate limit exceeded)");
            return false;
        }
    }
}

public class PS06 {

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        for(int i=0;i<5;i++)
            limiter.checkRateLimit("abc123");
    }
}