package wooteco.subway.path.application;

import wooteco.subway.path.domain.Fare;
import wooteco.subway.path.exception.InvalidDistanceArgumentException;

import java.util.Arrays;

public enum DistanceExtraFarePolicy {
    OF_10KM_OR_LESS(0, 0.0),
    OF_OVER_10KM_AND_50KM_OR_LESS(10, 5.0),
    OF_OVER_50KM(50, 8.0);

    private final int lowerBound;
    private final double chargeUnit;

    DistanceExtraFarePolicy(int lowerBound, double chargeUnit) {
        this.lowerBound = lowerBound;
        this.chargeUnit = chargeUnit;
    }

    public static Fare calculateFareByDistance(int distance) {
        if (distance <= 10) {
            return FarePolicy.BASIC_FARE;
        }

        DistanceExtraFarePolicy lastInstance = Arrays.stream(values())
                .filter(policy -> policy.lowerBound < distance)
                .reduce((first, second) -> second)
                .orElseThrow(InvalidDistanceArgumentException::new);

        return lastInstance.calculateFare(distance);
    }

    private Fare calculateFare(int distance) {
        return calculateExtraFare(distance - this.lowerBound).add(calculateFareByDistance(this.lowerBound));
    }

    private Fare calculateExtraFare(int distance) {
        int extraFare = (int) (Math.ceil(distance / this.chargeUnit) * 100);
        return new Fare(extraFare);
    }
}
