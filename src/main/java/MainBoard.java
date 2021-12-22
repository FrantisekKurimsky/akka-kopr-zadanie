import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MainBoard extends AbstractBehavior<MainBoard.Command> {
    ConcurrentMap<String, Train> trains;
    ActorRef<Dispecer.Command> dispecer;
    ActorRef<Delay.Command> delay;
    List<ActorRef<Platform.Command>> platforms = new ArrayList<>();


    private MainBoard(ActorContext<Command> context, ConcurrentMap<String, Train> trains) {
        super(context);
        this.trains = trains;
        dispecer = context.spawn(Dispecer.create(copy()),"dispecer");
        delay = context.spawn(Delay.create(copy()),"delay");
        ActorRef<Platform.Command> platform1 = context.spawn(Platform.create(), "platform1");
        ActorRef<Platform.Command> platform2 = context.spawn(Platform.create(), "platform2");
        ActorRef<Platform.Command> platform3 = context.spawn(Platform.create(), "platform3");
        ActorRef<Platform.Command> platform4 = context.spawn(Platform.create(), "platform4");
        ActorRef<Platform.Command> platform5 = context.spawn(Platform.create(), "platform5");
        platforms.add(platform1);
        platforms.add(platform2);
        platforms.add(platform3);
        platforms.add(platform4);
        platforms.add(platform5);
    }
    public static Behavior<Command> create(ConcurrentMap<String, Train> receivedTrains) {
        return Behaviors.setup(context -> {
           return new MainBoard(context, receivedTrains);
        });
//        return Behaviors.setup(context -> {
//            return Behaviors.withTimers(timers -> {
//                timers.startTimerWithFixedDelay(new Trigger(), Duration.ofSeconds(2));
//                return new MainBoard(context, receivedTrains);
//            });
//        });
    }

    @Override
    public Receive<MainBoard.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(ArrivalMessage.class, this::arrivedTrain)
                .onMessage(DepartureMessage.class, this::departuredTrain)
                .onMessage(PlatformMessage.class, this::setPlatform)
                .onMessage(DelayMessage.class, this::setDelay)
                .onMessage(MainBoard.Trigger.class, this::printTable)
                .build();
    }


    private Behavior<MainBoard.Command> arrivedTrain(ArrivalMessage arrivalMessage) {
        Train train = arrivalMessage.train;
        return Behaviors.same();
    }
    private Behavior<MainBoard.Command> printTable(Trigger trigger) {
        print();
        return Behaviors.same();
    }

    private Behavior<MainBoard.Command> setPlatform(PlatformMessage platformMessage) {


        Train train = platformMessage.train;
        Train mapTrain = trains.get(train.getType()+train.getNumber());
        mapTrain.setPlatform(train.getPlatform());
        platforms.get(platformMessage.train.getPlatform()-1).tell(new Platform.RecieveTrain(mapTrain));
//        System.out.println("Platform assigned to train: "+train.getType()+train.getNumber()+" number of platform: "+train.getPlatform());

        return Behaviors.same();
    }

    private Behavior<MainBoard.Command> departuredTrain(DepartureMessage departureMessage) {
        Train train = departureMessage.train;
        trains.remove(train.getType()+train.getNumber());
        return Behaviors.same();
    }

    private Behavior<MainBoard.Command> setDelay(DelayMessage delayMessage) {

        Train train = delayMessage.train;
        Train mapTrain = trains.get(train.getType()+train.getNumber());
        if (mapTrain!=null){
            trains.get(train.getType()+train.getNumber()).setDelayedArrival(train.getDelayedArrival());
            mapTrain.setDelayedArrival(train.getDelayedArrival());
            mapTrain.setDelay(train.getDelayedArrival().getTime()-train.getArrival().getTime());
            if (mapTrain.getPlatform()>0){
                platforms.get(mapTrain.getPlatform()-1).tell(new Platform.DelayMessage(mapTrain));
            }
        }

        System.out.println("Delayed time for train: "+train.getType()+train.getNumber()+" set to arrival: "+train.getArrival());
        return Behaviors.same();
    }

    public interface Command {}

    public static class DepartureMessage implements Command{
        public final Train train;
        public DepartureMessage(Train train) {
            this.train = train;
        }
    }

    public static class ArrivalMessage implements Command{
        public final Train train;
        public ArrivalMessage(Train train) {
            this.train = train;
        }

    }
    public static class PlatformMessage implements Command{
        public final Train train;
        public PlatformMessage(Train train) {
            this.train = train;
        }

    }
    public static class DelayMessage implements Command{
        public final Train train;
        public DelayMessage(Train train) {
            this.train = train;
        }

    }
    public static class Trigger implements Command { }
    private void print(){
        Date date = new Date();
        List<Train> copy = new ArrayList<>(trains.values());
        copy.sort(Comparator.comparing(Train::getDeparture));
        int number = 20;
        if (copy.size()<20){
            number=copy.size();
        }
//        System.out.println("Type\tDeparture\tarrival\tPlatform\tDelay");
        System.out.format("=====================================================================================================================");
        System.out.println();
        System.out.format("|%5s    |%33s    |%33s    |%10s    |%10s    |", "Type", "Departure", "Arrival","Platform","Delay");
        System.out.println();
        System.out.format("=====================================================================================================================");
        System.out.println();
        for (int i=0; i<number; i++) {
            Train entry = copy.get(i);
            if (entry.getDelayedArrival()==null){
                if (date.compareTo(entry.getArrival())<=0){
                    entry.setDelay(0l);
                }else{
                    entry.setDelay((date.getTime()-entry.getArrival().getTime())/1000);
                }

//                System.out.println("|"+entry.getType()+entry.getNumber()+"\t"+ entry.getDeparture()+"\t"+  entry.getArrival()+"\t"+ entry.getPlatform()+"\t"+ entry.getDelay()+"|");
            }else{
                entry.setDelay((entry.getDelayedArrival().getTime()-entry.getArrival().getTime())/1000);
//                entry.setDelay(2000l);
//                System.out.println("|"+entry.getType()+entry.getNumber()+"\t"+ entry.getDeparture()+"\t"+  entry.getArrival()+"\t"+ entry.getPlatform()+"\t"+  entry.getDelay()+"|");
            }
//            System.out.println();
            System.out.format("|%5s    |%33s    |%33s    |%10s    |%10s    |", entry.getType()+entry.getNumber(), entry.getDeparture(), entry.getArrival(),entry.getPlatform(),entry.getDelay());
            System.out.println();

        }
        System.out.format("=====================================================================================================================");
        System.out.println();
    }
    private ConcurrentMap<String,Train> copy(){
        ConcurrentMap<String, Train> copy = new ConcurrentHashMap<>();
        for (Map.Entry<String, Train> entry : trains.entrySet()) {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }

}