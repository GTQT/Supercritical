package supercritical.api.nuclear.fission.components;

import lombok.Getter;
import lombok.Setter;
import supercritical.api.capability.ICoolantHandler;
import supercritical.api.nuclear.fission.ICoolantStats;

import java.util.List;

public class CoolantChannel extends ReactorComponent {

    @Getter
    private final ICoolantStats coolant;
    @Getter
    private final ICoolantHandler inputHandler;
    @Getter
    private final ICoolantHandler outputHandler;
    // Allows fission reactors to heat up less than a full liter of coolant.
    public double partialCoolant;
    @Setter
    @Getter
    private double weight;
    private int relatedFuelRodPairs;

    public CoolantChannel(double maxTemperature, double thermalConductivity, ICoolantStats coolant, double mass,
                          ICoolantHandler inputHandler, ICoolantHandler outputHandler) {
        super(coolant.getModeratorFactor(), maxTemperature, thermalConductivity, mass,
                true);
        this.coolant = coolant;
        this.weight = 0;
        this.inputHandler = inputHandler;
        this.outputHandler = outputHandler;
    }

    public static void normalizeWeights(List<CoolantChannel> effectiveCoolantChannels) {
        double sum = 0;
        for (CoolantChannel channel : effectiveCoolantChannels) {
            sum += channel.weight;
        }
        for (CoolantChannel channel : effectiveCoolantChannels) {
            channel.weight /= sum;
        }
    }

    public void addFuelRodPair() {
        relatedFuelRodPairs++;
    }

    public void computeWeightFromFuelRodMap() {
        this.weight = relatedFuelRodPairs * 2;
    }

    @Override
    public double getAbsorptionFactor(boolean controlsInserted, boolean isThermal) {
        return isThermal ? coolant.getSlowAbsorptionFactor() : coolant.getFastAbsorptionFactor();
    }
}