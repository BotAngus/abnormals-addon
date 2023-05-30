package space.planetangus.abnormalsaddon.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.implementations.SkillImplementation;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;
import space.planetangus.abnormalsaddon.skills.skilldata.SuspendFluidData;

import java.util.Collection;

public class SuspendFluidSkill extends SkillImplementation {


    public SuspendFluidSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }


    @EventHandler
    public void onFlow(BlockFromToEvent event) {
        // Get all online players
        for (Player p : Bukkit.getOnlinePlayers()) {
            // Get their superhero
            Superhero superhero = heroHandler.getSuperhero(p);
            // Get all the skills that have SUSPENDFLUID as type associated with this hero
            Collection<SkillData> skillData = superhero.getSkillData(Skill.getSkill("SUSPENDFLUID"));
            // Now iterate over all the skill data
            for (SkillData data : skillData) {
                SuspendFluidData suspendFluidData = (SuspendFluidData) data;
                double radius = suspendFluidData.getRadius();
                // Cancel the flow event if the player is in range and has the skill
                if (event.getToBlock().getLocation().distance(p.getLocation()) <= radius) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
