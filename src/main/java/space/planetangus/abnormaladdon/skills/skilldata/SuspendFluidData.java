package space.planetangus.abnormaladdon.skills.skilldata;

import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.configuration.ConfigurationSection;

public class SuspendFluidData extends SkillData {
    private final double radius;

    public SuspendFluidData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        radius = configurationSection.getDouble("radius", 5);
    }

    public double getRadius() {
        return radius;
    }
}
