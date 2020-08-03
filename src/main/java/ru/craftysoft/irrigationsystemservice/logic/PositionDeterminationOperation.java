package ru.craftysoft.irrigationsystemservice.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.craftysoft.irrigationsystemservice.dto.FlowersByAngleData;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class PositionDeterminationOperation {

    private static final Logger logger = LogManager.getLogger(PositionDeterminationOperation.class);

    public double process(String in) throws IOException {
        var point = "PositionDeterminationOperation.process";
        logger.info("{}.in", point);
        try (var bufferedReader = Files.newBufferedReader(new File(in).toPath(), StandardCharsets.UTF_8)) {
            var flowersByAngleAndNameGrouping = new TreeMap<Double, FlowersByAngleData>();
            var firstLine = true;
            var irrigationSystemPositionX = 0d;
            var irrigationSystemPositionY = 0d;
            var irrigationSystemAngle = 0d;
            for (String line; (line = bufferedReader.readLine()) != null;) {
                if (firstLine) {
                    firstLine = false;
                    try {
                        var irrigationSystemPositions = line.split(" ");
                        irrigationSystemPositionX = Double.parseDouble(irrigationSystemPositions[0]);
                        irrigationSystemPositionY = Double.parseDouble(irrigationSystemPositions[1]);
                        irrigationSystemAngle = Double.parseDouble(irrigationSystemPositions[2]) * Math.PI / 180;
                        continue;
                    } catch (Exception e) {
                        logger.error("{}.end ошибка при получении информации о позиции поливалки: '{}'. Прекращаем обработку.", point, e.getMessage());
                        throw e;
                    }
                }
                try {
                    var flowersData = line.split(" ");
                    var name = flowersData[0];
                    var flowerPositionX = Double.parseDouble(flowersData[1]);
                    var flowerPositionY = Double.parseDouble(flowersData[2]);
                    addFlowerToGrouping(flowersByAngleAndNameGrouping, irrigationSystemPositionX, irrigationSystemPositionY, name, flowerPositionX, flowerPositionY);
                } catch (Exception e) {
                    logger.warn("{}.continue ошибка при добавлении информации о цветке: '{}'. Продолжаем обработку.", point, e.getMessage());
                }
            }
            double result = getResult(flowersByAngleAndNameGrouping, irrigationSystemAngle);
            logger.info("{}.out result={}", point, result);
            return result;
        } catch (Exception e) {
            logger.error("{}.thrown {}", point, e.getMessage());
            throw e;
        }
    }

    private double getResult(TreeMap<Double, FlowersByAngleData> flowersByAngleAndNameGrouping, double irrigationSystemAngle) {
        int flowersNamesCount = 0;
        int flowersCount = 0;
        var resultAngle = 0d;
        for (var entry : flowersByAngleAndNameGrouping.entrySet()) {
            var angle = entry.getKey();
            var maxAngle = angle + irrigationSystemAngle;
            SortedMap<Double, FlowersByAngleData> subMap;
            if (maxAngle <= 2 * Math.PI) {
                subMap = flowersByAngleAndNameGrouping.subMap(angle, true, maxAngle, true);
            } else {
                var deltaAngle = irrigationSystemAngle - (2 * Math.PI - angle);
                var headMap = flowersByAngleAndNameGrouping.headMap(deltaAngle, true);
                var tailMap = flowersByAngleAndNameGrouping.tailMap(angle);
                subMap = new TreeMap<>();
                subMap.putAll(headMap);
                subMap.putAll(tailMap);
            }
            Set<String> currentFlowersNames = new HashSet<>();
            int currentFlowersCount = 0;
            for (var subMapEntry : subMap.entrySet()) {
                currentFlowersNames.addAll(subMapEntry.getValue().getFlowersNames());
                currentFlowersCount += subMapEntry.getValue().getDistances().size();
            }
            if (currentFlowersNames.size() > flowersNamesCount
                    || (currentFlowersNames.size() == flowersNamesCount
                    && currentFlowersCount > flowersCount)) {
                flowersNamesCount = currentFlowersNames.size();
                flowersCount = currentFlowersCount;
                resultAngle = angle;
            }
        }
        return resultAngle * 180 / Math.PI;
    }

    private double resolveAngle(double relativeFlowerPositionX, double relativeFlowerPositionY) {
        if (relativeFlowerPositionX == 0d) {
            if (relativeFlowerPositionY > 0d) {
                return Math.PI;
            } else {
                return 1.5 * Math.PI;
            }
        }
        var angle = Math.atan(relativeFlowerPositionY / relativeFlowerPositionX);
        if (relativeFlowerPositionX < 0 && relativeFlowerPositionY < 0) {
            return angle + Math.PI;
        } else if (relativeFlowerPositionX < 0) {
            return Math.PI + angle;
        } else if (relativeFlowerPositionY < 0) {
            return 2 * Math.PI + angle;
        }
        return angle;
    }

    private void addFlowerToGrouping(Map<Double, FlowersByAngleData> flowersByAngleAndNameGrouping,
                                     double irrigationSystemPositionX,
                                     double irrigationSystemPositionY,
                                     String name,
                                     double flowerPositionX,
                                     double flowerPositionY) {
        var relativeFlowerPositionX = flowerPositionX - irrigationSystemPositionX;
        var relativeFlowerPositionY = flowerPositionY - irrigationSystemPositionY;
        if (relativeFlowerPositionX == 0d && relativeFlowerPositionY == 0d) {
            throw new RuntimeException();
        }
        double angle = resolveAngle(relativeFlowerPositionX, relativeFlowerPositionY);
        var distance = Math.sqrt(relativeFlowerPositionY * relativeFlowerPositionY + relativeFlowerPositionX * relativeFlowerPositionX);
        logger.debug("PositionDeterminationOperation.addFlowerToGrouping x={}, y={} => angle={}, distance={}", flowerPositionX, flowerPositionY, angle, distance);
        var flowersByAngleData = flowersByAngleAndNameGrouping.get(angle);
        if (flowersByAngleData == null) {
            flowersByAngleData = new FlowersByAngleData();
            flowersByAngleData.getDistances().add(distance);
            flowersByAngleData.getFlowersNames().add(name);
            flowersByAngleAndNameGrouping.put(angle, flowersByAngleData);
        } else {
            if (flowersByAngleData.getDistances().contains(distance)) {
                throw new RuntimeException("2 цветка не могут расти в одной лунке");
            }
            flowersByAngleData.getDistances().add(distance);
            flowersByAngleData.getFlowersNames().add(name);
        }
    }

}
