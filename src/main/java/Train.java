import java.util.Date;
import java.util.HashMap;
import java.util.Map;

 public class Train {

     private String type;
     private Long number;
     private Date arrival;
     private Date delayedArrival;
     private Date departure;
     private int platform;
     private String destination;
     private Long delay;

     public static Map<String, Train> generateTrains() {
         Map<String, Train> trains = new HashMap<>();
         for (int i = 0; i < 20; i++) {
             Train train = new Train();
             train.setType("Os");
             train.setNumber((long) i);
             train.setDestination("Poprad-Tatry");
             Date date = new Date();
             Date date2 = new Date();
             if (i <= 20 / 2)
                 date2.setSeconds(date2.getSeconds() + i * (-10));
             else
                 date2.setSeconds(date2.getSeconds() + (i - 10) * (10));
             train.setArrival(date2);
             date.setMinutes(date2.getMinutes());
             date.setHours(date2.getHours());
             date.setSeconds(date2.getSeconds()+40);
             date.setMonth(date2.getMonth());
             date.setDate(date2.getDate());
             train.setDeparture(date);
             trains.put(train.getType() + train.getNumber(), train);
         }
         for (int i = 0; i < 20; i++) {
             Train train = new Train();
             train.setType("R");
             train.setNumber((long) i);
             train.setDestination("Poprad-Tatry");
             Date date = new Date();
             Date date2 = new Date();
             if (i <= 20 / 2)
                 date2.setMinutes(date2.getMinutes() + i * (-1));
             else
                 date2.setSeconds(date2.getSeconds() + (i - 10) * (10));
             train.setArrival(date2);
             date.setMinutes(date2.getMinutes());
             date.setHours(date2.getHours());
             date.setSeconds(date2.getSeconds()+40);
             date.setMonth(date2.getMonth());
             date.setDate(date2.getDate());
             train.setDeparture(date);
             trains.put(train.getType() + train.getNumber(), train);
         }
         return trains;
     }
     public static Train generateTrain(int counter) {
             Train train = new Train();
             train.setType("Os");
             train.setNumber((long) counter);
             train.setDestination("Poprad-Tatry");
             Date date = new Date();
             Date date2 = new Date();
             train.setArrival(date2);
             date.setMinutes(date2.getMinutes());
             date.setHours(date2.getHours());
             date.setSeconds(date2.getSeconds()+40);
             date.setMonth(date2.getMonth());
             date.setDate(date2.getDate());
             train.setDeparture(date);

         return train;
     }
     public Date getDelayedArrival() {return delayedArrival;}

     public void setDelayedArrival(Date delayedArrival) {this.delayedArrival = delayedArrival;}

     public String getType() {
         return type;
     }

     public void setType(String type) {
         this.type = type;
     }

     public Long getNumber() {
         return number;
     }

     public void setNumber(Long number) {
         this.number = number;
     }

     public Date getArrival() {
         return arrival;
     }

     public void setArrival(Date arrival) {
         this.arrival = arrival;
     }

     public Date getDeparture() {
         return departure;
     }

     public void setDeparture(Date departure) {
         this.departure = departure;
     }

     public int getPlatform() {
         return platform;
     }

     public void setPlatform(int platform) {
         this.platform = platform;
     }

     public String getDestination() {
         return destination;
     }

     public void setDestination(String destination) {
         this.destination = destination;
     }

     public Long getDelay() {
         return delay;
     }

     public void setDelay(Long delay) {
         this.delay = delay;
     }
 }
