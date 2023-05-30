package space.planetangus.abnormalsaddon.skills;

import space.planetangus.abnormalsaddon.skills.skilldata.SuspendEntityData;
import space.planetangus.abnormalsaddon.skills.skilldata.SuspendFluidData;

import static me.xemor.superheroes.skills.Skill.registerSkill;

public class Skill {
    static {
        registerSkill("SUSPENDFLUID", SuspendFluidData.class);
        registerSkill("SUSPENDENTITY", SuspendEntityData.class);
    }

}
