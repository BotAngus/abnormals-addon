package space.planetangus.abnormaladdon.skills;

import space.planetangus.abnormaladdon.skills.skilldata.SuspendEntityData;
import space.planetangus.abnormaladdon.skills.skilldata.SuspendFluidData;

import static me.xemor.superheroes.skills.Skill.registerSkill;

public class Skill {
    static {
        registerSkill("SUSPENDFLUID", SuspendFluidData.class);
        registerSkill("SUSPENDENTITY", SuspendEntityData.class);
    }

}
