package ru.craftysoft.irrigationsystemservice;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.craftysoft.irrigationsystemservice.logic.PositionDeterminationOperation;

public class Application {

    private static final Logger logger = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("Обязательно должен быть передан путь до файла с данными в параметре --in");
        }
        var options = new Options();
        options.addOption("in", "in", true, "Путь до файла с данными");
        try {
            var parsed = new DefaultParser().parse(options, args);
            var in = parsed.getOptionValue("in");
            if (in == null) {
                throw new RuntimeException("Обязательно должен быть передан путь до файла с данными в параметре --in");
            }
            var positionDeterminationOperation = new PositionDeterminationOperation();
            positionDeterminationOperation.process(in);
        } catch (Exception e) {
            logger.error("Application.main.thrown", e);
        }
    }

}
