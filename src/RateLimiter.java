import java.util.HashMap;
import java.util.Map;

public class RateLimiter {

    private static class RequestWindow {
        long windowStartTime;
        int requestCount;

        RequestWindow(long windowStartTime) {
            this.windowStartTime = windowStartTime;
            this.requestCount = 0;
        }
    }

    private final Map<String, RequestWindow> clientWindows
            = new HashMap<>();

    private final int maxRequests;
    private final long windowDurationMillis;

    public RateLimiter(int maxRequests, long windowDurationMillis) {
        if (maxRequests <= 0 || windowDurationMillis <= 0) {
            throw new IllegalArgumentException();
        }

        this.maxRequests = maxRequests;
        this.windowDurationMillis = windowDurationMillis;
    }

    public boolean allowRequest(String clientId, long currentTimeMillis) {
        RequestWindow window = clientWindows.get(clientId);

        if (window == null ||
                currentTimeMillis - window.windowStartTime >= windowDurationMillis) {

            window = new RequestWindow(currentTimeMillis);
            clientWindows.put(clientId, window);
        }

        if (window.requestCount >= maxRequests) {
            return false;
        }

        window.requestCount++;
        return true;
    }

    public static void main(String[] args) {
        RateLimiter limiter = new RateLimiter(3, 10_000);

        System.out.println(limiter.allowRequest("userA", 1000));  // true
        System.out.println(limiter.allowRequest("userA", 2000));  // true
        System.out.println(limiter.allowRequest("userA", 3000));  // true
        System.out.println(limiter.allowRequest("userA", 4000));  // false
        System.out.println(limiter.allowRequest("userA", 11000)); // true
    }
}