/*
 * Decompiled with CFR 0.152.
 */
import java.util.HashMap;
import java.util.Map;

public class RateLimiter {
    private final Map<String, RequestWindow> clientWindows = new HashMap<String, RequestWindow>();
    private final int maxRequests;
    private final long windowDurationMillis;

    public RateLimiter(int maxRequests, long windowDurationMillis) {
        if (maxRequests <= 0 || windowDurationMillis <= 0L) {
            throw new IllegalArgumentException();
        }
        this.maxRequests = maxRequests;
        this.windowDurationMillis = windowDurationMillis;
    }

    public boolean allowRequest(String clientId, long currentTimeMillis) {
        RequestWindow window = this.clientWindows.get(clientId);
        if (window == null || currentTimeMillis - window.windowStartTime >= this.windowDurationMillis) {
            window = new RequestWindow(currentTimeMillis);
            this.clientWindows.put(clientId, window);
        }
        if (window.requestCount >= this.maxRequests) {
            return false;
        }
        ++window.requestCount;
        return true;
    }

    public static void main(String[] args) {
        RateLimiter limiter = new RateLimiter(3, 10000L);
        System.out.println(limiter.allowRequest("userA", 1000L));
        System.out.println(limiter.allowRequest("userA", 2000L));
        System.out.println(limiter.allowRequest("userA", 3000L));
        System.out.println(limiter.allowRequest("userA", 4000L));
        System.out.println(limiter.allowRequest("userA", 11000L));
    }

    private static class RequestWindow {
        long windowStartTime;
        int requestCount;

        RequestWindow(long windowStartTime) {
            this.windowStartTime = windowStartTime;
            this.requestCount = 0;
        }
    }
}
