
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class Runner {
    public static ActorSystem<MainBoard.Command> system;
    public static ActorSystem<Dispecer.Command> dispecer;
    public static ActorSystem<Delay.Command> delay;
    public static List<ActorSystem<Platform.Command>> platforms = new ArrayList<>();

    public static ActorSystem<Delay.Command> getDelayActor() {
        if (delay == null){
            delay = ActorSystem.create(Delay.create(copy()), "delay");
        }
        return delay;
    }
    public static ActorSystem<MainBoard.Command> createAndGetActorSystem() {
        if (system == null){
            system = ActorSystem.create(MainBoard.create(copy()), "system");
        }
        return system;
    }
    public static ActorSystem<Dispecer.Command> getDispecer() {
        if (dispecer == null){

            dispecer = ActorSystem.create(Dispecer.create(copy()), "dispecer");
        }
        return dispecer;
    }
    public static List<ActorSystem<Platform.Command>> getPlatforms() {
        if (platforms == null || platforms.size()==0){
            ActorSystem<Platform.Command> platform1 = ActorSystem.create(Platform.create(), "platform1");
            ActorSystem<Platform.Command> platform2 = ActorSystem.create(Platform.create(), "platform2");
            ActorSystem<Platform.Command> platform3 = ActorSystem.create(Platform.create(), "platform3");
            ActorSystem<Platform.Command> platform4 = ActorSystem.create(Platform.create(), "platform4");
            ActorSystem<Platform.Command> platform5 = ActorSystem.create(Platform.create(), "platform5");
            platforms.add(platform1);
            platforms.add(platform2);
            platforms.add(platform3);
            platforms.add(platform4);
            platforms.add(platform5);
        }
        return platforms;
    }

    public static final Map<String, Train> generatedTrains = Train.generateTrains();
    public static void main(String[] args) {
        System.out.println("APP STARTED");
        ActorSystem<MainBoard.Command> system = createAndGetActorSystem();
        ActorSystem<Dispecer.Command> dispecer = getDispecer();
        ActorSystem<Delay.Command> delay = getDelayActor();
        List<ActorSystem<Platform.Command>> platforms = getPlatforms();
//        int counter = 0;
//        Train[] platformTrains = new Train[5];
//        while (true){
//            List<Train> valuesList = new ArrayList<>(generatedTrains.values());
//            Train randomTrain = valuesList.get((int) (Math.random() * valuesList.size()));
//            Date date1 = new Date();
//            if (Math.random() < 0.9) {
//
//                if (randomTrain.getPlatform() > 0) {
//                    if (platformTrains[randomTrain.getPlatform() - 1] != null && platformTrains[randomTrain.getPlatform() - 1].getDeparture().compareTo(date1) <= 0) {
//                        system.tell(new MainBoard.DepartureMessage(platformTrains[randomTrain.getPlatform() - 1]));
//                        platformTrains[randomTrain.getPlatform() - 1] = null;
//                    } else {
//                        Date date = new Date();
//                        if (randomTrain.getDelayedArrival() != null){
//                            if (date.compareTo(randomTrain.getDelayedArrival()) >= 0) {
//                                randomTrain.setDelay((date.getTime() - randomTrain.getArrival().getTime()) / 1000);
//                                system.tell(new MainBoard.ArrivalMessage(randomTrain));
//                                generatedTrains.remove(randomTrain.getType() + randomTrain.getNumber());
//                                platformTrains[randomTrain.getPlatform() - 1] = randomTrain;
//                                System.out.println("chosen train: "+ platformTrains[randomTrain.getPlatform()].getType() + platformTrains[randomTrain.getPlatform()].getNumber());
//                            }
//                        }else{
//                            if (date.compareTo(randomTrain.getArrival()) >= 0) {
//                                randomTrain.setDelay((date.getTime() - randomTrain.getArrival().getTime()) / 1000);
//                                system.tell(new MainBoard.ArrivalMessage(randomTrain));
//                                generatedTrains.remove(randomTrain.getType() + randomTrain.getNumber());
//                                platformTrains[randomTrain.getPlatform() - 1] = randomTrain;
//                                System.out.println("chosen train: "+ platformTrains[randomTrain.getPlatform()-1].getType() + platformTrains[randomTrain.getPlatform()-1].getNumber());
//                            }
//                        }
//
//                    }
//
//                } else {
//                    int num = (int) (Math.random() * 5) + 1;
//                    generatedTrains.get(randomTrain.getType() + randomTrain.getNumber()).setPlatform(num);
//                    randomTrain.setPlatform(num);
//                    system.tell(new MainBoard.PlatformMessage(randomTrain));
//
//                }
//
//            } else {
//                Date date = randomTrain.getArrival();
//                date.setMinutes(date.getMinutes() + 10);
//                generatedTrains.get(randomTrain.getType() + randomTrain.getNumber()).setDelayedArrival(date);
//                randomTrain.setDelayedArrival(date);
//                System.out.println("delayed train: "+randomTrain.getType() + randomTrain.getNumber());
//                system.tell(new MainBoard.DelayMessage(randomTrain));
//
//            }
//
//            try {
//                TimeUnit.SECONDS.sleep(5l);
//            } catch (InterruptedException e) {
//                break;
//            }
//        }
    }
    private static ConcurrentMap<String,Train> copy(){
        ConcurrentMap<String, Train> copy = new ConcurrentHashMap<>();
        for (Map.Entry<String, Train> entry : generatedTrains.entrySet()) {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }
}
