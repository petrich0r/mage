/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */

package mage.sets.dragonsmaze;

import java.util.UUID;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.constants.TargetController;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.condition.common.ControlsPermanentCondition;
import mage.abilities.decorator.ConditionalTriggeredAbility;
import mage.abilities.effects.common.counter.AddCountersTargetEffect;
import mage.cards.CardImpl;
import mage.counters.CounterType;
import mage.filter.common.FilterControlledPermanent;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.mageobject.SubtypePredicate;
import mage.filter.predicate.permanent.ControllerPredicate;
import mage.target.Target;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author LevelX2
 */


public class UbulSarGatekeepers extends CardImpl<UbulSarGatekeepers> {

    private static final FilterControlledPermanent filter = new FilterControlledPermanent();
    private static final FilterCreaturePermanent targetFilter = new FilterCreaturePermanent("creature an opponent controls");
    static {
        filter.add(new SubtypePredicate("Gate"));
        targetFilter.add(new ControllerPredicate(TargetController.OPPONENT));
    }

    public UbulSarGatekeepers (UUID ownerId) {
        super(ownerId, 30, "Ubul Sar Gatekeepers", Rarity.COMMON, new CardType[]{CardType.CREATURE}, "{3}{B}");
        this.expansionSetCode = "DGM";
        this.subtype.add("Zombie");
        this.subtype.add("Soldier");
        this.color.setBlack(true);
        this.power = new MageInt(2);
        this.toughness = new MageInt(4);

        // Whenever Ubul Sar Gatekeepers enters the battlefield, if you control two or more Gates, target creature an opponent controls gets -2/-2 until end of turn.
        Ability ability = new ConditionalTriggeredAbility(
                new EntersBattlefieldTriggeredAbility(new AddCountersTargetEffect(CounterType.M1M1.createInstance(2))),
                new ControlsPermanentCondition(filter, ControlsPermanentCondition.CountType.MORE_THAN, 1),
                "Whenever {this} enters the battlefield, if you control two or more Gates, target creature an opponent controls gets -2/-2 until end of turn.");
        Target target = new TargetCreaturePermanent(targetFilter);
        target.setRequired(true);
        ability.addTarget(target);
        this.addAbility(ability);
    }

    public UbulSarGatekeepers (final UbulSarGatekeepers card) {
        super(card);
    }

    @Override
    public UbulSarGatekeepers copy() {
        return new UbulSarGatekeepers(this);
    }

}
