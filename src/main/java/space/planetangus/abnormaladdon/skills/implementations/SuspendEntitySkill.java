package space.planetangus.abnormaladdon.skills.implementations;


import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.implementations.SkillImplementation;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import space.planetangus.abnormaladdon.AbnormalAddon;
import space.planetangus.abnormaladdon.Tuple2;
import space.planetangus.abnormaladdon.skills.skilldata.SuspendEntityData;

import java.util.*;

public class SuspendEntitySkill extends SkillImplementation {
    private final Map<Player, Set<Tuple2<Entity, Vector>>> suspendedEntities = new HashMap<>();

    public SuspendEntitySkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        runnable(e.getPlayer());
    }

    @EventHandler
    public void onPowerGain(PlayerGainedSuperheroEvent e) {
        runnable(e.getPlayer());
    }

    public void runnable(Player player) {
        new BukkitRunnable() {

            @Override
            public void run() {
                // check if player is still real
                if (player == null || !player.isOnline()) {
                    cancel();
                    return;
                }
                Superhero superhero = heroHandler.getSuperhero(player);
                Collection<SkillData> skillDataCollection = superhero.getSkillData(Skill.getSkill("SUSPENDENTITY"));
                if (skillDataCollection.isEmpty()) {
                    cancel();
                    return;
                }
                for (SkillData data : skillDataCollection) {
                    SuspendEntityData suspendEntityData = (SuspendEntityData) data;
                    World world = player.getWorld();
                    double diameter = suspendEntityData.getDiameter();
                    Collection<Entity> entities;

                    List<EntityType> include = suspendEntityData.getInclude();
                    List<EntityType> exclude = suspendEntityData.getExclude();

                    // Get entities in radius then add them to the suspend list
                    if (suspendEntityData.isIncludeAll()) {
                        entities = world.getNearbyEntities(player.getLocation(), diameter, diameter, diameter);
                    } else {
                        entities = world.getNearbyEntities(player.getLocation(),
                                diameter, diameter, diameter,
                                e -> !exclude.contains(e.getType()) && include.contains(e.getType()));
                    }
                    entities.removeIf(o -> o.equals(player));
                    // Get entities suspended by the player or an empty set
                    Set<Tuple2<Entity, Vector>> suspendedByPlayer = suspendedEntities.getOrDefault(player, new HashSet<>());
                    for (Iterator<Tuple2<Entity, Vector>> iterator = suspendedByPlayer.iterator(); iterator.hasNext(); ) {
                        Tuple2<Entity, Vector> element = iterator.next();
                        Entity e = element.getA();
                        if (!entities.contains(e)) {
                            e.setGravity(true);
                            e.setSilent(false);
                            e.setInvulnerable(false);
                            if (e instanceof LivingEntity) {
                                ((LivingEntity) e).setAI(true);
                            }
                            e.setVelocity(new Vector(0, 200, 0));
                            iterator.remove();
                        }
                    }
                    entities.forEach(e -> {
                        e.setGravity(false);
                        e.setSilent(true);
                        e.setInvulnerable(true);
                        if (e instanceof LivingEntity) {
                            ((LivingEntity) e).setAI(false);
                        }
                        System.out.println(e.getVelocity());
                        suspendedByPlayer.add(new Tuple2<>(e, e.getVelocity().clone()));
                        e.setVelocity(new Vector());
                    });
                    suspendedEntities.put(player, suspendedByPlayer);
                }
            }
        }.runTaskTimer(AbnormalAddon.getPlugin(), 0L, 1L);
    }
}
