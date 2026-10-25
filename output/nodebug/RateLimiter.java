/*
 * Decompiled with CFR 0.152.
 */
import java.util.HashMap;
import java.util.Map;

public class RateLimiter {
    private final Map<String, RequestWindow> clientWindows = new HashMap<String, RequestWindow>();
    private final int maxRequests;
    private final long windowDurationMillis;

    public RateLimiter(int n, long l) {
        if (n <= 0 || l <= 0L) {
            throw new IllegalArgumentException();
        }
        this.maxRequests = n;
        this.windowDurationMillis = l;
    }

    public boolean allowRequest(String string, long l) {
        RequestWindow requestWindow = this.clientWindows.get(string);
        if (requestWindow == null || l - requestWindow.windowStartTime >= this.windowDurationMillis) {
            requestWindow = new RequestWindow(l);
            this.clientWindows.put(string, requestWindow);
        }
        if (requestWindow.requestCount >= this.maxRequests) {
            return false;
        }
        ++requestWindow.requestCount;
        return true;
    }

    public static void main(String[] stringArray) {
        RateLimiter rateLimiter = new RateLimiter(3, 10000L);
        System.out.println(rateLimiter.allowRequest("userA", 1000L));
        System.out.println(rateLimiter.allowRequest("userA", 2000L));
        System.out.println(rateLimiter.allowRequest("userA", 3000L));
        System.out.println(rateLimiter.allowRequest("userA", 4000L));
        System.out.println(rateLimiter.allowRequest("userA", 11000L));
    }

    private static class RequestWindow {
        long windowStartTime;
        int requestCount;

        RequestWindow(long l) {
            this.windowStartTime = l;
            this.requestCount = 0;
        }
    }
}
