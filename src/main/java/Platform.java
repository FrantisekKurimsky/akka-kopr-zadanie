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
        if (actualTrain == null) {
            if (trains.size() > 0) {
                List<Train> copy = new ArrayList<>(trains.values());
                Train train = copy.get((0));
                if (train.getDelayedArrival() == null) {
                    if (train.getArrival().compareTo(date) <= 0) {
                        actualTrain = train;
                        trains.remove(train.getType() + train.getNumber());
                        getContext().classicActorContext().parent().tell(new MainBoard.ArrivalMessage(train), getContext().classicActorContext().self());
//                        Runner.createAndGetActorSystem().tell(new MainBoard.ArrivalMessage(train));
                    }
                } else {
                    if (train.getDelayedArrival().compareTo(date) <= 0) {
                        actualTrain = train;
                        trains.remove(train.getType() + train.getNumber());
                        getContext().classicActorContext().parent().tell(new MainBoard.ArrivalMessage(train), getContext().classicActorContext().self());
//                        Runner.createAndGetActorSystem().tell(new MainBoard.ArrivalMessage(train));
                    }
                }
            }
        } else {
            if (actualTrain.getDeparture().compareTo(date) <= 0) {
                getContext().classicActorContext().parent().tell(new MainBoard.DepartureMessage(actualTrain), getContext().classicActorContext().self());
//                Runner.createAndGetActorSystem().tell(new MainBoard.DepartureMessage(actualTrain));
                actualTrain = null;
            }
        }
        if (Config.printPlatforms) {
            synchronized (getContext().classicActorContext().parent()) {
                print();
            }
        }


        return Behaviors.same();
    }

    private Behavior<Platform.Command> recieveTrain(RecieveTrain recieveTrain) {
        Train train = recieveTrain.train;
        trains.put(train.getType() + train.getNumber(), train);
//        getContext().getLog().info("Recieved Train: {}", train.getType()+train.getNumber());

        return Behaviors.same();
    }

    private Behavior<Platform.Command> delayMessage(DelayMessage delayMessage) {
        Train train = delayMessage.train;
        if (trains.get(train.getType() + train.getNumber()) != null) {
            trains.get(train.getType() + train.getNumber()).setDelayedArrival(train.getDelayedArrival());
        } else {
            trains.put(train.getType() + train.getNumber(), train);

        }
//        trains.get(train.getType()+train.getNumber()).setDelayedArrival(train.getDelayedArrival());
//        getContext().getLog().info("Delayed Train: {} set to new arrival: {}", train.getType()+train.getNumber(), train.getDelayedArrival());

        return Behaviors.same();
    }

    private void print() {
        System.out.format("====================================          " + this.getContext().classicActorContext().self().path().name() + "               ==========================================");
        System.out.println();
        System.out.format("|%5s    |%33s    |%33s    |%10s    |%10s    |", "Type", "Departure", "Arrival", "Platform", "Delay");
        System.out.println();
        System.out.format("=====================================             AKTUALNY VLAK               =======================================");
        System.out.println();

        if (actualTrain == null) {
            System.out.format("|%5s    |%33s    |%33s    |%10s    |%10s    |", "null", "null", "null", "null", "null");
        } else {
            System.out.format("|%5s    |%33s    |%33s    |%10s    |%10s    |", actualTrain.getType() + actualTrain.getNumber(), actualTrain.getDeparture(), actualTrain.getDeparture(), actualTrain.getPlatform(), actualTrain.getDelay());
        }
        System.out.println();
        Date date = new Date();
        List<Train> copy = new ArrayList<>(trains.values());
        copy.sort(Comparator.comparing(Train::getDeparture));
        if (copy.size() > 0) {

            System.out.format("=====================================               DALSIE VLAKY              =======================================");
            System.out.println();
            int number = 5;
            if (copy.size() < 5) {
                number = copy.size();
            }
            for (int i = 0; i < number; i++) {
                Train entry = copy.get(i);
                if (entry.getDelayedArrival() == null) {
                    if (date.compareTo(entry.getArrival()) <= 0) {
                        entry.setDelay(0l);
                    } else {
                        entry.setDelay((date.getTime() - entry.getArrival().getTime()) / 1000);
                    }

//                System.out.println("|"+entry.getType()+entry.getNumber()+"\t"+ entry.getDeparture()+"\t"+  entry.getArrival()+"\t"+ entry.getPlatform()+"\t"+ entry.getDelay()+"|");
                } else {
                    entry.setDelay((entry.getDelayedArrival().getTime() - entry.getArrival().getTime()) / 1000);
//                entry.setDelay(2000l);
//                System.out.println("|"+entry.getType()+entry.getNumber()+"\t"+ entry.getDeparture()+"\t"+  entry.getArrival()+"\t"+ entry.getPlatform()+"\t"+  entry.getDelay()+"|");
                }
//            System.out.println();
                System.out.format("|%5s    |%33s    |%33s    |%10s    |%10s    |", entry.getType() + entry.getNumber(), entry.getDeparture(), entry.getArrival(), entry.getPlatform(), entry.getDelay());
                System.out.println();

            }
        }

        System.out.format("=====================================================================================================================");
        System.out.println();
    }

    public interface Command {
    }

    public static class DelayMessage implements Command {
        public final Train train;

        public DelayMessage(Train train) {
            this.train = train;
        }
    }

    public static class RecieveTrain implements Command {
        public final Train train;

        public RecieveTrain(Train train) {
            this.train = train;
        }

    }

    public static class Trigger implements Platform.Command {
    }

}