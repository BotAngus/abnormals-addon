package space.planetangus.abnormaladdon.skills.skilldata;

import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.stream.Collectors;

public class SuspendEntityData extends SkillData {
    private final double diameter;

    private final boolean includeAll;
    private final List<EntityType> exclude;
    private final List<EntityType> include;

    public SuspendEntityData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        diameter = configurationSection.getDouble("radius", 5);
        exclude = configurationSection.getStringList("exclude").stream().map(EntityType::valueOf).collect(Collectors.toList());
        include = configurationSection.getStringList("include").stream().map(EntityType::valueOf).collect(Collectors.toList());

        includeAll = exclude.isEmpty() && include.isEmpty();
    }

    public List<EntityType> getExclude() {
        return exclude;
    }

    public List<EntityType> getInclude() {
        return include;
    }

    public double getDiameter() {
        return diameter;
    }

    public boolean isIncludeAll() {
        return includeAll;
    }
}
