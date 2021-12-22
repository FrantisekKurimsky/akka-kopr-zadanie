import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class Dispecer extends AbstractBehavior<Dispecer.Command> {
    ConcurrentMap<String, Train> trains;
    ActorSystem<MainBoard.Command> system ;
    private Dispecer(ActorContext<Command> context, ConcurrentMap<String, Train> trains) {
        super(context);
        this.trains = trains;
    }
    public static Behavior<Command> create(ConcurrentMap<String, Train> receivedTrains) {
        return Behaviors.setup(context -> {
            return Behaviors.withTimers(timers -> {
                timers.startTimerWithFixedDelay(new TriggerMeasurement(), Duration.ofSeconds(2));
                return new Dispecer(context, receivedTrains);
            });
        });
    }

    public static class TriggerMeasurement implements Command {
    }


    @Override
    public Receive<Dispecer.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(TriggerMeasurement.class, this::sendtrain)
                .build();
    }

    private Behavior<Dispecer.Command> sendtrain(TriggerMeasurement command) {
        List<Train> copy = new ArrayList<>(trains.values());
        copy.sort(Comparator.comparing(Train::getDeparture));
//        int number = (int)(Math.random()*copy.size());
        if (copy.size()>0){
            Train train = copy.get(0);
            int plat = (int)(Math.random()*5)+1;
            train.setPlatform(plat);
            trains.remove(train.getType()+train.getNumber());

            getContext().classicActorContext().parent().tell(new MainBoard.PlatformMessage(train), getContext().classicActorContext().self());
//             getContext().classicActorContext().parent().tell(new MainBoard.PlatformMessage(train));
//            this.getContext().getSelf().tell(new MainBoard.PlatformMessage(train));
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