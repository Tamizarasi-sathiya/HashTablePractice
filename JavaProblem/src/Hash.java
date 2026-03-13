import java.util.*;

class Event {
    String url;
    String userId;
    String source;

    Event(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

class AnalyticsDashboard {

    HashMap<String, Integer> pageViews = new HashMap<>();
    HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();
    HashMap<String, Integer> trafficSources = new HashMap<>();

    public void processEvent(Event e) {

        pageViews.put(e.url, pageViews.getOrDefault(e.url, 0) + 1);

        uniqueVisitors.putIfAbsent(e.url, new HashSet<>());
        uniqueVisitors.get(e.url).add(e.userId);

        trafficSources.put(e.source, trafficSources.getOrDefault(e.source, 0) + 1);
    }

    public void getDashboard() {

        System.out.println("Top Pages:");

        PriorityQueue<Map.Entry<String,Integer>> pq =
                new PriorityQueue<>((a,b)->b.getValue()-a.getValue());

        pq.addAll(pageViews.entrySet());

        int count = 0;

        while(!pq.isEmpty() && count < 10) {
            Map.Entry<String,Integer> e = pq.poll();

            String page = e.getKey();
            int views = e.getValue();
            int unique = uniqueVisitors.get(page).size();

            System.out.println(page + " - " + views + " views (" + unique + " unique)");
            count++;
        }

        System.out.println("\nTraffic Sources:");

        int total = trafficSources.values().stream().mapToInt(i->i).sum();

        for(String s : trafficSources.keySet()) {
            int c = trafficSources.get(s);
            double percent = (c*100.0)/total;
            System.out.printf("%s : %.2f%%\n",s,percent);
        }
    }
}

public class PS05 {
    public static void main(String[] args) {

        AnalyticsDashboard dashboard = new AnalyticsDashboard();

        dashboard.processEvent(new Event("/article/breaking-news","user1","google"));
        dashboard.processEvent(new Event("/article/breaking-news","user2","facebook"));
        dashboard.processEvent(new Event("/sports/championship","user3","direct"));
        dashboard.processEvent(new Event("/article/breaking-news","user1","google"));

        dashboard.getDashboard();
    }
}