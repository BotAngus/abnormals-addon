package space.planetangus.abnormalsaddon;

import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.implementations.SkillImplementation;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import space.planetangus.abnormalsaddon.skills.Skill;
import space.planetangus.abnormalsaddon.skills.implementations.SuspendEntitySkill;
import space.planetangus.abnormalsaddon.skills.implementations.SuspendFluidSkill;

public final class AbnormalsAddon extends JavaPlugin {

    private static Plugin plugin;

    public static Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;


        plugin.getLogger().info("Abnormal Addon for SuperHeroes 2 has been enabled");


        // Register Addon SkillData
        new Skill();

        // Register events
        registerSkills(Superheroes.getInstance().getHeroHandler());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Register the event listeners for each skill
     *
     * @param handler Instance of the hero handler obtained from the SuperHeroes2 plugin
     */
    public void registerSkills(HeroHandler handler) {
        SkillImplementation[] skills = new SkillImplementation[]{
                new SuspendFluidSkill(handler),
                new SuspendEntitySkill(handler)
        };

        for (SkillImplementation skill : skills) {
            this.getServer().getPluginManager().registerEvents(skill, this);
        }
    }

}
