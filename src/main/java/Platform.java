import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Platform extends AbstractBehavior<Platform.Command> {
    private ConcurrentMap<String, Train> trains = new ConcurrentHashMap<>();
    private Train actualTrain = null;

    private Platform(ActorContext<Command> context) {
        super(context);
    }
    public static Behavior<Command> create() {
        return Behaviors.setup(context -> {
            return Behaviors.withTimers(timers -> {
                timers.startTimerWithFixedDelay(new Trigger(), Duration.ofSeconds(4));
                return new Platform(context);
            });
        });
    }

    @Override
    public Receive<Platform.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(RecieveTrain.class, this::recieveTrain)
                .onMessage(DelayMessage.class, this::delayMessage)
                .onMessage(Trigger.class, this::arrivalTrain)
                .build();
    }

    private Behavior<Platform.Command> arrivalTrain(Trigger trigger) {
        Date date = new Date();
        if (actualTrain==null){
            if (trains.size()>0){
                List<Train> copy = new ArrayList<>(trains.values());
                Train train = copy.get((0));
                if (train.getDelayedArrival()==null){
                    if (train.getArrival().compareTo(date)<=0){
                        actualTrain=train;
                        trains.remove(train.getType()+train.getNumber());
                        Runner.createAndGetActorSystem().tell(new MainBoard.ArrivalMessage(train));
                    }
                }else {
                    if (train.getDelayedArrival().compareTo(date)<=0){
                        actualTrain=train;
                        trains.remove(train.getType()+train.getNumber());
                        Runner.createAndGetActorSystem().tell(new MainBoard.ArrivalMessage(train));
                    }
                }
            }
        }else{
            if (actualTrain.getDeparture().compareTo(date)<=0){
                Runner.createAndGetActorSystem().tell(new MainBoard.DepartureMessage(actualTrain));
                actualTrain=null;
            }
        }
        System.out.printf("");



        return Behaviors.same();
    }

    private Behavior<Platform.Command> recieveTrain(RecieveTrain recieveTrain) {
        Train train = recieveTrain.train;
        trains.put(train.getType()+train.getNumber(),train);
//        getContext().getLog().info("Recieved Train: {}", train.getType()+train.getNumber());

        return Behaviors.same();
    }

    public interface Command {}

    private Behavior<Platform.Command> delayMessage(DelayMessage delayMessage) {
        Train train = delayMessage.train;
        if (trains.get(train.getType()+train.getNumber()) != null){
            trains.get(train.getType()+train.getNumber()).setDelayedArrival(train.getDelayedArrival());
        }else{
            trains.put(train.getType()+train.getNumber(),train);

        }
//        trains.get(train.getType()+train.getNumber()).setDelayedArrival(train.getDelayedArrival());
//        getContext().getLog().info("Delayed Train: {} set to new arrival: {}", train.getType()+train.getNumber(), train.getDelayedArrival());

        return Behaviors.same();
    }

    public static class DelayMessage implements Command{
        public final Train train;
        public DelayMessage(Train train) {
            this.train = train;
        }
    }

    public static class RecieveTrain implements Command{
        public final Train train;
        public RecieveTrain(Train train) {
            this.train = train;
        }

    }
    public static class Trigger implements Platform.Command {
    }

    private void print (){
        System.out.format("=====================================================================================================================");
        System.out.println();
        System.out.format("=====================================================================================================================");
        System.out.println();
    }

}