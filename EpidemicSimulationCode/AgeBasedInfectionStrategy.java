
public class AgeBasedInfectionStrategy implements InfectionStrategy {

    @Override
    public boolean shouldInfect(Healthy person, Virus virus) {

        int chance = virus.getSpreadPercentage();

        // Under 30: two close contacts required
        if (person.getAge() < 30) {

            person.addExposure();

            if (person.getExposureCount() < 2) {
                return false;
            }

            chance -= 20;
        }

        // Over 50: higher infection probability
        else if (person.getAge() > 50) {
            chance += 25;
        }

        chance = Math.max(5, Math.min(chance, 90));

        return Math.random() * 100 < chance;
    }
}
