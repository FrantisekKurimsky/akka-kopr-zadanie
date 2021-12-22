import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.sql.DatabaseMetaData;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class Delay extends AbstractBehavior<Delay.Command> {
    ConcurrentMap<String, Train> trains;
    ActorSystem<MainBoard.Command> system ;
    private Delay(ActorContext<Command> context, ConcurrentMap<String, Train> trains) {
        super(context);
        this.trains = trains;
    }
    public static Behavior<Command> create(ConcurrentMap<String, Train> receivedTrains) {
        return Behaviors.setup(context -> {
            return Behaviors.withTimers(timers -> {
                timers.startTimerWithFixedDelay(new TriggerMeasurement(), Duration.ofSeconds(10));
                return new Delay(context, receivedTrains);
            });
        });
    }

    public static class TriggerMeasurement implements Command {
    }


    @Override
    public Receive<Delay.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(TriggerMeasurement.class, this::sendtrain)
                .build();
    }

    private Behavior<Delay.Command> sendtrain(TriggerMeasurement command) {
        if (Math.random()<0.1){
            List<Train> copy = new ArrayList<>(trains.values());
            int number = (int)(Math.random()*copy.size());
            Train train = copy.get(number);
            Date date = new Date();
            date.setMinutes(train.getArrival().getMinutes()+10);
            train.setDelayedArrival(date);
//            trains.remove(train.getType()+train.getNumber());
            Runner.createAndGetActorSystem().tell(new MainBoard.DelayMessage(train));
        }

        return Behaviors.same();
    }



    public interface Command {}

    public static class SendTrain implements Command{

        private final int number;
        public SendTrain(int number ) {

            this.number = number;
        }
    }



}