package space.planetangus.abnormaladdon.skills.implementations;


import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.implementations.SkillImplementation;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import space.planetangus.abnormaladdon.AbnormalsAddon;
import space.planetangus.abnormaladdon.skills.skilldata.SuspendEntityData;

import java.util.*;

public class SuspendEntitySkill extends SkillImplementation {

    private final Map<Player, Map<Entity, EntityState>> suspendedEntities = new HashMap<>();

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

                Collection<SkillData> skillDataCollection = heroHandler.getSuperhero(player)
                        .getSkillData(Skill.getSkill("SUSPENDENTITY"));
                if (skillDataCollection.isEmpty()) {
                    cancel();
                    return;
                }

                Map<Entity, EntityState> suspendedByPlayer = suspendedEntities.getOrDefault(player, new HashMap<>());

                for (SkillData data : skillDataCollection) {
                    SuspendEntityData suspendEntityData = (SuspendEntityData) data;
                    World world = player.getWorld();
                    double diameter = suspendEntityData.getDiameter();
                    Collection<Entity> entities;

                    Set<EntityType> include = suspendEntityData.getInclude();
                    Set<EntityType> exclude = suspendEntityData.getExclude();

                    if (suspendEntityData.isIncludeAll()) {
                        entities = world.getNearbyEntities(player.getLocation(),
                                diameter,
                                diameter,
                                diameter);
                    } else {
                        entities = world.getNearbyEntities(player.getLocation(),
                                diameter,
                                diameter,
                                diameter,
                                e -> include.contains(e.getType()) && !exclude.contains(e.getType()));
                    }

                    // Remove the player
                    entities.removeIf(e -> e.equals(player));

                    // New entities, freeze them
                    entities.stream().filter(entity -> !suspendedByPlayer.containsKey(entity)).forEach(entity -> {
                        suspendedByPlayer.put(entity, new EntityState(entity));
                    });
                    for (Iterator<Map.Entry<Entity, EntityState>> iterator = suspendedByPlayer.entrySet().iterator(); iterator.hasNext(); ) {
                        Map.Entry<Entity, EntityState> entry = iterator.next();
                        if (!entities.contains(entry.getKey())) {
                            Entity entity = entry.getKey();
                            entry.getValue().applyState(entity);
                            iterator.remove();
                        }
                    }
                    suspendedEntities.put(player, suspendedByPlayer);

                }
            }
        }.runTaskTimer(AbnormalsAddon.getPlugin(), 0L, 1L);
    }

    private static class EntityState {
        private final Vector velocity;
        private final Optional<Boolean> ai;
        private final boolean gravity;
        private final boolean silent;
        private final boolean invulnerable;

        private EntityState(Entity entity) {
            this.velocity = entity.getVelocity();
            entity.setVelocity(new Vector());
            this.gravity = entity.hasGravity();
            entity.setGravity(false);
            this.silent = entity.isSilent();
            entity.setSilent(true);
            this.invulnerable = entity.isInvulnerable();
            entity.setInvulnerable(true);
            if (entity instanceof LivingEntity) {
                this.ai = Optional.of(((LivingEntity) entity).hasAI());
                ((LivingEntity) entity).setAI(false);
            } else {
                this.ai = Optional.empty();
            }
        }

        public void applyState(Entity entity) {
            entity.setVelocity(velocity);
            entity.setInvulnerable(invulnerable);
            entity.setSilent(silent);
            if (entity instanceof LivingEntity) {
                ai.ifPresent(value -> ((LivingEntity) entity).setAI(value));
            }
            entity.setGravity(gravity);
        }
    }
}
