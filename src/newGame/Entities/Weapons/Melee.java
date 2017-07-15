package newGame.Entities.Weapons;

import newGame.Entities.Character;
import newGame.Entities.Entity;
import newGame.Entities.Item;
import newGame.IntPoint;
import newGame.MainGame;

import java.util.ArrayList;
import java.util.List;

public abstract class Melee extends Item {

    private static final double singleRange = Math.sqrt(2);

    private int damageOutput;
    private float swingRange;
    private float range;

    private int expRewardForKill;
    private int expRewardForHit;

    public Melee() {
        setName("Melee");
        damageOutput = 1;
        swingRange = 0;
        range = (float) Math.sqrt(2);

        expRewardForKill = 32;
        expRewardForHit = 3;
    }

    public int getExpRewardForKill() {
        return expRewardForKill;
    }

    public void setExpRewardForKill(int expRewardForKill) {
        this.expRewardForKill = expRewardForKill;
    }

    public int getExpRewardForHit() {
        return expRewardForHit;
    }

    public void setExpRewardForHit(int expRewardForHit) {
        this.expRewardForHit = expRewardForHit;
    }

    public float getSwingRange() {
        return swingRange;
    }

    public void setSwingRange(float swingRange) {
        this.swingRange = swingRange;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float r) {
        range = r;
    }

    public boolean isSwingWeapon() {
        return swingRange > 0;
    }

    public int getDamageOutput() {
        return damageOutput;
    }

    public void setDamageOutput(int damageOutput) {
        this.damageOutput = damageOutput;
    }

    public void attack(Entity entity) {
        onAttack(entity);

        if(entity.isDead() && getOwner() instanceof Character) {
            rewardForKill((Character) getOwner());
            entity.removeAndClean();
        } else if(getOwner() instanceof Character) {
            rewardForHit((Character) getOwner());
        }
    }

    public void attackPlayer(Character c) {
        if(isSwingWeapon() && getOwner().distance(c) <= getSwingRange())
            c.damage(getDamageOutput());
        else if(!isSwingWeapon() && getOwner().distance(c) <= singleRange)
            c.damage(getDamageOutput());

        if(c.isDead())
            c.removeAndClean();
    }

    public void attackClosest(List<Entity> entities) {
        boolean attacked = false;
        for(Entity e : entities) {
            if(e.distance(getOwner()) <= singleRange) {
                attack(e);
                attacked = true;
                break;
            }
        }

        if(!attacked)
            setTimesUsed(getTimesUsed() - 1);
    }

    public void swing(List<Entity> entities) {
        if(!isSwingWeapon() || entities.size() == 0) {
            setTimesUsed(getTimesUsed() - 1);
            return;
        }

        for(Entity e : entities) {
            IntPoint ownerPosition = getOwner().getPosition();
            if(e.distance(ownerPosition.getX(), ownerPosition.getY()) <= swingRange)
                attack(e);
        }
    }

    protected abstract void onAttack(Entity entity);

    protected abstract void rewardForKill(Character entity);

    protected abstract void rewardForHit(Character entity);

    @Override
    protected void onItemUse() {
        if(getOwner() == null)
            return;

        List<Entity> notPlayer = new ArrayList<>();
        Character character = null;
        for(Entity e : MainGame.map.getEntities()) {
            if(e instanceof Character)
                character = (Character) e;
            else
                notPlayer.add(e);
        }

        if(character == null)
            return;

        boolean attackerIsCharacter = getOwner() instanceof Character;
        if(isSwingWeapon()) {
            if(attackerIsCharacter)
                swing(notPlayer);
            else
                attackPlayer(character);
        }
        else {
            if(attackerIsCharacter)
                attackClosest(notPlayer);
            else
                attackPlayer(character);
        }
    }
}
