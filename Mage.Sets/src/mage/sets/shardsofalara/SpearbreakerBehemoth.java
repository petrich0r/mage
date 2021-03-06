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
package mage.sets.shardsofalara;

import java.util.UUID;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.MageInt;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.continious.GainAbilityTargetEffect;
import mage.abilities.keyword.IndestructibleAbility;
import mage.cards.CardImpl;
import mage.filter.Filter.ComparisonType;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.mageobject.PowerPredicate;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author North
 */
public class SpearbreakerBehemoth extends CardImpl<SpearbreakerBehemoth> {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("creature with power 5 or greater");

    static {
        filter.add(new PowerPredicate(ComparisonType.GreaterThan, 4));
    }

    public SpearbreakerBehemoth(UUID ownerId) {
        super(ownerId, 150, "Spearbreaker Behemoth", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{5}{G}{G}");
        this.expansionSetCode = "ALA";
        this.subtype.add("Beast");

        this.color.setGreen(true);
        this.power = new MageInt(5);
        this.toughness = new MageInt(5);

        // Spearbreaker Behemoth is indestructible.
        this.addAbility(IndestructibleAbility.getInstance());
        // {1}: Target creature with power 5 or greater is indestructible this turn.
        SimpleActivatedAbility ability = new SimpleActivatedAbility(Zone.BATTLEFIELD,
                new GainAbilityTargetEffect(IndestructibleAbility.getInstance(), Duration.EndOfTurn),
                new ManaCostsImpl("{1}"));
        ability.addTarget(new TargetCreaturePermanent(filter));
        this.addAbility(ability);
    }

    public SpearbreakerBehemoth(final SpearbreakerBehemoth card) {
        super(card);
    }

    @Override
    public SpearbreakerBehemoth copy() {
        return new SpearbreakerBehemoth(this);
    }
}
