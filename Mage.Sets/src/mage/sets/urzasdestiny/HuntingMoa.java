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
package mage.sets.urzasdestiny;

import java.util.UUID;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.DiesTriggeredAbility;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.effects.common.counter.AddCountersTargetEffect;
import mage.abilities.keyword.EchoAbility;
import mage.cards.CardImpl;
import mage.counters.CounterType;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author Backfir3
 */
public class HuntingMoa extends CardImpl<HuntingMoa> {

    public HuntingMoa(UUID ownerId) {
        super(ownerId, 109, "Hunting Moa", Rarity.UNCOMMON, new CardType[]{CardType.CREATURE}, "{2}{G}");
        this.expansionSetCode = "UDS";
        this.subtype.add("Bird");
        this.subtype.add("Beast");
        this.color.setGreen(true);
        this.power = new MageInt(3);
        this.toughness = new MageInt(2);

        this.addAbility(new EchoAbility("{2}{G}"));
        // When Hunting Moa enters the battlefield or dies, put a +1/+1 counter on target creature.
        Ability enterAbility = new EntersBattlefieldTriggeredAbility(new AddCountersTargetEffect(CounterType.P1P1.createInstance()), false);
        enterAbility.addTarget(new TargetCreaturePermanent());
        this.addAbility(enterAbility);
        // When Hunting Moa enters the battlefield or dies, put a +1/+1 counter on target creature.
        Ability diesAbility = new DiesTriggeredAbility(new AddCountersTargetEffect(CounterType.P1P1.createInstance()), false);
        diesAbility.addTarget(new TargetCreaturePermanent());
        this.addAbility(diesAbility);
    }

    public HuntingMoa(final HuntingMoa card) {
        super(card);
    }

    @Override
    public HuntingMoa copy() {
        return new HuntingMoa(this);
    }
}
