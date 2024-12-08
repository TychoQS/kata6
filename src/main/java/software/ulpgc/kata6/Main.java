package software.ulpgc.kata6;

import software.ulpgc.kata6.architecture.CommandFactory;
import software.ulpgc.kata6.architecture.control.CalculateTaskDaysWithAvailabilityCommand;
import software.ulpgc.kata6.architecture.control.CalculateTaskDaysWithRestrictionCommand;
import software.ulpgc.kata6.architecture.control.Command;
import software.ulpgc.kata6.spark.SparkRequestAdapter;
import software.ulpgc.kata6.spark.SparkResponseAdapter;
import spark.Spark;

public class Main {

    public static final String TASKDAYS_WITH_RESTRICTION = "/taskdays-with-restriction";
    public static final String TASKDAYS_WITH_AVAILABILITY = "/taskdays-with-availability";

    public static void main(String[] args) {
        CommandFactory factory = new CommandFactory();
        factory.register(TASKDAYS_WITH_RESTRICTION, CalculateTaskDaysWithRestrictionCommand::new)
                .register(TASKDAYS_WITH_AVAILABILITY, CalculateTaskDaysWithAvailabilityCommand::new);
        Spark.port(8080);
        setEndpoint(TASKDAYS_WITH_RESTRICTION, factory);
        setEndpoint(TASKDAYS_WITH_AVAILABILITY, factory);
    }

    private static void setEndpoint(String pathParameter, CommandFactory factory) {
        Spark.get(pathParameter, ((request, response) -> {
            Command command = factory.get(request.pathInfo(), new SparkRequestAdapter(request), new SparkResponseAdapter(response));
            command.execute();
            response.status(200);
            response.type("application/json");
            return response.body();
        }));
    }
}
