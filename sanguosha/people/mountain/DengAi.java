package sanguosha.people.mountain;

import sanguosha.cards.Card;
import sanguosha.cards.Color;
import sanguosha.cards.strategy.ShunShouQianYang;
import sanguosha.cardsheap.CardsHeap;
import sanguosha.people.Nation;
import sanguosha.people.Person;
import sanguosha.skills.AfterWakeSkill;
import sanguosha.skills.Skill;
import sanguosha.skills.WakeUpSkill;

import java.util.ArrayList;

public class DengAi extends Person {
    private final ArrayList<Card> tian = new ArrayList<>();

    public DengAi() {
        super(4, Nation.WEI);
    }

    @Skill("屯田")
    public void tunTian() {
        if (!isMyRound() && launchSkill("屯田")) {
            Card c = CardsHeap.judge(this);
            if (c.color() != Color.HEART) {
                CardsHeap.getJudgeCard();
                tian.add(c);
                println(this + " now has " + tian.size() + " 田");
            }
        }
    }

    @Override
    public void lostCard() {
        tunTian();
    }

    @Override
    public void lostEquipment() {
        tunTian();
    }

    @Override
    public int numOfTian() {
        return tian.size();
    }

    @WakeUpSkill("凿险")
    @Override
    public void beginPhase() {
        if (!hasWakenUp() && tian.size() >= 3) {
            println(this + " uses 凿险");
            setMaxHP(getMaxHP() - 1);
            wakeUp();
        }
    }

    @AfterWakeSkill("急袭")
    @Override
    public boolean useSkillInUsePhase(String order) {
        if (order.equals("急袭") && hasWakenUp()) {
            println(this + " uses 急袭");
            Card c = chooseCard(tian, true);
            if (c == null) {
                return true;
            }
            ShunShouQianYang shun = new ShunShouQianYang(c.color(), c.number());
            if (shun.askTarget(this)) {
                tian.remove(c);
                CardsHeap.discard(c);
                println(this + " now has " + tian.size() + " 田");
                useCard(shun);
            }
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Card> getExtraCards() {
        return tian;
    }

    @Override
    public String getExtraInfo() {
        return tian.size() + " 田";
    }

    @Override
    public String name() {
        return "邓艾";
    }

    @Override
    public String skillsDescription() {
        return "屯田：当你于回合外失去牌后，你可以进行判定，若结果不为红桃，将判定牌置于你的武将牌上，称为田；" +
                "你计算与其他角色的距离-X（X为田\"的数量）。\n" +
                "凿险：觉醒技，准备阶段，若“田”的数量大于等于3，你减1点体力上限，然后获得“急袭”。\n" +
                (hasWakenUp() ? "急袭——你可以将一张“田”当【顺手牵羊】使用。" : "");
    }
}
