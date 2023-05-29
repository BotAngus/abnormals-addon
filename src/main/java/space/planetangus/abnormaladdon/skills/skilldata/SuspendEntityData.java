package space.planetangus.abnormaladdon.skills.skilldata;

import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.stream.Collectors;

public class SuspendEntityData extends SkillData {
    private final double diameter;

    private final boolean includeAll;
    private final Set<EntityType> exclude;
    private final Set<EntityType> include;

    public SuspendEntityData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        diameter = configurationSection.getDouble("radius", 5);
        exclude = configurationSection.getStringList("exclude").stream().map(EntityType::valueOf).collect(Collectors.toSet());
        include = configurationSection.getStringList("include").stream().map(EntityType::valueOf).collect(Collectors.toSet());

        includeAll = exclude.isEmpty() && include.isEmpty();
    }

    public Set<EntityType> getExclude() {
        return exclude;
    }

    public Set<EntityType> getInclude() {
        return include;
    }

    public double getDiameter() {
        return diameter;
    }

    public boolean isIncludeAll() {
        return includeAll;
    }
}
